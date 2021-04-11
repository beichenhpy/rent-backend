package com.hpy.distributed.House.service.Impl;

import DO.HouseDo;
import com.hpy.distributed.House.dao.HouseMapper;
import com.hpy.distributed.House.dao.HouseStatusMapper;
import com.hpy.distributed.House.service.HouseStatusService;
import com.hpy.distributed.House.service.feign.RedisFeign;
import entity.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: beichenhpy
 * @Date: 2020/5/4 21:51
 */
@Service
public class HouseStatusServiceImpl implements HouseStatusService {

    @Autowired
    private HouseStatusMapper houseStatusMapper;
    @Autowired
    private RedisFeign redisFeign;
    @Autowired
    private HouseMapper houseMapper;

    /**
     * 更新为已出租
     * @param hid hid
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void isRented(String hid) {
        //变成不可出租 ==>isRented = 1
        Integer statusNo = houseStatusMapper.updateUnRentedToRented(hid);
        if (statusNo == 0) {
            throw new RuntimeException();
        }
        //防止脏数据
        redisFeign.del(Constant.HOUSE_H + hid);
    }

    /**
     * 更新为未出租
     * @param hid hid
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unIsRented(String hid) {
        //变成可以出租 ==>isRented = 0
        Integer statusYes = houseStatusMapper.updateRentedToUnRented(hid);
        //更新缓存使
        if (statusYes == 0) {
            throw new RuntimeException();
        }
        redisFeign.del(Constant.HOUSE_H + hid);
    }

    /**
     * 更改状态为已经核验
     *
     * @param hid 房屋编号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCheck(String hid) {
        //更新check状态
        Integer isUpdate = houseStatusMapper.updateCheck(hid);
        if (isUpdate == 0) {
            throw new RuntimeException();
        }
        //清除缓存
        redisFeign.del(Constant.HOUSE_H + hid);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCheckToUnSuccess(String hid) {
        Integer update = houseStatusMapper.updateCheckToUnSuccess(hid);
        if(update == 0){
            throw new RuntimeException();
        }
        //清除缓存
        redisFeign.del(Constant.HOUSE_H + hid);
    }

    /**
     * 更新为已发布
     * @param hid hid
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void isOnRent(String hid) {
        //isOnRent==>1
        Integer onRentNo = houseStatusMapper.updateUnOnRentToOnRent(hid);
        if (onRentNo == 0) {
            throw new RuntimeException();
        }
        //清除缓存
        redisFeign.del(Constant.HOUSE_H + hid);
    }

    /**
     * 更新为未发布
     * @param hid hid
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unisOnRent(String hid) {
        //isOnRent==>0
        Integer update = houseStatusMapper.updateOnRentToUnOnRent(hid);
        if (update == 0) {
            throw new RuntimeException();
        }
        //清除缓存
        redisFeign.del(Constant.HOUSE_H + hid);
    }
}
