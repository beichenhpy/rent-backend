package com.hpy.rentHouse.comment.service;

import DTO.Comment;
import DTO.Friend;
import DTO.Friends;
import entity.Message;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 9:29
 */

public interface CommentService {
    void send(Comment comment,String uid);
    void adminSend(Comment comment);
    Message addFriend(Friend friend);
    Message adminAddFriend(Friend friend);
    Friends findFriend(String uid);
    void clearUnread(String friendUid,String uid);
}
