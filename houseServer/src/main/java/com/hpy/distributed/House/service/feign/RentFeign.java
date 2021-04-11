package com.hpy.distributed.House.service.feign;

import DTO.OnRentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author: beichenhpy
 * @Date: 2020/3/15 22:23
 */
@FeignClient(value = "rent-service")
public interface RentFeign {
    /**
     * 远程调用根据hid删除出租信息
     * @param authorization token
     * @param hid 房屋编号
     */
    @DeleteMapping("/delRentNoCheck/{hid}")
    void delOnRentNoCheck(@RequestHeader String authorization, @PathVariable("hid") String hid);

    @PutMapping("/updateHouseRent")
    void updateHouseRent(@RequestHeader String authorization,@RequestBody OnRentDto onRentDto);
}
