package com.hpy.RentHouse.order.service.feign;

import DTO.Comment;
import entity.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: beichenhpy
 * @Date: 2020/4/27 16:32
 */
@FeignClient("comment-service")
public interface CommentFeign {

    @PostMapping("/adminSend")
    Message adminSend(@RequestBody Comment comment);


}
