package com.hpy.RentHouse.user.aop;

/**
 * @author: beichenhpy
 * @Date: 2020/3/24 14:40
 */
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Date;

@Component
@Aspect
public class LogAopAdviseDefine {
    private Logger logger = LoggerFactory.getLogger(getClass());


    @Pointcut(value = "execution(* com.hpy.RentHouse.user.service.Impl..*(..)) ||" +
            "execution(* com.hpy.RentHouse.user.util.*.*(..)) ||" +
            "execution(* com.hpy.RentHouse.user.controller.*.*(..))")
    private void pointCut(){}

    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();//1、开始时间
        ServletRequestAttributes requestAttr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String uri = requestAttr.getRequest().getRequestURI();
        logger.info("---开始计时: {}  URI: {}", new Date(),uri);

        //访问目标方法的参数 可动态改变参数值
        Object[] args = joinPoint.getArgs();
        //方法名获取
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("---请求方法：{}, 请求参数: {}", methodName, Arrays.toString(args));

        //调用实际方法
        Object object = joinPoint.proceed();
        logger.info("---请求返回值：{}",object);

        long endTime = System.currentTimeMillis();
        logger.info("---方法：{}结束--------结束时间: {},  URI: {},耗时：{}",methodName, new Date(),uri,endTime - beginTime);
        return object;
    }

    @AfterThrowing(pointcut = "pointCut()", throwing = "exception")
    public void logMethodInvokeException(JoinPoint joinPoint, Exception exception) {
        logger.error("---发生在方法 {} 产生异常时。异常为：{}---", joinPoint.getSignature().toShortString(), exception.getMessage());
    }
}
