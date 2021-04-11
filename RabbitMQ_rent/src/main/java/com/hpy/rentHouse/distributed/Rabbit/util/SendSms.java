package com.hpy.rentHouse.distributed.Rabbit.util;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.stereotype.Component;

/**
 * @author: beichenhpy
 * @Date: 2020/2/26 19:51
 * 阿里云发送短信工具类
 */
@Component
public class SendSms {
    private static final String ACCESSKEY_ID="LTAI4FfDcCgezfoPVVLqQ68q";
    private static final String ACCESSKEY_SECRET="9bCaBukiP9gcaMcMblV2KwXhjh1TzL";
    private static final String SIGN_NAME="北辰租房";
    private static final String TEMPLATE_CODE="SMS_184216182";

//    private static final String TEMPLATE_CODE_ORDER="SMS_186595023";
//    private static final String TEMPLATE_CODE_DEPOSIT="SMS_184216182";
    public static final String ORDER = "order";
    public static final String DEPOSIT = "deposit";
    public static final String CODE = "code";

    /**
     * 发送短信
     * @param mobile
     * @param text
     */
    public void sendCode(String mobile,String text,String type){
        DefaultProfile profile = DefaultProfile.getProfile("default", ACCESSKEY_ID, ACCESSKEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("PhoneNumbers", mobile);

        if (CODE.equals(type)){
            request.putQueryParameter("SignName", SIGN_NAME);
            request.putQueryParameter("TemplateCode", TEMPLATE_CODE);
            request.putQueryParameter("TemplateParam", "{\"checkCode\":"+"'"+text+"'"+"}");
        }
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }




}
