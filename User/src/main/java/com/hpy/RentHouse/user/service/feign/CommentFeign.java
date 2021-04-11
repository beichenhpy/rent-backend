package com.hpy.RentHouse.user.service.feign;

import DTO.Friend;
import entity.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: beichenhpy
 * @Date: 2020/4/28 20:50
 */
@FeignClient("comment-service")
public interface CommentFeign {
    @PostMapping("/adminAdd")
    Message adminAdd(@RequestBody Friend friend);
}
