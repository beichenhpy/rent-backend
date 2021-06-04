package com.hpy.rentHouse.distributed.uaa.exception;

import entity.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

/**
 * @author beichenhpy
 * @since 2020/4/5 18:55
 */
@Component("myWebResponseExceptionTranslator")
public class MyWebResponseExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception> {
    //主要是抛出异常让servlet接收到
    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
        if (e instanceof OAuth2Exception) {
            String message = "密码错误";
            return ResponseEntity.ok(new OAuth2Exception(message));
        }
        throw e;
    }
}
