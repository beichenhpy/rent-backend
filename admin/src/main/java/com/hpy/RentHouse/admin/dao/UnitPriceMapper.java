package com.hpy.RentHouse.admin.dao;

import DO.UnitPriceDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/11 19:35
 */
@Repository
public interface UnitPriceMapper {


    /**
     * 添加电费水费单价
     * @param unitPriceDo 单价
     */
    @Insert("insert into unitprice (priceId, province, city, firstStage, firstStagePrice, secondStage, secondStagePrice, thirdStagePrice, type) values " +
            "(#{priceId},#{province},#{city},#{firstStage},#{firstStagePrice},#{secondStage},#{secondStagePrice},#{thirdStagePrice},#{type})")
    void addUnitPrice(UnitPriceDo unitPriceDo);


    /**
     * 根据province和city查询单价
     * @param province 省份
     * @param city 城市
     * @return 返回单价
     */
    @Select("select * from unitprice where city = #{city} and province = #{province}")
    List<UnitPriceDo> findUnitPriceByProvinceAndCity(String province, String city);

    /**
     * 查询所有费用单价
     * @return
     */
    @Select("select * from unitprice")
    List<UnitPriceDo> findAllUnitPrice();

    //删除单价信息
    @Delete("delete from unitprice where priceId = #{priceId}")
    Integer deleteUnitPrice(String priceId);

    //更新单价信息
    @Update("update unitprice set firstStage = #{firstStage},firstStagePrice = #{firstStagePrice}," +
            "secondStage = #{secondStage}, secondStagePrice = #{secondStagePrice}," +
            "thirdStagePrice = #{thirdStagePrice},type = #{type} where priceId = #{priceId}")
    Integer updateUnitPrice(UnitPriceDo unitPriceDo);
}
