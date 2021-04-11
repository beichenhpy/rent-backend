package com.hpy.RentHouse.filestore.service.feign;

import DTO.UserDto;
import DTO.UserOrderDto;
import DTO.UserRentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author: beichenhpy
 * @Date: 2020/3/25 20:59
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


    /**
     * 将用户的电子签名置为空
     * @param uid uid
     */
    @PutMapping("/updateECardToNull/{uid}")
    void updateECardToNull(
            @PathVariable("uid") String uid);
}
