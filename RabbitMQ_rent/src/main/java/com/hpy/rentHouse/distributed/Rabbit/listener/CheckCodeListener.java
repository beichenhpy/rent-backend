package com.hpy.rentHouse.distributed.Rabbit.listener;

import com.hpy.rentHouse.distributed.Rabbit.util.SendSms;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author: beichenhpy
 * @Date: 2020/2/26 19:27
 * 发送短信中间件
 */
@Component
@RabbitListener(queues = "sms_checkCode")
public class CheckCodeListener {
    @Autowired
    private SendSms sendSms;
    @RabbitHandler
    public void executeSms(Map<String,String> map){
        String phone = map.get("mobile");
        String checkCode = map.get("checkCode");
        sendSms.sendCode(phone,checkCode,SendSms.CODE);
    }

}
