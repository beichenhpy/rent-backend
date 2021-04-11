package com.hpy.RentHouse.order.service.Impl;

import com.hpy.RentHouse.order.dao.OrderMapper;
import com.hpy.RentHouse.order.service.DownLoadContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 10:25
 */
@Service
public class DownLoadContractServiceImpl implements DownLoadContractService {

    @Autowired
    private OrderMapper orderMapper;
    //租房子目录
    @Value("${aliyun.url}")
    private String url;

    /**
     * 查询下载合同
     * @param oid 订单
     * @return 合同连接
     */
    @Override
    public String downloadContractRenter(String oid) {
        return url + orderMapper.findContractRenter(oid);
    }

    /**
     * 查询下载合同
     * @param oid 订单
     * @return 合同连接
     */
    @Override
    public String downloadContractOwner(String oid) {
        return url + orderMapper.findContractOwner(oid);
    }
}
