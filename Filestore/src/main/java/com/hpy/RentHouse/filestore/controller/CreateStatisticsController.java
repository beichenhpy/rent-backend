package com.hpy.RentHouse.filestore.controller;

import com.hpy.RentHouse.filestore.service.CreateStatisticsService;
import entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: beichenhpy
 * @Date: 2020/5/14 13:52
 */
@RestController
public class CreateStatisticsController {

    @Autowired
    private CreateStatisticsService createStatisticsService;

    @GetMapping("printStatistic/{hid}")
    public Message printStatistic(@PathVariable("hid")String hid){
      return  Message.requestSuccess(createStatisticsService.printElectWaterStatistics(hid));
    }
}
