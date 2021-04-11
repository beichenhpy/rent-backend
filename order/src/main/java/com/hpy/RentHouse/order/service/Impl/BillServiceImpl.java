package com.hpy.RentHouse.order.service.Impl;

import DO.BillDo;
import DO.OrderDo;
import DO.PriceInfoDo;
import DTO.*;
import Query.HouseRecordQuery;
import com.alibaba.fastjson.JSON;
import com.hpy.RentHouse.order.dao.BillMapper;
import com.hpy.RentHouse.order.dao.OrderMapper;
import com.hpy.RentHouse.order.dao.PriceInfoMapper;
import com.hpy.RentHouse.order.model.PriceCount;
import com.hpy.RentHouse.order.service.BillService;
import com.hpy.RentHouse.order.service.feign.AdminFeign;
import com.hpy.RentHouse.order.service.feign.HouseFeign;
import com.hpy.RentHouse.order.service.feign.RedisFeign;
import entity.Constant;
import entity.Message;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author: beichenhpy
 * @Date: 2020/3/14 14:58
 */
@Service
public class BillServiceImpl implements BillService {
    @Autowired
    private BillMapper billMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PriceInfoMapper priceInfoMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisFeign redisFeign;
    @Autowired
    private HouseFeign houseFeign;
    @Autowired
    private AdminFeign adminFeign;
    @Autowired
    private HttpServletRequest httpServletRequest;

    private static final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);

    /**
     * 房东添加费用详情 同时新建一个新的账单
     * 此时插入到 ownerprice和billowner 表 触发器同步更新price和bill表
     * 将账单放入缓存 bills+oid形式
     * <p>
     * 自动计算出 price的基本费用为 月租*几个月交一次
     * 然后根据电费和水费的单价计算方式。计算出水费电费 放入bill中
     * 然后添加bill
     * 根据bill的bid添加priceInfo账单详情
     *
     * @param priceCount 费用详情
     */
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addPriceInfo(PriceCount priceCount, String oid, String uid) {
        String authorization = httpServletRequest.getHeader("Authorization");
        //根据订单编号查询到订单信息
        logger.info("-----------------------查询订单信息---------------------------");
        OrderDo orderDo = orderMapper.findOrderByOid(oid);
        PriceInfoDo priceInfoDo = new PriceInfoDo();
        //基本费用为每个月月租*billTime 就是几个月交一次的总共的月租价格
        priceInfoDo.setBasic(orderDo.getPrice() * orderDo.getBillTime());
        //添加账单
        /*根据规则计算出水费和电费*/
        logger.info("-----------------------查询房屋信息---------------------------");
        HouseDto houseDto = houseFeign.findHouseHid(orderDo.getHid());
        if (houseDto == null) {
            throw new RuntimeException();
        }
        Integer lastWater = houseDto.getHouseInfo().getWaterRecord();
        Integer lastCar = houseDto.getHouseInfo().getCarRecord();
        Integer lastHouse = houseDto.getHouseInfo().getHouseRecord();
        Integer newCarRecord = priceCount.getCarRecord() - lastCar;
        Integer water = priceCount.getWaterRecord() - lastWater;
        Integer newHouseRecord = priceCount.getHouseRecord() - lastHouse;
        Integer elect = newCarRecord + newHouseRecord;
        logger.info("-----------------------查询单价信息---------------------------");
        List<UnitPriceDto> unitPrices = adminFeign.findUnitPrice(houseDto.getProvince(), houseDto.getCity());
        for (UnitPriceDto unitPrice : unitPrices) {
            logger.info("------------------------计算电费开始--------------------");
            if (Constant.ELECT.equals(unitPrice.getType())) {
                logger.info("------------------电费单价:{}", unitPrice);
                if (elect <= unitPrice.getFirstStage()) {
                    priceInfoDo.setElect(elect * unitPrice.getFirstStagePrice());
                    logger.info("-----------------第一阶段：{}", priceCount);
                } else if (elect > unitPrice.getFirstStage() &&
                        elect <= unitPrice.getSecondStage()) {
                    priceInfoDo.setElect(unitPrice.getFirstStage() * unitPrice.getFirstStagePrice() +
                            (elect - unitPrice.getFirstStage()) * unitPrice.getSecondStagePrice());
                    logger.info("-----------------第二阶段：{}", priceCount);
                } else {
                    priceInfoDo.setElect(unitPrice.getFirstStage() * unitPrice.getFirstStagePrice() +
                            unitPrice.getSecondStage() * unitPrice.getSecondStagePrice() +
                            (elect - unitPrice.getSecondStage()) * unitPrice.getThirdStagePrice());
                    logger.info("-----------------第三阶段：{}", priceCount);
                }
            } else {
                logger.info("------------------------计算水费开始--------------------");
                if (water <= unitPrice.getFirstStage()) {
                    priceInfoDo.setWater(water * unitPrice.getFirstStagePrice());
                } else if (water > unitPrice.getFirstStage() &&
                        water <= unitPrice.getSecondStage()) {
                    priceInfoDo.setWater(unitPrice.getFirstStage() * unitPrice.getFirstStagePrice() +
                            (water - unitPrice.getFirstStage()) * unitPrice.getSecondStagePrice());
                } else {
                    priceInfoDo.setWater(
                                    unitPrice.getFirstStage() * unitPrice.getFirstStagePrice() +
                                    unitPrice.getSecondStage() * unitPrice.getSecondStagePrice() +
                                    (water - unitPrice.getSecondStage()) * unitPrice.getThirdStagePrice());
                }
            }
        }
        logger.info("-----------------------新建账单信息---------------------------");
        Date createTime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat df = new DecimalFormat("0.00 ");
        Double price = priceInfoDo.getBasic() + priceInfoDo.getWater() + priceInfoDo.getElect();
        String priceS = df.format(price);
        //更新bill的支付金额
        BillDo billDo = new BillDo(idWorker.nextId() + "",
                createTime,
                oid,
                Double.parseDouble(priceS),
                0,
                0,
                null,
                null);
        billMapper.addBill(billDo);
        //设置bid
        logger.info("-----------------------新建详细信息---------------------------");
        priceInfoDo.setBid(billDo.getBid());
        priceInfoDo.setPriceId(idWorker.nextId()+"");
        //添加到数据库
        priceInfoMapper.addPriceInfo(priceInfoDo);
        logger.info("-----------------------更新房屋读数---------------------------");
        HouseRecordQuery houseRecordQuery = new HouseRecordQuery();
        houseRecordQuery.setCarRecord(priceCount.getCarRecord());
        houseRecordQuery.setWaterRecord(priceCount.getWaterRecord());
        houseRecordQuery.setHouseRecord(priceCount.getHouseRecord());
        houseRecordQuery.setHid(houseDto.getHid());
        houseRecordQuery.setHouseInfoId(houseDto.getHouseInfo().getHouseInfoId());
        houseFeign.updateHouseRecord(authorization,houseRecordQuery);
        logger.info("------------------------添加统计信息-----------------------");
        StatisticsDto statisticsDto = new StatisticsDto();
        statisticsDto.setHid(orderDo.getHid());
        statisticsDto.setCreateTime(sdf.format(createTime));
        statisticsDto.setPrice(priceInfoDo.getBasic());
        statisticsDto.setElectCount(elect);
        statisticsDto.setWaterCount(water);
        statisticsDto.setElectPrice(df.format(priceInfoDo.getElect()));
        statisticsDto.setWaterPrice(df.format(priceInfoDo.getWater()));
        statisticsDto.setUid(uid);
        statisticsDto.setOid(oid);
        houseFeign.addStatistics(statisticsDto);
        //删除缓存
        logger.info("-----------------------清除缓存---------------------------");
        redisFeign.del(
                Constant.ORDER_R + orderDo.getRenterUid(),
                Constant.ORDER_O + uid,
                Constant.BILLS_R + oid,
                Constant.BILLS_O + oid
        );
    }

    /**
     * 根据订单编号查询账单
     * 先从缓存中拿，如果没有就从数据库拿，然后更新缓存
     *
     * @param oid  订单编号
     * @param type renter/owner
     * @return 返回账单集合
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<BillDto> findBillByOid(String oid, String type) {
        List<BillDto> billDtos;
        //租客查询账单
        if (Constant.RENTER.equals(type)) {
            String billJson = redisFeign.get(Constant.BILLS_R + oid);
            billDtos = JSON.parseArray(billJson, BillDto.class);
        } else {
            //房东查询
            String billJson = redisFeign.get(Constant.BILLS_O + oid);
            billDtos = JSON.parseArray(billJson, BillDto.class);
        }
        if (billDtos != null && !billDtos.isEmpty()) {
            return billDtos;
        } else {
            //数据库查询
            if (Constant.RENTER.equals(type)) {
                //租客
                List<BillDo> billDos = billMapper.findBillByOidRenter(oid);
                billDtos = BillConvert(billDos);
                redisFeign.set(Constant.BILLS_R + oid, JSON.toJSONString(billDtos));
            } else {
                //房东
                List<BillDo> billDos = billMapper.findBillByOidOwner(oid);
                billDtos = BillConvert(billDos);
                redisFeign.set(Constant.BILLS_O + oid, JSON.toJSONString(billDtos));
            }

        }
        return billDtos;

    }

    /**
     * 将billDos==>billDto
     *
     * @param billDos billDos
     * @return List<BillDto>
     */
    private List<BillDto> BillConvert(List<BillDo> billDos) {
        DecimalFormat df = new DecimalFormat("0.00 ");
        List<BillDto> billDtos = null;
        if (billDos != null) {
            billDtos = new ArrayList<>();
            for (BillDo billDo : billDos) {
                BillDto billDto = new BillDto();
                //取出priceInfoDo 转换成 priceInfoDto
                PriceInfoDto priceInfoDto = null;
                PriceInfoDo priceInfoDo = billDo.getPriceInfoDo();
                if (priceInfoDo != null) {
                    priceInfoDto = new PriceInfoDto();
                    BeanUtils.copyProperties(priceInfoDo, priceInfoDto);
                    priceInfoDto.setElect(df.format(priceInfoDo.getElect()));
                    priceInfoDto.setWater(df.format(priceInfoDo.getWater()));
                }
                BeanUtils.copyProperties(billDo, billDto);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                billDto.setCreateTime(sdf.format(billDo.getCreateTime()));
                billDto.setPriceInfo(priceInfoDto);
                billDto.setPrice(df.format(billDo.getPrice()));
                billDtos.add(billDto);
            }
        }
        return billDtos;
    }

    /**
     * 租客确认支付成功 清除order_r为了更新firstPay
     * 更新数据库，更新缓存/通过删除更新，因为bills是list
     *
     * @param bid 账单编号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void renterCheck(String bid, String oid) {
        //先判断是否签合同
        Integer renterCheck = billMapper.renterCheck(bid);
        if (renterCheck == 0) {
            throw new RuntimeException();
        }
        orderMapper.updateIsFirstPay(oid);
        //清空缓存
        redisFeign.del(Constant.BILLS_R + oid,
                Constant.BILLS_O + oid, Constant.ORDER_R + oid);

    }

    /**
     * 房东确认支付成功
     * 确认后ownerCheck置为1
     *
     * @param bid 账单编号 更新缓存
     * @param oid 订单编号 更新缓存
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void ownerCheck(String bid, String oid) {
        Integer ownerCheck = billMapper.ownerCheck(bid);
        if (ownerCheck == 0) {
            throw new RuntimeException();
        }
        //清空缓存
        redisFeign.del(Constant.BILLS_R + oid,
                Constant.BILLS_O + oid);
    }

    /**
     * 查询所有未支付的账单
     *
     * @param oid 订单编号
     * @return 返回订单对应的未支付账单数量
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer findCountNoPay(String oid) {
        Integer countNoPayRenter = billMapper.findCountNoPayRenter(oid);
        Integer countNoPayOwner = billMapper.findCountNoPayOwner(oid);
        if (countNoPayOwner != 0 || countNoPayRenter != 0) {
            return 1;
        }
        return 0;
    }


    @Override
    public BillDto findBillByBid(String bid) {
        BillDo billByBid = billMapper.findBillByBid(bid);
        BillDto billDto = new BillDto();
        BeanUtils.copyProperties(billByBid, billDto);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        billDto.setCreateTime(sdf.format(billByBid.getCreateTime()));
        billDto.setPrice(Double.toString(billByBid.getPrice()));
        return billDto;
    }


    @Override
    public void updateReceipt(String receipt, String bid) {
        billMapper.updateReceipt(receipt, bid);
        Set<String> keys = redisFeign.keys(Constant.BILLS_R + "*");
        if (!keys.isEmpty()) {
            for (String key : keys) {
                redisFeign.del(key);
            }
        }
    }


}
