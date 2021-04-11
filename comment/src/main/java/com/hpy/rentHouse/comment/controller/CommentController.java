package com.hpy.rentHouse.comment.controller;

import DTO.*;
import com.alibaba.fastjson.JSON;
import com.hpy.rentHouse.comment.dao.CommentMapper;
import com.hpy.rentHouse.comment.service.CommentService;
import com.hpy.rentHouse.comment.service.feign.UserFeign;
import entity.Message;
import io.goeasy.GoEasy;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import util.IdWorker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/26 13:48
 */
@RestController
public class CommentController {
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CommentService commentService;


    /**
     * 获得上下文中的用户信息
     */
    private String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2Authentication)) {
            return null;
        }
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        return userAuthentication.getName();
    }

    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/send")
    public Message send(@RequestBody Comment comment) {
        comment.setSender(getUserId());
        comment.setId(idWorker.nextId() + "");
        commentService.send(comment,getUserId());
        return Message.requestSuccess(null);
    }

    @PostMapping("/adminSend")
    public Message adminSend(@RequestBody Comment comment) {
        comment.setSender("admin");
        comment.setId(idWorker.nextId() + "");
        commentService.adminSend(comment);
        return Message.requestSuccess(null);
    }


    @PostMapping("/addFriend")
    public Message addFriend(@RequestBody Friend friend) {
       return commentService.addFriend(friend);
    }

    @PostMapping("/adminAdd")
    public Message adminAdd(@RequestBody Friend friend) {
       return commentService.adminAddFriend(friend);
    }


    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/getFriends")
    public Message findFriend() {
        Friends friend = commentService.findFriend(getUserId());
        return Message.requestSuccess(friend);
    }

    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/clearUnRead/{friendUid}")
    public Message clear(@PathVariable("friendUid") String friendUid) {
       commentService.clearUnread(friendUid,getUserId());
       return Message.requestSuccess(null);
    }

    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/clearAdmin")
    public Message clear() {
        commentService.clearUnread("admin",getUserId());
        return Message.requestSuccess(null);
    }
}
