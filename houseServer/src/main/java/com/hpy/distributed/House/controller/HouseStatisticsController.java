package com.hpy.distributed.House.controller;

import DTO.ElectWaterUsageDto;
import DTO.ElectWaterUsageYearDto;
import DTO.HousePriceStatisticsDto;
import DTO.StatisticsDto;
import com.hpy.distributed.House.service.HouseStatisticsService;
import entity.Message;
import entity.ResponseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import util.IdWorker;

import javax.swing.*;
import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 9:21
 */
@RestController
public class HouseStatisticsController {
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private HouseStatisticsService houseStatisticsService;

    private String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2Authentication)) {
            return null;
        }
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        return userAuthentication.getName();
    }
    /**
     * 查询统计信息中房屋的出租年份
     * @param hid 房屋编号
     * @return 返回年份集合
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findYears/{hid}")
    public Message findYears(@PathVariable("hid")String hid){
        List<String> years = houseStatisticsService.findYears(hid);
        return Message.requestSuccess(years);
    }

    /**
     * 查询统计信息中房屋的出租年份
     * @param uid 用户编号
     * @return 返回年份集合
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findYearsByUid/{uid}")
    public Message findYearsByUid(@PathVariable("uid")String uid){
        List<String> years = houseStatisticsService.findYearsByUid(uid);
        return Message.requestSuccess(years);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findElectWaterByYear/{hid}")
    public Message findElectWaterByYear(@PathVariable("hid")String hid,@RequestParam("year")String year){
        ElectWaterUsageDto electWaterByYear = houseStatisticsService.findElectWaterByYear(year, hid);
        return Message.requestSuccess(electWaterByYear);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findElectWatersByHid/{hid}")
    public Message findElectWatersByHid(@PathVariable("hid")String hid){
        List<ElectWaterUsageYearDto> statisticsByHid = houseStatisticsService.findStatisticsByHid(hid);
        return Message.requestSuccess(statisticsByHid);
    }


    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findPricesByYear")
    public Message findPricesByYear(@RequestParam("year")String year){
        List<HousePriceStatisticsDto> pricesStatisticsByUid = houseStatisticsService.findPricesStatisticsByUid(getUserId(), year);
        return Message.requestSuccess(pricesStatisticsByUid);
    }



    @PostMapping("/addStatistics")
    public Message addStatistics(@RequestBody StatisticsDto statisticsDto){
        statisticsDto.setSid(idWorker.nextId()+"");
        houseStatisticsService.addStatistics(statisticsDto);
        return Message.requestSuccess(null);
    }

    @DeleteMapping("/deleteStatistic/{oid}")
    public Message deleteStatistic(@PathVariable("oid") String oid,@RequestParam("hid")String hid,@RequestParam("year")String year,@RequestParam("uid")String uid) {
        houseStatisticsService.deleteStatistics(oid,hid,year,uid);
        return Message.requestSuccess(ResponseConstant.DELETE_SUCCESS);
    }


    @GetMapping("findAll/{hid}")
    public Message findAll(@PathVariable("hid")String hid){
        return Message.requestSuccess(houseStatisticsService.findAllStaticsByHid(hid));
    }

}
