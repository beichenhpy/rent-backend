package com.hpy.RentHouse.user.dao;

import DO.IdCardInfoDo;
import DTO.IdCardInfoDto;
import DTO.UserDto;
import DO.UserDo;
import DTO.UserOrderDto;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @author: beichenhpy
 * @Date: 2020/3/11 22:15
 */
@Repository
public interface UserMapper {

    /**
     * 根据用户信息查询用户
     * @return 返回用户实体
     */
    @Select("select * from user where username = #{username}")
    UserDo findUser(String username);


    /**
     * 根据uid查询用户信息
     * 用于表现层传输，显示到前台
     * @param uid uid
     * @return 返回用户实体类
     */
    @Select("select * from user where uid = #{uid}")
    @Results(id = "userMap",value = {
            @Result(id = true,column = "uid",property = "uid"),
            @Result(column = "uid",property = "idCardInfo",one = @One(select = "com.hpy.RentHouse.user.dao.IdCardInfoMapper.findIdCardInfo"))
    })
    UserDo findUserAllInfo(String uid);

    /**
     * 根据username查询用户信息
     * @param username 用户名
     * @return 返回用户信息
     */
    @Select("select * from user where username = #{username}")
    UserDo findUserByUsername(String username);


    /**
     * 根据uid查询用户信息
     * @param uid 用户编号
     * @return 返回用户信息
     */
    @Select("select * from user where uid = #{uid}")
    UserDo findUserBasicInfoByUid(String uid);


    /**
     * 用户修改昵称
     */
    @Update("update user set nickName = #{nickName} where uid = #{uid}")
    void updateNickNameByUid(String nickName,String uid);

    @Select("select count(*) from user where nickName = #{nickName}")
    Integer selectNickName(String nickName);
    /**
     * 用户修改性别
     */
    @Update("update user set sex = #{sex} where uid = #{uid}")
    void updateSexByUid(String sex,String uid);

    /**
     * 用户修改头像
     */
    @Update("update user set profilePhoto = #{profilePhoto} where uid = #{uid}")
    Integer updateProfilePhotoByUid(String profilePhoto,String uid);


    /**
     * 忘记密码
     * @param password
     * @param username
     */
    @Update("update user set password = #{password} where username = #{username}")
    void forgetPassword(String password,String username);
    /**
     * 用户修改手机号
     */
    @Update("update user set phone = #{phone} where uid = #{uid}")
    void updatePhoneByUid(String phone,String uid);

    /**
     * 用户上传自己的电子签名
     * @param uid 用户编号
     * @param eCard 电子签名保存路径
     */
    @Update("update user set eCard = #{eCard} where uid = #{uid}")
    Integer updateECard(String uid,String eCard);


    /**
     * 判断电子签名是否存在
     */
    @Select("select eCard from user where uid = #{uid}")
    String findECard(String uid);


}
