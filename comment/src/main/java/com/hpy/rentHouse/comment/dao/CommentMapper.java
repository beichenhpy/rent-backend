package com.hpy.rentHouse.comment.dao;


import DTO.Friend;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/26 14:17
 */
@Repository
public interface CommentMapper {

    @Insert("insert into rent_comment.friends (uid, friendUid) VALUES (#{uid},#{friendUid})")
    void addfriend(Friend friend);


    @Select("select uid from rent_comment.friends where friendUid = #{friendUid}")
    List<String> findFriends(String friendUid);

    @Select("select unread from rent_comment.friends where friendUid = #{friendUid} and uid = #{uid}")
    Integer findUnread(String friendUid,String uid);

    @Update("update rent_comment.friends set unRead = unRead + 1 where uid = #{sender} and friendUid = #{receiver}")
    void updateUnread(String sender,String receiver);

    @Update("update rent_comment.friends set unRead = 0 where uid = #{sender} and friendUid = #{receiver}")
    void clearUnread(String sender,String receiver);

    //查询uid为admin的用户的未读数
    @Select("select unRead from rent_comment.friends where uid = 'admin' and friendUid = #{friendUid}")
    Integer findAdminUnRead(String friendUid);

    @Select("select * from rent_comment.friends where uid = #{uid} and friendUid = #{friendUid}")
    Friend findisFriend(String uid,String friendUid);
}
