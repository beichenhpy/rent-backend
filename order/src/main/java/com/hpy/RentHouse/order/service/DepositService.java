package com.hpy.RentHouse.order.service;

import DO.DepositInfoDo;
import DTO.DepositInfoDto;
import DTO.MyDepositDto;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/3/19 21:54
 */

public interface DepositService {

    /**
     * 租客确认押金已经退回
     * @param did 订单编号d
     */
    void confirmBack(String did);

    void ownerCheck(String did);

    /**
     * 查询订单对应的押金详情
     * @param oid 订单编号
     *
     * @return 押金详情
     */
    DepositInfoDto findDeposit(String oid);

    /**
     * 申请退还押金，给房东发送短信 带上对应的押金金额
     * @param did 编号
     */
    void askBackDeposit(String did);

    List<MyDepositDto> findDepositByUid(String uid,String type);


    //更新收据
    void updateReceipt(String did,String receipt);
    //删除收据
    void deleteDeposit(String did);
}
