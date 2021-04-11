package com.hpy.distributed.House.service.Impl;

import DO.HouseDo;
import DO.StatisticsDo;
import DTO.ElectWaterUsageDto;
import DTO.ElectWaterUsageYearDto;
import DTO.HousePriceStatisticsDto;
import DTO.StatisticsDto;
import com.alibaba.fastjson.JSON;
import com.hpy.distributed.House.dao.HouseMapper;
import com.hpy.distributed.House.dao.HouseStatisticsMapper;
import com.hpy.distributed.House.service.HouseStatisticsService;
import com.hpy.distributed.House.service.feign.RedisFeign;
import entity.Constant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 9:22
 */
@Service
public class HouseStatisticsServiceImpl implements HouseStatisticsService {
    @Autowired
    private HouseStatisticsMapper houseStatisticsMapper;
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private RedisFeign redisFeign;
    private static final Logger logger = LoggerFactory.getLogger(HouseStatisticsServiceImpl.class);


    //添加统计信息
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addStatistics(StatisticsDto statisticsDto) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfyear = new SimpleDateFormat("yyyy");
        Date createTime;
        logger.info("---------------------createTime开始-------------------");
        try {
            createTime = sdf.parse(statisticsDto.getCreateTime());
        } catch (ParseException e) {
            logger.info("---------------------createTime异常-------------------");
            throw new RuntimeException();
        }
        logger.info("---------------------createTime结束,{}-------------------", createTime);
        StatisticsDo statisticsDo = new StatisticsDo();
        BeanUtils.copyProperties(statisticsDto, statisticsDo);
        statisticsDo.setElectPrice(Double.parseDouble(statisticsDto.getElectPrice()));
        statisticsDo.setWaterPrice(Double.parseDouble(statisticsDto.getWaterPrice()));
        statisticsDo.setCreateTime(createTime);
        logger.info("---------------------statisticsDo,{}-------------------", statisticsDo);
        houseStatisticsMapper.addStatistics(statisticsDo);
        redisFeign.del(Constant.STATISTICS_Y+statisticsDto.getHid(),
                Constant.STATISTICS_M+statisticsDto.getHid()+sdfyear.format(statisticsDo.getCreateTime()),
                Constant.STATISTICS_P+statisticsDto.getUid()+sdfyear.format(statisticsDo.getCreateTime()),
                Constant.STATISTICS+statisticsDto.getHid()
        );
    }

    //用户取消订单时删除对应的统计信息
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteStatistics(String oid,String hid,String year,String uid) {
        houseStatisticsMapper.deleteStatisticsByOid(oid);
        redisFeign.del(Constant.STATISTICS_Y+hid,Constant.STATISTICS_M + hid + year,Constant.STATISTICS_P+uid+year,Constant.STATISTICS+hid);
    }


    /**
     * 查询统计信息的年份
     *
     * @param hid 房屋编号
     * @return
     */
    @Override
    public List<String> findYears(String hid) {
        return houseStatisticsMapper.findYears(hid);
    }

    @Override
    public List<String> findYearsByUid(String uid) {
        return houseStatisticsMapper.findYearsByUid(uid);
    }

    /**
     * 查询对应年份和房屋的水电费信息 月报表
     *
     * @param year 年份
     * @param hid  房屋编号
     * @return 返回水电费信息
     */
    @Override
    public ElectWaterUsageDto findElectWaterByYear(String year, String hid) {
        ElectWaterUsageDto electWaterUsageDto = new ElectWaterUsageDto();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        logger.info("--------------------------缓存查询-----------------------------------");
        String statistics = redisFeign.get(Constant.STATISTICS_M + hid + year);
        if(StringUtils.isNotEmpty(statistics)){
            electWaterUsageDto = JSON.parseObject(statistics, ElectWaterUsageDto.class);
        }else {
            logger.info("--------------------------数据库查询-----------------------------------");
            List<StatisticsDo> statisticsDos = houseStatisticsMapper.findElectWaterByYear(year, hid);
            List<String> electCounts = new ArrayList<>();
            List<String> waterCounts = new ArrayList<>();
            List<String> electPrices = new ArrayList<>();
            List<String> waterPrices = new ArrayList<>();
            List<String> dates = new ArrayList<>();
            for (StatisticsDo statisticsDo : statisticsDos) {
                electCounts.add(Integer.toString(statisticsDo.getElectCount()));
                waterCounts.add(Integer.toString(statisticsDo.getWaterCount()));
                electPrices.add(Double.toString(statisticsDo.getElectPrice()));
                waterPrices.add(Double.toString(statisticsDo.getWaterPrice()));
                dates.add(sdf.format(statisticsDo.getCreateTime()));
            }
            electWaterUsageDto.setElectCount(electCounts);
            electWaterUsageDto.setWaterCount(waterCounts);
            electWaterUsageDto.setElectPrice(electPrices);
            electWaterUsageDto.setWaterPrice(waterPrices);
            electWaterUsageDto.setDate(dates);
            redisFeign.set(Constant.STATISTICS_M+hid+year,JSON.toJSONString(electWaterUsageDto));
        }
        return electWaterUsageDto;
    }

    /**
     * 查询房屋的所有统计信息 年度报表 水电费 水电用量 年报表
     *
     * @param hid 房屋编号
     * @return 返回用量
     */
    @Override
    public List<ElectWaterUsageYearDto> findStatisticsByHid(String hid) {
        List<ElectWaterUsageYearDto> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        DecimalFormat df = new DecimalFormat("0.00");
        logger.info("--------------------------缓存查询-----------------------------------");
        String statistic = redisFeign.get(Constant.STATISTICS_Y + hid);
        if(StringUtils.isNotEmpty(statistic)){
            list = JSON.parseArray(statistic, ElectWaterUsageYearDto.class);
        }else {
            logger.info("--------------------------数据库查询-----------------------------------");
            List<String> years = houseStatisticsMapper.findYears(hid);
            List<StatisticsDo> statisticsByHid = houseStatisticsMapper.findStatisticsByHid(hid);
            Integer electCount = 0,waterCount = 0;
            Double electPrice = 0d,waterPrice = 0d;
            for (String year : years) {
                ElectWaterUsageYearDto electWaterUsageYearDto = new ElectWaterUsageYearDto();
                electWaterUsageYearDto.setYear(year);
                for (StatisticsDo statisticsDo : statisticsByHid) {
                    if (year.equals(sdf.format(statisticsDo.getCreateTime()))) {
                        electCount += statisticsDo.getElectCount();
                        waterCount += statisticsDo.getWaterCount();
                        electPrice += statisticsDo.getElectPrice();
                        waterPrice += statisticsDo.getWaterPrice();
                    }
                }
                electWaterUsageYearDto.setElectCount(Integer.toString(electCount));
                electWaterUsageYearDto.setWaterCount(Integer.toString(waterCount));
                electWaterUsageYearDto.setElectPrice(df.format(electPrice));
                electWaterUsageYearDto.setWaterPrice(df.format(waterPrice));
                list.add(electWaterUsageYearDto);
                redisFeign.set(Constant.STATISTICS_Y+hid,JSON.toJSONString(list));
            }
        }
        return list;
    }

    /**
     * 查询某一年该用户所有房屋的租金和
     * @param uid 用户编号
     * @param year 年份
     * @return
     */
    @Override
    public List<HousePriceStatisticsDto> findPricesStatisticsByUid(String uid, String year) {
        List<HousePriceStatisticsDto> housePriceStatisticsDtos = new ArrayList<>();
        String statistics = redisFeign.get(Constant.STATISTICS_P + uid + year);
        if(StringUtils.isNotEmpty(statistics)){
            housePriceStatisticsDtos = JSON.parseArray(statistics,HousePriceStatisticsDto.class);
        }else {
            logger.info("---------------------查询房屋信息-------------------");
            List<HouseDo> houseByUid = houseMapper.findHouseByUid(uid);
            if(!houseByUid.isEmpty()){
                logger.info("---------------------根据房屋信息查询对应的租金记录-------------------");
                for (HouseDo houseDo : houseByUid) {
                    HousePriceStatisticsDto housePriceStatisticsDto = new HousePriceStatisticsDto();
                    housePriceStatisticsDto.setHouseName(houseDo.getBuilding()+"栋"+houseDo.getUnit()+"单元"+houseDo.getAddress()+houseDo.getHouseNum());
                    List<Integer> priceList = houseStatisticsMapper.findHousePriceByHid(houseDo.getHid(), year);
                    logger.info("---------------------priceList:{}-------------------",priceList);
                    Integer prices = priceList.stream().reduce(Integer::sum).orElse(0);
                    logger.info("---------------------prices:{}-------------------",prices);
                    housePriceStatisticsDto.setPrices(prices);
                    housePriceStatisticsDtos.add(housePriceStatisticsDto);
                    logger.info("---------------------house:{}-------------------",housePriceStatisticsDto);
                    logger.info("---------------------houses:{}-------------------",housePriceStatisticsDtos);
                }
                redisFeign.set(Constant.STATISTICS_P + uid + year,JSON.toJSONString(housePriceStatisticsDtos));
            }
        }

        return housePriceStatisticsDtos;
    }

    //查询所有统计信息
    @Override
    public List<StatisticsDto> findAllStaticsByHid(String hid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat df = new DecimalFormat("0.00");
        List<StatisticsDto> statisticsDtoList = new ArrayList<>();
        String s = redisFeign.get(Constant.STATISTICS + hid);
        if(StringUtils.isNotEmpty(s)){
            statisticsDtoList = JSON.parseArray(s,StatisticsDto.class);
        }else {
            List<StatisticsDo> statisticsByHid = houseStatisticsMapper.findStatisticsByHid(hid);
            if(!statisticsByHid.isEmpty()){
                for (StatisticsDo statisticsDo : statisticsByHid) {
                    StatisticsDto statisticsDto = new StatisticsDto();
                    BeanUtils.copyProperties(statisticsDo,statisticsDto);
                    statisticsDto.setCreateTime(sdf.format(statisticsDo.getCreateTime()));
                    statisticsDto.setElectPrice(df.format(statisticsDo.getElectPrice()));
                    statisticsDto.setWaterPrice(df.format(statisticsDo.getWaterPrice()));
                    statisticsDtoList.add(statisticsDto);
                }
                redisFeign.set(Constant.STATISTICS+hid,JSON.toJSONString(statisticsDtoList));
            }
        }
        return statisticsDtoList;
    }
}
