package com.hpy.RentHouse.order.dao;

import DO.PriceInfoDo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 14:46
 */
@Repository
public interface PriceInfoMapper {
    /**
     * 租客根据账单编号查询对应的费用详情
     * @param bid 账单编号
     * @return 费用详情
     */
    @Select("select * from RenterPriceInfo where bid = #{bid}")
    PriceInfoDo findPriceInfoByBidRenter(String bid);


    /**
     * 房东根据账单编号查询对应的费用详情
     * @param bid 账单编号
     * @return 费用详情
     */
    @Select("select * from ownerpriceinfo where bid = #{bid}")
    PriceInfoDo findPriceInfoByBidOwner(String bid);

    /**
     * 新建金额详情 房东新建
     * @param priceInfoDo 金额详细信息
     */
    @Insert("insert into ownerpriceinfo(priceId,water, elect, basic, bid) VALUES " +
            "(#{priceId},#{water},#{elect},#{basic},#{bid})")
    void addPriceInfo(PriceInfoDo priceInfoDo);
}
