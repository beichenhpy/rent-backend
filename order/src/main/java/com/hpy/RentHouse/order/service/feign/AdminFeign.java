package com.hpy.RentHouse.order.service.feign;

import DTO.UnitPriceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/11 19:44
 */
@FeignClient("admin-service")
public interface AdminFeign {

    /**
     * 查询对应的省份的费用单价
     *
     * @param province
     * @param city
     * @return
     */
    @GetMapping("/findUnitPriceByProvince")
    List<UnitPriceDto> findUnitPrice(
            @RequestParam("province") String province,
            @RequestParam("city") String city);
}
