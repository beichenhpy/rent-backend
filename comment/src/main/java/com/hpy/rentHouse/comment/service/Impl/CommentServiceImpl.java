package com.hpy.rentHouse.comment.service.Impl;

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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 9:29
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserFeign userFeign;
    @Value("${aliyun.url}")
    private String url;
    @Value("${goeasy.host}")
    private String host;
    @Value("${goeasy.key}")
    private String key;
    @Override
    public void send(Comment comment,String uid) {
        commentMapper.updateUnread(uid, comment.getReciever());
        GoEasy goEasy = new GoEasy(host, key);
        goEasy.publish(comment.getReciever(), JSON.toJSONString(comment));
    }

    @Override
    public void adminSend(Comment comment) {
        commentMapper.updateUnread("admin", comment.getReciever());
        GoEasy goEasy = new GoEasy(host, key);
        goEasy.publish("admin" + comment.getReciever(), JSON.toJSONString(comment));
    }

    @Override
    public Message addFriend(Friend friend) {
        Friend isFriend = commentMapper.findisFriend(friend.getUid(), friend.getFriendUid());
        Friend isFriend1 = commentMapper.findisFriend(friend.getFriendUid(), friend.getUid());
        if (isFriend != null && isFriend1 != null) {
            return Message.requestSuccess(null);
        } else {
            commentMapper.addfriend(friend);
            Friend friend1 = new Friend();
            friend1.setFriendUid(friend.getUid());
            friend1.setUid(friend.getFriendUid());
            commentMapper.addfriend(friend1);
            return Message.requestSuccess(null);
        }
    }

    @Override
    public Message adminAddFriend(Friend friend) {
        Friend isFriend = commentMapper.findisFriend(friend.getUid(), friend.getFriendUid());
        if (isFriend != null) {
            return Message.requestSuccess(null);
        } else {
            commentMapper.addfriend(friend);
            return Message.requestSuccess(null);
        }
    }

    /**
     * ?????????????????????uid ?????????uid ??? ????????????
     * uid ????????? sender
     * friendUid?????????receiver
     * ????????????????????????uid??????receiver??????????????????sender???unread?????????
     * ???????????? ??????????????? ????????????sender????????????uid??????receiver?????????
     * @param uid
     * @return
     */
    @Override
    public Friends findFriend(String uid) {
        List<String> friendUids = commentMapper.findFriends(uid);
        List<UserCommentDto> friends = new ArrayList<>();
        for (String friendUid : friendUids) {
            if (!"admin".equals(friendUid)) {
                UserDto userById = userFeign.findUserById(friendUid);
                UserCommentDto userCommentDto = new UserCommentDto();
                BeanUtils.copyProperties(userById, userCommentDto);
                userCommentDto.setProfilePhoto(url + userCommentDto.getProfilePhoto());
                userCommentDto.setUnRead(commentMapper.findUnread(uid, friendUid));
                friends.add(userCommentDto);
            }
        }

        Integer adminUnRead = commentMapper.findAdminUnRead(uid);
        Friends friends1 = new Friends();
        friends1.setFriendInfos(friends);
        friends1.setAdminUnRead(adminUnRead);
        return friends1;
    }

    @Override
    public void clearUnread(String friendUid,String uid) {
        commentMapper.clearUnread(friendUid, uid);
    }
}
