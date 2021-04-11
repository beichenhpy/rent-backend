package com.hpy.rentHouse.distributed.rent.service.Feign;

import DTO.HouseDto;
import DTO.HouseInfoDto;
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

    //未发布
    @PutMapping("/updateUnOnRent/{hid}")
    void updateUnOnRent(@PathVariable("hid") String hid);

    //已发布
    @PutMapping("/updateOnRent/{hid}")
    void updateOnRent(@PathVariable("hid") String hid);


    /**
     * 查询房屋详细信息,用于查看出租的详细信息
     * @param hid
     * @return
     */
    @GetMapping("/findHouse/{hid}")
    HouseDto findHouseHid(@PathVariable("hid") String hid);

}
