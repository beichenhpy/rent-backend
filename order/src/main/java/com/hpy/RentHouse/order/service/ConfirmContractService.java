package com.hpy.RentHouse.order.service;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 10:30
 */

public interface ConfirmContractService {
    /**
     * 租客签合同确认
     * @param oid 编号
     */
    void renterConfirm(String oid);

    /**
     * 房东签合同确认
     * @param oid 编号
     */
    void ownerConfirm(String oid);


    /**
     * 更新合同路径
     * @param contract 合同路径
     * @param oid 订单编号
     */
    void updateRenterContract(String contract,String oid);
    /**
     * 更新合同路径
     * @param contract 合同路径
     * @param oid 订单编号
     */
    void updateOwnerContract(String contract,String oid);
}
