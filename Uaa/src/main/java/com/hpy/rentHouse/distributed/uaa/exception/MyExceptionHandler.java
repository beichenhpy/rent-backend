package com.hpy.rentHouse.distributed.uaa.exception;

import entity.Message;
import entity.ResponseConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.security.auth.login.AccountException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: beichenhpy
 * @Date: 2020/4/4 14:40
 */
@ControllerAdvice
public class MyExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(MyExceptionHandler.class);

    /**
     * 参数解析失败异常处理
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Message handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        logger.error(request.getRequestURI() + ":参数解析失败",e.getMessage());
        return Message.requestFail(ResponseConstant.REQUEST_FAILED);
    }

    /**
     * 不支持当前请求方法异常处理
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Message handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        logger.error(request.getRequestURI() + ":不支持当前请求方法",e.getMethod());
        return Message.requestFail(ResponseConstant.REQUEST_FAILED);
    }



}
