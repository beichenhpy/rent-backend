package com.hpy.RentHouse.order.service.Impl;

import Query.SignQuery;
import com.hpy.RentHouse.order.dao.OrderMapper;
import com.hpy.RentHouse.order.service.ConfirmContractService;
import com.hpy.RentHouse.order.service.feign.FileFeign;
import com.hpy.RentHouse.order.service.feign.RedisFeign;
import entity.Constant;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 10:30
 */
@Service
public class ConfirmContractServiceImpl implements ConfirmContractService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private FileFeign fileFeign;
    @Autowired
    private RedisFeign redisFeign;
    /**
     * 租客签合同确认
     * @param oid 编号
     */
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void renterConfirm(String oid) {
        orderMapper.renterConfirm(oid);
        SignQuery signQuery = new SignQuery();
        signQuery.setType("renter");
        signQuery.setOid(oid);
        fileFeign.signContract(signQuery);
        redisFeign.del(Constant.ORDER_O+oid,Constant.ORDER_R+oid);
    }

    /**
     * 房东签合同确认
     * @param oid 编号
     */
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void ownerConfirm(String oid) {
        orderMapper.ownerConfirm(oid);
        SignQuery signQuery = new SignQuery();
        signQuery.setType("owner");
        signQuery.setOid(oid);
        fileFeign.signContract(signQuery);
        redisFeign.del(Constant.ORDER_O+oid,Constant.ORDER_R+oid);
    }


    /**
     * 更新租客合同
     * @param contract 合同路径
     * @param oid 订单编号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateRenterContract(String contract, String oid) {
        orderMapper.updateContractRenter(contract, oid);
        redisFeign.del(Constant.ORDER_R + oid);
    }

    /**
     * 更新房东合同
     * @param contract 合同路径
     * @param oid 订单编号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOwnerContract(String contract, String oid) {
        orderMapper.updateContractOwner(contract, oid);
        redisFeign.del(Constant.ORDER_O + oid);
    }
}
