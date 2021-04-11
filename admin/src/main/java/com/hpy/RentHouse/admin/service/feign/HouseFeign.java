package com.hpy.RentHouse.admin.service.feign;

import DTO.HouseBasicDto;
import Query.UpdateProvinceQuery;
import entity.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author: beichenhpy
 * @Date: 2020/4/15 20:07
 */
@FeignClient("house-service")
public interface HouseFeign {
    @PutMapping("/updateProvinceOrCityOrVillage")
    Message updateProvinceOrCityOrVillage(
            @RequestBody UpdateProvinceQuery query);


    @GetMapping("/findHouseByProvince")
    Boolean findHouseByProvince(@RequestParam("province") String province);

    @GetMapping("/findHouseByCity")
    Boolean findHouseByCity(@RequestParam("city") String city);

    @GetMapping("/findHouseByVillage")
    Boolean findHouseByVillage(@RequestParam("village") String village);
}
