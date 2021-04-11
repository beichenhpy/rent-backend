package com.hpy.distributed.House.dao;

import DO.StatisticsDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 14:28
 */
@Repository
public interface HouseStatisticsMapper {
    //添加统计信息
    @Insert("insert into housestatistics (sid,oid,uid,hid, electCount, waterCount, electPrice, waterPrice, createTime, price) VALUES (#{sid},#{oid},#{uid},#{hid},#{electCount},#{waterCount},#{electPrice},#{waterPrice},#{createTime},#{price})")
    void addStatistics(StatisticsDo statisticsDo);


    //用户取消订单时要删除对应的所有oid的信息
    @Delete("delete from housestatistics where oid = #{oid}")
    void deleteStatisticsByOid(String oid);

    //查询统计中对应的房屋的所有年
    @Select("select distinct year(createTime) from housestatistics where hid = #{hid} group by createTime asc")
    List<String> findYears(String hid);
    //查询统计中对应的房屋的所有年
    @Select("select distinct year(createTime) from housestatistics where uid = #{uid} group by createTime asc")
    List<String> findYearsByUid(String uid);
    //根据房屋编号和年份查询所有的电和水的用量
    @Select("select electPrice,waterPrice,electCount,waterCount,createTime from housestatistics where year(createTime) = #{year} and hid = #{hid} order by createTime asc")
    List<StatisticsDo> findElectWaterByYear(String year,String hid);

    @Select("select electCount,waterCount,electPrice,waterPrice,createTime,price from housestatistics where hid = #{hid}")
    List<StatisticsDo> findStatisticsByHid(String hid);

    @Select("select electPrice+waterPrice+price from housestatistics where hid = #{hid} and year(createTime) = #{year}")
    List<Integer>findHousePriceByHid(String hid,String year);
}
