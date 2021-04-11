package com.hpy.RentHouse.order.service.feign;



import DTO.HouseDto;
import DTO.StatisticsDto;
import Query.HouseRecordQuery;
import entity.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @author: beichenhpy
 * @Date: 2020/2/28 19:15
 */
@Component
@FeignClient(value = "house-service")
public interface HouseFeign {

    @PutMapping("/isRented/{hid}")
    void isRented(@PathVariable("hid") String hid);

    @PutMapping("/unisRented/{hid}")
    void unisRented(@PathVariable("hid") String hid);


    @GetMapping("/findHouse/{hid}")
    HouseDto findHouseHid(@PathVariable("hid") String hid);

    @GetMapping("/findBasicHouse/{hid}")
    Message findBasicHouse(@RequestHeader("Authorization") String authorization, @PathVariable("hid") String hid);
    @PutMapping("/updateHouseRecord")
    Message updateHouseRecord(@RequestHeader("Authorization") String authorization, @RequestBody HouseRecordQuery houseRecordQuery);

    @PostMapping("/addStatistics")
    Message addStatistics(@RequestBody StatisticsDto statisticsDto);


    @DeleteMapping("/deleteStatistic/{oid}")
    Message deleteStatistic(@PathVariable("oid") String oid,@RequestParam("hid")String hid,@RequestParam("year")String year,@RequestParam("uid")String uid);
}
