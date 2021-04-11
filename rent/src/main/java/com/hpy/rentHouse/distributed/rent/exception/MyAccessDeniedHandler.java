package com.hpy.rentHouse.distributed.rent.exception;

/**
 * @author: beichenhpy
 * @Date: 2020/3/6 19:11
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import entity.Message;
import entity.ResponseConstant;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author beichenhpy
 * token 无效处理器重写
 */
@Component("myAccessDeniedHandler")
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        try {
            response.getWriter().write(JSON.toJSONString(
                    Message.tokenFail(ResponseConstant.OAUTH_TOKEN_DENIED), SerializerFeature.DisableCircularReferenceDetect
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}