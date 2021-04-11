package com.hpy.RentHouse.order.service;

import DO.UnitPriceDo;
import DTO.UnitPriceDto;
import com.github.pagehelper.PageInfo;
import com.hpy.RentHouse.order.model.PriceCount;
import DTO.BillDto;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/3/14 14:56
 */

public interface BillService {

    /**
     * 房东添加账单金额详情
     * @param priceCount 费用详情
     * @param oid oid
     */
    void addPriceInfo(PriceCount priceCount, String oid, String uid);


    /**
     * 根据订单编号查询账单
     * @param oid 订单编号
     * @return 返回账单集合
     */
    List<BillDto> findBillByOid(String oid, String type);

    /**
     * 租客确认支付成功
     * @param bid 账单编号
     */
    void renterCheck(String bid,String oid);

    /**
     * 房东确认支付成功
     * @param bid 账单编号
     */
    void ownerCheck(String bid,String oid);


    /**
     * 查询所有未支付的账单的数量
     * @param oid 订单编号
     * @return 返回订单对应的未支付账单数量
     */
    Integer findCountNoPay(String oid);


    /**
     * 通过编号查询账单
     * @param bid 账单编号
     * @return 账单
     */
    BillDto findBillByBid(String bid);

    /**
     * 更新收据存访位置
     * @param receipt 收据
     * @param bid 账单编号
     */
    void updateReceipt(String receipt,String bid);

}
