package com.hpy.distributed.House.service.Impl;


import DO.HouseDo;
import DO.HouseInfoDo;
import DTO.HouseBasicDto;
import DTO.HouseChecked;
import DTO.OnRentDto;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hpy.distributed.House.dao.HouseInfoMapper;
import com.hpy.distributed.House.dao.HouseMapper;
import com.hpy.distributed.House.exception.AddHouseException;
import com.hpy.distributed.House.exception.HouseDeleteException;
import com.hpy.distributed.House.service.HouseService;
import com.hpy.distributed.House.service.feign.RedisFeign;
import com.hpy.distributed.House.service.feign.RentFeign;
import com.hpy.distributed.House.util.OssUtil;
import entity.Constant;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author: beichenhpy
 * @Date: 2020/2/27 21:18
 * <p>
 * 房屋信息业务层操作
 */
@Service
public class HouseServiceImpl implements HouseService {
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private HouseInfoMapper houseInfoMapper;
    @Autowired
    private RentFeign rentFeign;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private RedisFeign redisFeign;
    @Autowired
    private IdWorker idWorker;
    //url
    @Value("${aliyun.url}")
    private String url;

    private static final Logger logger = LoggerFactory.getLogger(HouseServiceImpl.class);



    /**
     * 通过uid查看所有的房屋信息
     * 由于房屋信息不总发生变化，所以使用redis存储
     * <p>
     * 先从redis取，如果出错，则从数据库查
     * <p>
     * 如果查询不到，则从数据库查
     * 放入redis
     * 放入失败，直接返回house
     *
     * @param uid uid
     * @return 返回这个人所有的房屋和房屋的详细信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PageInfo<HouseBasicDto> findAllHouseByUid(String uid, int page, int size) {
        PageHelper.startPage(page, size);
        List<HouseDo> houseDos = houseMapper.findHouseByUid(uid);
        PageInfo<HouseDo> pageInfo = new PageInfo<>(houseDos);

        PageInfo<HouseBasicDto> houseBasicDtoPageInfo = new PageInfo<>();
        List<HouseBasicDto> houseBasicDtos = new ArrayList<>();
        for (HouseDo houseDo : houseDos) {
            if (houseDo != null) {
                HouseBasicDto houseBasicDto = new HouseBasicDto();
                BeanUtils.copyProperties(houseDo, houseBasicDto);
                houseBasicDtos.add(houseBasicDto);
            }
        }
        houseBasicDtoPageInfo.setList(houseBasicDtos);
        houseBasicDtoPageInfo.setPageNum(page);
        houseBasicDtoPageInfo.setPageSize(size);
        houseBasicDtoPageInfo.setTotal(pageInfo.getTotal());
        return houseBasicDtoPageInfo;
    }

    /**
     * admin
     * 并且isCheck为0 管理员用的
     *
     * @return 返回house集合
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PageInfo<HouseBasicDto> findAll(int page, int size) {
        PageHelper.startPage(page, size);
        List<HouseDo> houseDos = houseMapper.findAll();
        PageInfo<HouseDo> pageInfo = new PageInfo<>(houseDos);

        PageInfo<HouseBasicDto> houseBasicDtoPageInfo = new PageInfo<>();
        List<HouseBasicDto> houseBasicDtos = new ArrayList<>();
        if (!houseDos.isEmpty()) {
            for (HouseDo houseDo : houseDos) {
                HouseBasicDto houseBasicDto = new HouseBasicDto();
                BeanUtils.copyProperties(houseDo, houseBasicDto);
                houseBasicDtos.add(houseBasicDto);

            }
            houseBasicDtoPageInfo.setList(houseBasicDtos);
            houseBasicDtoPageInfo.setPageNum(page);
            houseBasicDtoPageInfo.setPageSize(size);
            houseBasicDtoPageInfo.setTotal(pageInfo.getTotal());
        }
        return houseBasicDtoPageInfo;
    }


    /**
     * 添加新房屋
     *
     * @param houseBasicDto 房屋
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addHouse(HouseBasicDto houseBasicDto) {
        HouseDo houseDo = new HouseDo();
        BeanUtils.copyProperties(houseBasicDto, houseDo);
        //添加一个对应的房屋详细信息空信息
        HouseInfoDo houseInfoDo = new HouseInfoDo();
        houseInfoDo.setHid(houseBasicDto.getHid());
        houseInfoDo.setHouseInfoId(idWorker.nextId()+"");
        try {
            houseMapper.addHouse(houseDo);
        }catch (Exception e){
            throw new AddHouseException();
        }
        houseInfoMapper.addHouseInfo(houseInfoDo);
    }




    /**
     * 更新房屋基本信息 更改为未核验 需要在出租信息未发布的情况下才能修改
     *
     * @param houseBasicDto 房屋信息
     */
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateHouse(HouseBasicDto houseBasicDto) {
        String authorization = httpServletRequest.getHeader(Constant.AUTHOR);
        HouseDo houseDo = new HouseDo();
        BeanUtils.copyProperties(houseBasicDto, houseDo);
        Integer update = houseMapper.updateHouse(houseDo);
        if (update == 0) {
            throw new RuntimeException();
        }
        //更新出租信息
        OnRentDto onRentDto = new OnRentDto();
        onRentDto.setHid(houseBasicDto.getHid());
        onRentDto.setProvince(houseBasicDto.getProvince());
        onRentDto.setCity(houseBasicDto.getCity());
        onRentDto.setDetailPosition(houseBasicDto.getVillage()+houseBasicDto.getAddress());
        rentFeign.updateHouseRent(authorization,onRentDto);
        //清除缓存
        redisFeign.del(Constant.HOUSE_H + houseBasicDto.getHid(), Constant.RENT + houseBasicDto.getHid());
    }




    /**
     * 删除房屋 房屋未出租可以删除，删除后同时删除对应出租信息
     * <p>
     * 已租信息由于在退租后就会删除对应订单 所以不用删除
     *
     * @param hid 房屋编号
     */
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteHouseByHid(String hid) {
        HouseInfoDo houseInfoDo = houseInfoMapper.findHouseInfo(hid);
        if (houseInfoDo != null) {
            logger.info("----------------------------删除房产证图片-----------------");
            String houseCard = houseInfoDo.getHouseCard();
            if (StringUtils.isNotEmpty(houseCard)) {
                ossUtil.deleteFile(houseCard);
            }
            logger.info("----------------------------删除房屋的详细信息图片-----------------");
            String images = houseInfoDo.getImages();
            if (StringUtils.isNotEmpty(images)) {
                List<String> imageList = JSON.parseArray(images, String.class);
                ossUtil.deleteFiles(imageList);
            }
            logger.info("----------------------------删除视频-----------------");
            String video = houseInfoDo.getVideo();
            if (StringUtils.isNotEmpty(video)) {
                ossUtil.deleteFile(video);
            }
        }
        logger.info("----------------------------删除房屋-----------------");
        String authorization = httpServletRequest.getHeader(Constant.AUTHOR);
        Integer delete = houseMapper.deleteHouse(hid);
        if (delete == 0) {
            throw new HouseDeleteException();
        }
        logger.info("----------------------------删除出租信息-----------------");
        rentFeign.delOnRentNoCheck(authorization, hid);
        logger.info("----------------------------清除缓存-----------------");
        redisFeign.del(Constant.HOUSE_H + hid, Constant.HOUSEINFO + hid, Constant.RENT + hid);
        redisFeign.del(Constant.STATISTICS_Y+hid);
        Set<String> keys1 = redisFeign.keys(Constant.STATISTICS_P + "*");
        Set<String> keys2 = redisFeign.keys(Constant.STATISTICS_M + hid + "*");
        if(!keys1.isEmpty()){
            for (String key : keys1) {
                redisFeign.del(key);
            }
        }
        if(!keys2.isEmpty()){
            for (String s : keys2) {
                redisFeign.del(s);
            }
        }
    }



    /**
     * 根据房屋编号查询房屋的基本信息
     *
     * @param hid 房屋编号
     * @return 返回房屋基本信息
     */
    @Override
    public HouseBasicDto findBasicHouse(String hid) {
        HouseDo houseDo = houseMapper.findHouseBasicByHid(hid);
        HouseBasicDto houseBasicDto = new HouseBasicDto();
        if (houseDo != null) {
            BeanUtils.copyProperties(houseDo, houseBasicDto);
            return houseBasicDto;
        }
        return null;
    }

    /**
     * 修改省市县信息时，同时修改房屋信息的对应信息
     *
     * @param newName 新名字
     * @param oldName 旧名字
     * @param type    类型 province/city/village
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateProvinceOrCityOrVillage(String newName, String oldName, String type) {
        if ("province".equals(type)) {
            houseMapper.updateProvince(newName, oldName);
        } else if ("city".equals(type)) {
            houseMapper.updateCity(newName, oldName);
        } else {
            houseMapper.updateVillage(newName, oldName);
        }
        //批量清空redis
        Set<String> keys = redisFeign.keys(Constant.HOUSE_H + "*");
        if (!keys.isEmpty()) {
            for (String key : keys) {
                redisFeign.del(key);
            }
        }

    }




    //拿到房屋
    @Override
    public List<HouseChecked> findCheckedHouse(String uid) {
        List<HouseChecked> checkedList = new ArrayList<>();
        List<HouseDo> allChecked = houseMapper.findAllChecked(uid);
        if (!allChecked.isEmpty()) {
            for (HouseDo houseDo : allChecked) {
                HouseChecked houseChecked = new HouseChecked();
                houseChecked.setLabel(houseDo.getVillage() +
                        houseDo.getAddress() +
                        houseDo.getBuilding() + "栋" +
                        houseDo.getUnit() + "单元" +
                        houseDo.getHouseNum() + "号");
                houseChecked.setValue(houseDo.getHid());
                checkedList.add(houseChecked);
            }
        }
        return checkedList;
    }

    //查询已经出租或发布的房屋
    @Override
    public Boolean findHouseByProvince(String province) {
        Integer houseByProvince = houseMapper.findHouseByProvince(province);

        return houseByProvince != 0;
    }

    @Override
    public Boolean findHouseByCity(String city) {
        Integer houseByCity = houseMapper.findHouseByCity(city);

        return houseByCity != 0;
    }

    @Override
    public Boolean findHouseByVillage(String village) {
        Integer houseByVillage = houseMapper.findHouseByVillage(village);

        return houseByVillage != 0;
    }


}
