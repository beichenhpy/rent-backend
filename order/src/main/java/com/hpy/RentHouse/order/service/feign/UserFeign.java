package com.hpy.RentHouse.order.service.feign;

import DTO.UserDto;
import DTO.UserOrderDto;
import entity.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author: beichenhpy
 * @Date: 2020/3/18 22:18
 */
@FeignClient("user-service")
public interface UserFeign {
    /**
     * 查询房东信息
     * @param uid uid
     * @return 返回全部信息
     */
    @GetMapping("/find/{uid}")
    UserDto findUserById(@PathVariable("uid") String uid);
}
