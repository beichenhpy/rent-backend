package com.hpy.RentHouse.filestore.service.feign;

import DTO.HouseDto;
import entity.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author: beichenhpy
 * @Date: 2020/4/28 16:18
 */
@FeignClient("house-service")
public interface HouseFeign {

    @GetMapping("/findHouse/{hid}")
    HouseDto findHouseHid(@PathVariable("hid") String hid);

    @GetMapping("findAll/{hid}")
    Message findAll(@PathVariable("hid") String hid);

}
