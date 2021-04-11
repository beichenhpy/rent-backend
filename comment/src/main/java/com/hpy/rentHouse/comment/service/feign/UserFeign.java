package com.hpy.rentHouse.comment.service.feign;

import DTO.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author: beichenhpy
 * @Date: 2020/4/26 18:07
 */
@FeignClient("user-service")
public interface UserFeign {

    @GetMapping("/find/{uid}")
    UserDto findUserById(@PathVariable("uid") String uid);
}
