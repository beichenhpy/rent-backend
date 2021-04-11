package com.hpy.RentHouse.order.exception;

import entity.Message;
import entity.ResponseConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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
     * 订单删除失败
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(OrderDeleteException.class)
    @ResponseBody
    public Message OrderDeleteExceptionHandler(OrderDeleteException e,HttpServletRequest request){
        logger.error(request.getRequestURI() + ":",e.getMessage());
        return Message.requestFail(ResponseConstant.ORDERDELETEFAILD);
    }

    /**
     * 订单取消失败
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(OrderCancelException.class)
    @ResponseBody
    public Message OrderCancelExceptionHandler(OrderCancelException e,HttpServletRequest request){
        logger.error(request.getRequestURI() + ":",e.getMessage());
        return Message.requestFail(ResponseConstant.ORDERCANCELFAILD);
    }

    /**
     * 由于订单被别人下了导致的下订单失败异常
     * @return
     */
    @ExceptionHandler(OrderException.class)
    @ResponseBody
    public Message OrderExceptionHandler(OrderException e,HttpServletRequest request){
        logger.error(request.getRequestURI() + ":",e.getMessage());
        return Message.requestFail(ResponseConstant.ADDORDERERROR);
    }

    //申请退押金异常
    @ExceptionHandler(DepositException.class)
    @ResponseBody
    public Message DepositExceptionHandler(DepositException e,HttpServletRequest request){
        logger.error(request.getRequestURI() + ":",e.getMessage());
        return Message.requestFail(ResponseConstant.UNRENT);
    }



}
