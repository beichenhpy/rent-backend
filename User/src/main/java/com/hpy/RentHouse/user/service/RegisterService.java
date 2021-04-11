package com.hpy.RentHouse.user.service;

import DO.UserDo;
import entity.Message;

/**
 * @author: beichenhpy
 * @Date: 2020/2/21 22:20
 *
 * 用户业务层接口
 */

public interface RegisterService {

    /**
     *
     *
     * @return 返回用户实体类
     */
    Integer findBy(String username);


    /**
     * 新建用户
     *
     * @param userDo 用户实体类
     */
    Message addUser(UserDo userDo,String code);

    /**
     * 发送手机验证码 产生随机数
     * @param mobile 手机号
     */
    Boolean sendSms(String mobile);

}
