package com.hpy.distributed.House.exception;

import entity.Message;
import entity.ResponseConstant;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.tm.api.GlobalTransactionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

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
    //权限不足
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Message oAuth2ExceptionHandler(AccessDeniedException e,HttpServletRequest request){
        logger.error(request.getRequestURI() + ":权限不足",e.getMessage());
        return Message.tokenFail(ResponseConstant.OAUTH_TOKEN_DENIED);
    }
    /**
     * 房屋删除异常
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(HouseDeleteException.class)
    @ResponseBody
    public Message HouseDeleteExceptionHandler(HouseDeleteException e,HttpServletRequest request){
        logger.error(request.getRequestURI() + ":",e.getMessage());
        return Message.requestFail(ResponseConstant.HOUSEDELETEFAILD);
    }

    /**
     * 房屋删除异常
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(AddHouseException.class)
    @ResponseBody
    public Message HouseAddExceptionHandler(AddHouseException e,HttpServletRequest request){
        logger.error(request.getRequestURI() + ":",e.getMessage());
        return Message.addError(null);
    }

}
