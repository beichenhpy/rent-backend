package com.hpy.RentHouse.user.controller;


import Query.RegisterQuery;
import com.hpy.RentHouse.user.service.RegisterService;
import com.hpy.RentHouse.user.service.feign.RedisFeign;
import entity.Message;
import DO.UserDo;
import entity.ResponseConstant;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import util.IdWorker;

/**
 * @author: beichenhpy
 * @Date: 2020/2/21 22:51
 * 注册表现层
 * <p>
 * 客户端逻辑：注册的话就是添加到用户表，然后提示是否自动登录(或者在关闭注册页面时，申请token)，
 * 如果是则使用username和password/使用phone申请token
 * 如果否，则跳转到登录页面
 * 当要授权需要Token时 再申请token使用 type=user模式
 * 登录的话有两种方式都可以  type=user/phone
 * <p>
 * <p>
 * 用户注册必须添加 电子签名图片，身份证图片 前台验证
 */
@RestController
public class RegisterController {
    @Autowired
    private RegisterService registerService;
    @Autowired
    private IdWorker idWorker;



    /**
     * 使用用户名密码 用户自己输入的方式注册
     * 使用Check方法，判断是否成功插入
     *
     * @return 返回信息
     */

    @PostMapping("/Register")
    public Message register(@RequestBody RegisterQuery registerQuery) {
        UserDo userDo = new UserDo();
        userDo.setUid(idWorker.nextId() + "");
        userDo.setUsername(registerQuery.getUsername());
        userDo.setNickName("用户"+registerQuery.getUsername());
        userDo.setPhone(registerQuery.getUsername());
        userDo.setPassword(registerQuery.getPassword());
        return registerService.addUser(userDo,registerQuery.getVerCode());
    }

    @PostMapping("/checkUser/{username}")
    public Message checkUser(@PathVariable("username")String username){
        if (registerService.findBy(username) == 1) {
            return Message.requestFail(null);
        }
        return Message.requestSuccess(null);
    }
    /**
     * 发送验证码
     * 调用 sendSms 业务层 如果业务层出现错误，则返回false
     *
     * @param mobile 手机号
     * @return 成功发送返回成功信息，失败返回失败信息
     */
    @PostMapping("/sendSms/{mobile}")
    public Message sendSms(@PathVariable String mobile) {
        Boolean ok = registerService.sendSms(mobile);
        return ok ? Message.requestSuccess(ResponseConstant.GETCODE_FAILED) :
                Message.requestFail(ResponseConstant.GETCODE_SUCCESS);
    }



}
