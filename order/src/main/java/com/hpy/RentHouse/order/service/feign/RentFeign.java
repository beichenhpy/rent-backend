package com.hpy.RentHouse.order.service.feign;

import DTO.OnRentForOrderDto;
import entity.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author: beichenhpy
 * @Date: 2020/3/14 13:46
 */
@FeignClient(value = "rent-service")
public interface RentFeign {
    /**
     * 远程调用冻结出租信息
     * @param hid hid
     */
    @PutMapping("/frozenOnRent/{hid}")
    void frozenOnRent(@PathVariable("hid") String hid);
    /**
     * 远程调用解冻出租信息
     * @param hid hid
     */
    @PutMapping("/unfrozenOnRent/{hid}")
    void unfrozenOnRent(@PathVariable("hid") String hid);

    /**
     * 远程调用根据hid找到对应的的出租信息
     * @param hid hid
     * @return 返回出租信息
     */
    @GetMapping("/findForOrder/{hid}")
    OnRentForOrderDto findOnRentInfoForOrderByHid(@PathVariable("hid")String hid);
}
