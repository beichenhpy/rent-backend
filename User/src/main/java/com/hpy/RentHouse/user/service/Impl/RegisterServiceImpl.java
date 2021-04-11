package com.hpy.RentHouse.user.service.Impl;

import DTO.Friend;
import com.hpy.RentHouse.user.dao.RegisterMapper;
import com.hpy.RentHouse.user.dao.UserMapper;
import com.hpy.RentHouse.user.service.RegisterService;
import com.hpy.RentHouse.user.service.feign.CommentFeign;
import com.hpy.RentHouse.user.service.feign.RedisFeign;
import DO.UserDo;
import entity.Message;
import entity.ResponseConstant;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author: beichenhpy
 * @Date: 2020/2/21 22:20
 * <p>
 * 注册业务层实现类
 */
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisFeign redisFeign;
    @Autowired
    private CommentFeign commentFeign;

    private static final Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);
    /**
     * 检查用户的 username 和 phone是否存在
     *
     *
     * @return 返回值意思: 只要有一个存在 证明用户存在 返回1 否则返回 0
     */
    @Override
    public Integer findBy(String username) {
        return userMapper.findUser(username) != null ? 1 : 0;
    }

    /**
     * 新增用户
     *
     * @param userDo 用户实体类
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Message addUser(UserDo userDo,String code) {
        //从redis中获取验证码
        String checkCodeRedis = redisFeign.get(userDo.getUsername());
        //不存在
        if (checkCodeRedis == null || checkCodeRedis.isEmpty()) {
            return Message.requestFail(ResponseConstant.CODE_WRONG);
        }
        //验证码错误
        if (!checkCodeRedis.equals(code)) {
            return Message.requestFail(ResponseConstant.CODE_WRONG);
        }
        //将密码设置为BC加密
        userDo.setPassword(BCrypt.hashpw(userDo.getPassword(), BCrypt.gensalt()));
        //添加到数据库
        registerMapper.insertInUser(userDo);
        //添加到系统通知
        Friend friend = new Friend();
        friend.setUid("admin");
        friend.setFriendUid(userDo.getUid());
        commentFeign.adminAdd(friend);
        return Message.requestSuccess(ResponseConstant.REGISTOK);
    }

    /**
     * 发送短信实现类
     *
     * @param mobile 手机号
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean sendSms(String mobile) {
        logger.info("-----------------生成短信验证码------------------");
        String checkCode = RandomStringUtils.randomNumeric(6);
        redisFeign.set(mobile, checkCode, 5, TimeUnit.MINUTES);
        logger.info("-----------------发送到消息队列------------------");
        HashMap<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("checkCode", checkCode);
        rabbitTemplate.convertAndSend("sms_checkCode", map);
        return true;
    }

}
