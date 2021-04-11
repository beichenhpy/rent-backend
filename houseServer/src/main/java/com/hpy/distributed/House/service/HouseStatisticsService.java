package com.hpy.distributed.House.service;

import DTO.ElectWaterUsageDto;
import DTO.ElectWaterUsageYearDto;
import DTO.HousePriceStatisticsDto;
import DTO.StatisticsDto;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 9:22
 */

public interface HouseStatisticsService {
    //添加统计信息
    void addStatistics(StatisticsDto statisticsDto);
    //删除统计信息
    void deleteStatistics(String oid,String hid,String year,String uid);

    //查询统计信息年份
    List<String> findYears(String hid);

    List<String> findYearsByUid(String uid);

    //根据年份和编号查询对应的水电信息
    ElectWaterUsageDto findElectWaterByYear(String year, String hid);

    //查询房屋的所有统计信息 用于年度报表
    List<ElectWaterUsageYearDto> findStatisticsByHid(String hid);
    //用户查询他所有房屋的那一年对应的房租信息
    List<HousePriceStatisticsDto> findPricesStatisticsByUid(String uid,String year);

    List<StatisticsDto> findAllStaticsByHid(String hid);
}
