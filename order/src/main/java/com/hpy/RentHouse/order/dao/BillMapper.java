package com.hpy.RentHouse.order.dao;

import DO.BillDo;
import DO.UnitPriceDo;
import DO.PriceInfoDo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/3/13 17:30
 * 账单持久层
 */
@Repository
public interface BillMapper {

    /**
     * 新建账单 房东新建 插入房东账单表 同步到 租客表
     * @param billDo billDto
     */
    @Insert("insert into ownerBill (createTime, oid, price, bid) VALUES (#{createTime},#{oid},#{price},#{bid})")
    void addBill(BillDo billDo);


    /**
     * 租客根据订单编号查询账单
     * @param oid 订单编号
     * @return 返回账单集合
     */
    @Select("select * from renterBill where oid = #{oid}")
    @Results(id = "billMapRenter",value = {
            @Result(id = true,property = "bid",column = "bid"),
            @Result(property = "priceInfoDo",column = "bid",
            one = @One(select = "com.hpy.RentHouse.order.dao.PriceInfoMapper.findPriceInfoByBidRenter"))
    })
    List<BillDo> findBillByOidRenter(String oid);

    /**
     * 房东根据订单编号查询账单
     * @param oid 订单编号
     * @return 返回账单集合
     */
    @Select("select * from ownerBill where oid = #{oid} order by createTime asc")
    @Results(id = "billMapOwner",value = {
            @Result(id = true,property = "bid",column = "bid"),
            @Result(property = "priceInfoDo",column = "bid",
                    one = @One(select = "com.hpy.RentHouse.order.dao.PriceInfoMapper.findPriceInfoByBidOwner"))
    })
    List<BillDo> findBillByOidOwner(String oid);


    /**
     * 租客确认支付成功 租客修改
     * @param bid 账单编号
     * @return 返回影响行数
     */
    @Update("update renterBill set renterCheck = 1 where renterCheck = 0 and bid = #{bid}")
    Integer renterCheck(String bid);

    /**
     * 房主确认支付成功
     * @param bid 账单编号
     * @return 返回影响行数
     */
    @Update("update ownerBill set ownerCheck = 1 where ownerCheck = 0 and bid = #{bid}")
    Integer ownerCheck(String bid);


    /**
     * 租客查询所有未支付的账单的数量
     * @param oid 订单编号
     * @return 返回订单对应的未支付账单数量
     */
    @Select("select count(*) from renterBill where oid = #{oid} and renterCheck = 0")
    Integer findCountNoPayRenter(String oid);

    @Select("select count(*) from ownerBill where oid = #{oid} and ownerCheck = 0")
    Integer findCountNoPayOwner(String oid);
    /**
     * 根据特定的创建时间查询bill
     * @param createTime 创建时间
     * @return 返回账单
     */
    @Select("select * from renterBill where createTime = #{createTime} and oid = #{oid}")
    BillDo findBillByCreateTime(Date createTime, String oid);


    //查询账单通过账单编号
    @Select("select * from renterBill where bid = #{bid}")
    BillDo findBillByBid(String bid);

    //更新收据保存地址
    @Update("update renterBill set receipt = #{receipt} where bid = #{bid}")
    void updateReceipt(String receipt,String bid);
}
