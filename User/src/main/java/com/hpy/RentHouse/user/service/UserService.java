package com.hpy.RentHouse.user.service;

import DO.IdCardInfoDo;
import DTO.*;

/**
 * @author: beichenhpy
 * @Date: 2020/3/11 22:19
 */

public interface UserService {

    /**
     * 查询用户根据uid 用于订单信息查询使用
     * 返回表现层对象
     * @param uid uid
     * @return user
     */
    UserDto findUserDtoByUid(String uid);
    /**
     * 查询自己的基本信息
     * @param uid 用户编号
     * @return 用户信息
     */
    UserBasicDto findMyInfo(String uid);

    /**
     * 用户更改信息
     * @param profile 信息
     */
    void updateUserByUid(String profile,String uid,String type);

    /**
     * 用户更改默认头像
     * @param profileImagePath 头像路径
     * @param uid uid
     */
    void updateProfileImage(String profileImagePath,String uid);



    /**
     * 用户上传自己的电子签名
     * @param uid 用户编号
     */
    void updateECard(String uid,String fileName);

    Boolean findNickNameExist(String nickName);

    /**
     * 判断用户的电子签名是否存在
     * 用于更新电子签名后，跳转下一页时验证使用
     * @param uid 用户编号
     * @return 返回null/电子签名路径
     */
    String findEcard(String uid);

    void forgetPassword(String password,String username);

}
