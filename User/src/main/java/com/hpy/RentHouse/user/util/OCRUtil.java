package com.hpy.RentHouse.user.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hpy.RentHouse.user.model.IdCardMessage;
import entity.ResponseConstant;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.apache.tomcat.util.codec.binary.Base64.encodeBase64;

/**
 * @author: beichenhpy
 * @Date: 2020/3/19 15:12
 */
@Component
public class OCRUtil {

    private static final String host = "https://dm-51.data.aliyun.com";
    private static final String urlPath = "/rest/160601/ocr/ocr_idcard.json";
    //放在头部 Authorization
    private static final String appCode = "你的key";
    private static final String method = "POST";



    public IdCardMessage ocrIdCard(String path) {
        HttpResponse response;
        String res;
        //config
        JSONObject configObj = new JSONObject();
        JSONObject requestObj = new JSONObject();
        String config_str;
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        try {
            configObj.put("side", "face");
            config_str = configObj.toString();
            //header
            headers.put("Authorization", "APPCODE " + appCode);
            // 拼装请求body的json字符串
            requestObj.put("image", path);
            if (config_str.length() > 0) {
                requestObj.put("configure", config_str);
            }
            String bodys = requestObj.toString();
            //请求
            response = HttpUtils.doPost(host, urlPath, method, headers, querys, bodys);
            //返回状态码
            int stat = response.getStatusLine().getStatusCode();
            if (stat != 200) {
                throw new RuntimeException(ResponseConstant.REQUEST_FAILED);
            }
            //返回体
            res = EntityUtils.toString(response.getEntity());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return JSON.parseObject(res, IdCardMessage.class);
    }

}
