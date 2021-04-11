package com.hpy.RentHouse.order.service.Impl;

import DO.*;
import DTO.*;
import Query.SignQuery;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hpy.RentHouse.order.dao.BillMapper;
import com.hpy.RentHouse.order.dao.DepositMapper;
import com.hpy.RentHouse.order.dao.OrderMapper;
import com.hpy.RentHouse.order.dao.PriceInfoMapper;
import com.hpy.RentHouse.order.exception.OrderCancelException;
import com.hpy.RentHouse.order.exception.OrderDeleteException;
import com.hpy.RentHouse.order.exception.OrderException;
import com.hpy.RentHouse.order.service.feign.*;
import com.hpy.RentHouse.order.service.OrderService;
import com.hpy.RentHouse.order.utils.OssUtil;
import entity.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: beichenhpy
 * @Date: 2020/3/13 18:04
 * 已租订单业务层
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private HouseFeign houseFeign;
    @Autowired
    private RentFeign rentFeign;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    private BillMapper billMapper;
    @Autowired
    private PriceInfoMapper priceInfoMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private DepositMapper depositMapper;
    @Autowired
    private RedisFeign redisFeign;
    @Autowired
    private FileFeign fileFeign;
    @Autowired
    private CommentFeign commentFeign;
    @Autowired
    private OssUtil ossUtil;
    //租房子目录
    @Value("${aliyun.url}")
    private String url;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    /**
     * 添加已租信息 订单
     * 添加完后应该给房东发送一个短信，提示房子有人下单
     * 先进行电子签名是否存在的判断，不存在不能继续
     * <p>
     * 先进行电子签名的添加
     * 为了安全使用完后租客用户的电子签名置为空
     * 同时创建bill 每次为一个月
     * 都使用 uid作为key 方便使用
     * <p>
     * 用户只需输入想要租的时间即可
     * 通过自动计算出结束时间
     * 先进行冻结操作，防止其他用户下单
     * 然后根据查询到的出租信息补充订单信息
     * 进行和合同的生成
     * 创建一个账单，用于支付押金
     * 创建一个押金详情，用于记录押金的详细内容，便于租客管理押金
     * 修改房屋状态为已出租
     * 发送短信提醒房东房屋已出租
     *
     * @param orderDto 已租信息
     */
    //GlobalTransactional 为seata控制分布式事务的注解
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addOrder(OrderDto orderDto) {
        //远程调用出租服务查询出租信息
        OnRentForOrderDto onRentForOrderDto = rentFeign.findOnRentInfoForOrderByHid(orderDto.getHid());
        if (onRentForOrderDto == null){
            throw new OrderException();
        }
        //远程调用出租服务将出租信息状态改为已发布
        rentFeign.frozenOnRent(orderDto.getHid());
        //将房东的编号放入对应的订单
        orderDto.setOwnerUid(onRentForOrderDto.getUid());
        //远程调用用户服务查询房东和租客的信息
        UserDto renterDto = userFeign.findUserById(orderDto.getRenterUid());
        UserDto ownerDto = userFeign.findUserById(orderDto.getOwnerUid());
        //判断是否存在
        if (renterDto == null || ownerDto == null){
            throw new RuntimeException();
        }
        //将UserDto转换为UserOrderDto
        UserOrderDto renter = new UserOrderDto();
        UserOrderDto owner = new UserOrderDto();
        BeanUtils.copyProperties(renterDto,renter);
        BeanUtils.copyProperties(ownerDto,owner);
        //格式化日期
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.setTime(new Date());
        Date startTime = calendar.getTime();
        calendar.add(Calendar.MONTH, orderDto.getMonths());
        Date endTime = calendar.getTime();
        //多久交一次钱 1/2/3/6
        orderDto.setBillTime(onRentForOrderDto.getTime());
        orderDto.setIsFinish(0);
        orderDto.setPrice(onRentForOrderDto.getPrice());
        orderDto.setDeposit(onRentForOrderDto.getDeposit() + onRentForOrderDto.getKeyDeposit());
        //添加合同信息
        ContractDto contract = new ContractDto(
                orderDto.getOid(),
                owner.getIdCardInfo().getRealName(),
                renter.getIdCardInfo().getRealName(),
                owner.getIdCardInfo().getIdNum(),
                renter.getIdCardInfo().getIdNum(),
                owner.getPhone(),
                renter.getPhone(),
                sdf.format(startTime),
                sdf.format(endTime),
                orderDto.getPrice().toString(),
                orderDto.getDeposit().toString(),
                sdf.format(startTime),
                owner.getUid(),
                renter.getUid()
        );
        //远程调用文件服务插入合同信息到数据库
        fileFeign.addContract(contract);
        //添加订单
        OrderDo orderDo = new OrderDo();
        BeanUtils.copyProperties(orderDto,orderDo);
        orderDo.setStartTime(startTime);
        orderDo.setEndTime(endTime);
        orderMapper.addOrder(orderDo);
        //添加押金详情
        DepositInfoDo depositInfoDo = new DepositInfoDo();
        depositInfoDo.setOid(orderDto.getOid());
        //月押金
        depositInfoDo.setBasicDeposit(onRentForOrderDto.getDeposit());
        depositInfoDo.setDid(idWorker.nextId()+"");
        //钥匙押金
        depositInfoDo.setKeyDeposit(onRentForOrderDto.getKeyDeposit());
        depositInfoDo.setRenterUid(orderDto.getRenterUid());
        depositInfoDo.setOwnerUid(owner.getUid());
        //放入数据库
        depositMapper.addDepositInfo(depositInfoDo);
        //添加账单 第一次要交押金
        BillDo billDo = new BillDo(
                idWorker.nextId() + "",
                startTime,
                orderDto.getOid(),
                orderDto.getDeposit().doubleValue()+orderDto.getPrice().doubleValue(),
                0,
                0,
                null,
                null
        );
        //插入到数据库
        billMapper.addBill(billDo);
        //添加价格详情
        PriceInfoDo priceInfoDo = new PriceInfoDo();
        priceInfoDo.setElect(0.0);
        priceInfoDo.setWater(0.0);
        priceInfoDo.setBid(billDo.getBid());
        priceInfoDo.setBasic(orderDto.getDeposit()+orderDto.getPrice());
        priceInfoDo.setPriceId(idWorker.nextId()+"");
        priceInfoMapper.addPriceInfo(priceInfoDo);
        //远程调用房屋服务添加统计信息
        StatisticsDto statisticsDto = new StatisticsDto();
        statisticsDto.setCreateTime(sdf.format(startTime));
        statisticsDto.setHid(orderDto.getHid());
        statisticsDto.setPrice(orderDto.getPrice());
        statisticsDto.setUid(orderDto.getOwnerUid());
        statisticsDto.setOid(orderDto.getOid());
        statisticsDto.setWaterCount(0);
        statisticsDto.setElectCount(0);
        statisticsDto.setElectPrice("0");
        statisticsDto.setWaterPrice("0");
        houseFeign.addStatistics(statisticsDto);
        //远程调用修改房屋状态为已出租
        houseFeign.isRented(orderDto.getHid());
        //调用留言服务系统通知房东租下房屋
        Comment comment = new Comment();
        comment.setReciever(owner.getUid());
        String content = "您的房屋被租下了，请及时联系租户,租户电话为:"+renter.getPhone();
        comment.setContent(content);
        commentFeign.adminSend(comment);
    }


    /**
     * 退租 将订单变成完成状态
     * 出租信息解冻 在首页又可以查看到
     *
     * @param orderDto 已租信息
     */
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void finishOrder(OrderDto orderDto,Integer isBreak) {
        if(isBreak == 1){
            Integer update = depositMapper.breakRule(orderDto.getOid());
            if(update == 0){
                throw new RuntimeException();
            }
        }
        Integer update = orderMapper.updateToIsFinish(orderDto.getOid());
        if (update == 0) {
            throw new RuntimeException();
        }
        logger.info("------------------------解冻出租信息-----------------------");
        rentFeign.unfrozenOnRent(orderDto.getHid());
        logger.info("------------------------修改房屋状态-----------------------");
        houseFeign.unisRented(orderDto.getHid());
        logger.info("------------------------缓存清空-----------------------");
        redisFeign.del(Constant.ORDER_R + orderDto.getOid(), Constant.ORDER_O + orderDto.getOid());
    }

    /**
     * 根据订单编号删除订单
     *
     * @param oid 订单编号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delOrdersByOid(String oid, String type) {
        if (Constant.RENTER.equals(type)) {
            logger.info("------------------------renter-----------------------");
            logger.info("------------------------删除合同文件-----------------------");
            String contract = orderMapper.findContractRenter(oid);
            ossUtil.deleteFile(contract);
            logger.info("------------------------删除订单-----------------------");
            Integer delete = orderMapper.delOrdersByOid(oid);
            if (delete == 0) {
                throw new OrderDeleteException();
            }
            redisFeign.del(Constant.ORDER_R + oid);
        } else if (Constant.OWNER.equals(type)) {
            logger.info("------------------------owner-----------------------");
            logger.info("------------------------删除合同文件-----------------------");
            String contract = orderMapper.findContractOwner(oid);
            ossUtil.deleteFile(contract);
            logger.info("------------------------删除订单-----------------------");
            Integer delete = orderMapper.delOrdersByOidOwner(oid);
            if (delete == 0) {
                throw new OrderDeleteException();
            }
            redisFeign.del(Constant.ORDER_O + oid);
        }

        redisFeign.del(Constant.BILLS_R + oid, Constant.BILLS_O + oid);

    }

    /**
     * 租客取消订单 同时删除房东的的订单
     *
     * @param oid 订单编号
     */
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelOrder(String oid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        OrderDo orderDo = orderMapper.findOrderByOid(oid);
        Integer deleteRenter = orderMapper.cancelOrder(oid);
        Integer deleteOwner = orderMapper.cancelOwnerOrder(oid);
        if (deleteRenter == 0 || deleteOwner == 0) {
            throw new OrderCancelException();
        }
        logger.info("------------------------解冻出租信息-----------------------");
        rentFeign.unfrozenOnRent(orderDo.getHid());
        logger.info("------------------------修改房屋状态-----------------------");
        houseFeign.unisRented(orderDo.getHid());
        logger.info("------------------------删除统计信息-----------------------");
        houseFeign.deleteStatistic(oid,orderDo.getHid(),sdf.format(orderDo.getStartTime()),orderDo.getOwnerUid());
        redisFeign.del(Constant.ORDER_R + oid, Constant.ORDER_O + oid);
        redisFeign.del(Constant.BILLS_R + oid, Constant.BILLS_O + oid);
    }


    /**
     * 根据用户编号查询所有订单
     *
     * @param uid 租客/房东的用户编号
     * @return 返回订单集合
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PageInfo<OrderDto> findAllOrder(String uid, String type, int page, int size) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<OrderDto> orderDtos = new ArrayList<>();
        List<OrderDo> orderDos = new ArrayList<>();
        PageInfo<OrderDo> pageInfo;
        //租客查询
        if (Constant.RENTER.equals(type)) {
            logger.info("------------------------renter-----------------------");
            PageHelper.startPage(page,size);
            orderDos = orderMapper.findAllOrderByRenter(uid);
        } else if (Constant.OWNER.equals(type)) {
            //房东查询
            logger.info("------------------------owner-----------------------");
            PageHelper.startPage(page,size);
            orderDos = orderMapper.findAllOrderByOwner(uid);
        }
        pageInfo = new PageInfo<>(orderDos);
        //转换
        PageInfo<OrderDto> orderDtoPageInfo = new PageInfo<>();
        if (!orderDos.isEmpty()){
            for (OrderDo orderDo : orderDos) {
                OrderDto orderDto = new OrderDto();
                BeanUtils.copyProperties(orderDo,orderDto);
                orderDto.setStartTime(sdf.format(orderDo.getStartTime()));
                orderDto.setEndTime(sdf.format(orderDo.getEndTime()));
                orderDtos.add(orderDto);
            }
            orderDtoPageInfo.setList(orderDtos);
            orderDtoPageInfo.setPageNum(page);
            orderDtoPageInfo.setPageSize(size);
            orderDtoPageInfo.setTotal(pageInfo.getTotal());
        }
        return orderDtoPageInfo;
    }


    /**
     * 根据订单编号查询订单信息
     *
     * @param oid 订单编号
     * @return 返回订单信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderDto findOrderByOid(String oid, String type) {
        OrderDto orderDto;
        String authorization = httpServletRequest.getHeader(Constant.AUTHOR);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //租客查询
        if (Constant.RENTER.equals(type)) {
            logger.info("------------------------renter-----------------------");
            String orderJson = redisFeign.get(Constant.ORDER_R + oid);
            if (StringUtils.isNotEmpty(orderJson)){
                orderDto = JSON.parseObject(orderJson, OrderDto.class);
            }else {
                OrderDo orderDo = orderMapper.findOrderByOid(oid);
                logger.info("------------------------查询房东信息-----------------------");
                UserDto userById = userFeign.findUserById(orderDo.getOwnerUid());
                UserOrderDto userOrderDto = new UserOrderDto();
                BeanUtils.copyProperties(userById,userOrderDto);
                userOrderDto.setProfilePhoto(url + userOrderDto.getProfilePhoto());
                logger.info("------------------------查询房屋信息-----------------------");
                Message house = houseFeign.findBasicHouse(authorization,orderDo.getHid());
                if(house.getCode() != 200){
                    throw new RuntimeException();
                }
                HouseBasicDto houseBasicDto = JSON.parseObject(JSON.toJSONString(house.getData()), HouseBasicDto.class);
                orderDto = new OrderDto();
                BeanUtils.copyProperties(orderDo,orderDto);
                orderDto.setStartTime(sdf.format(orderDo.getStartTime()));
                orderDto.setEndTime(sdf.format(orderDo.getEndTime()));
                if(orderDto.getContract() != null){
                    orderDto.setContract(url + orderDto.getContract());
                }
                orderDto.setHouse(houseBasicDto);
                orderDto.setUser(userOrderDto);
                redisFeign.set(Constant.ORDER_R + oid,JSON.toJSONString(orderDto));
            }
            return orderDto;
        }else if (Constant.OWNER.equals(type)) {
            logger.info("------------------------owner-----------------------");
            String orderJson = redisFeign.get(Constant.ORDER_O + oid);
            if (StringUtils.isNotEmpty(orderJson)){
                orderDto = JSON.parseObject(orderJson, OrderDto.class);
            }else {
                OrderDo orderDo = orderMapper.findOwnerOrderByOid(oid);
                logger.info("------------------------查询租客信息-----------------------");
                UserDto userById = userFeign.findUserById(orderDo.getRenterUid());
                UserOrderDto userOrderDto = new UserOrderDto();
                BeanUtils.copyProperties(userById,userOrderDto);
                userOrderDto.setProfilePhoto(url + userOrderDto.getProfilePhoto());
                logger.info("------------------------查询房屋信息-----------------------");
                Message house = houseFeign.findBasicHouse(authorization,orderDo.getHid());
                if(house.getCode() != 200){
                    throw new RuntimeException();
                }
                HouseBasicDto houseBasicDto = JSON.parseObject(JSON.toJSONString(house.getData()), HouseBasicDto.class);
                orderDto = new OrderDto();
                BeanUtils.copyProperties(orderDo,orderDto);
                orderDto.setStartTime(sdf.format(orderDo.getStartTime()));
                orderDto.setEndTime(sdf.format(orderDo.getEndTime()));
                if(orderDto.getContract() != null){
                    orderDto.setContract(url + orderDto.getContract());
                }
                orderDto.setHouse(houseBasicDto);
                orderDto.setUser(userOrderDto);
                redisFeign.set(Constant.ORDER_O + oid,JSON.toJSONString(orderDto));
            }
            return orderDto;
        }
        return null;
    }






}
