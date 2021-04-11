package com.hpy.RentHouse.user.dao;

import DO.UserDo;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

/**
 * @author: beichenhpy
 * @Date: 2020/5/4 19:04
 */
@Repository
public interface RegisterMapper {

    /**
     * 插入用户基本信息表
     * @param userDo 用户实体类
     */
    @Insert("insert into user (uid,username,nickName, password, phone) VALUES " +
            "(#{uid},#{username},#{nickName},#{password},#{phone})")
    void insertInUser(UserDo userDo);
}
