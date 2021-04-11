package com.hpy.rentHouse.comment.exception;

/**
 * @author: beichenhpy
 * @Date: 2020/3/6 19:10
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import entity.Message;
import entity.ResponseConstant;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author beichenhpy
 * 无效token 异常类 重写
 */
@Component
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint
{

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws ServletException {
        Throwable cause = authException.getCause();

        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        try {
            if(cause instanceof InvalidTokenException) {
                response.getWriter().write(JSON.toJSONString(
                        Message.tokenExpired(ResponseConstant.OAUTH_TOKEN_ILLEGAL), SerializerFeature.DisableCircularReferenceDetect
                ));
            }else{
                response.getWriter().write(JSON.toJSONString(
                        Message.tokenFail(ResponseConstant.OAUTH_TOKEN_MISSING), SerializerFeature.DisableCircularReferenceDetect
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
