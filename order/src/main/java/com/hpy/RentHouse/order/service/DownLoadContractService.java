package com.hpy.RentHouse.order.service;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 10:24
 */

public interface DownLoadContractService {
    /**
     * 根据订单查询下载合同
     * @param oid 订单
     * @return 返回合同链接
     */
    String downloadContractRenter(String oid);
    /**
     * 根据订单查询下载合同
     * @param oid 订单
     * @return 返回合同链接
     */
    String downloadContractOwner(String oid);
}
