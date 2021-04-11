package com.hpy.rentHouse.distributed.uaa.dao;

import DO.AuthsDo;
import DTO.UserDto;
import DO.UserDo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author beichenhpy
 * 查询用户信息
 */
@Repository
public interface UserMapper {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户
     */
    @Select("select * from user where username = #{username}")
    UserDo findByName(String username);


    /**
     * 根据用户id查询对应的权限
     * @param uid 用户id
     * @return 用户权限
     */
    @Select("select auth.* from auth,role_auth where auth.aid = role_auth.aid\n" +
            "and rid = (select role.rid from user,role where role.rid =user.rid and uid = #{uid});")
    List<AuthsDo> findPerm(String uid);
}
