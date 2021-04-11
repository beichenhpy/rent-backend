package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: beichenhpy
 * @Date: 2020/2/21 22:06
 *
 * 返回客户端信息实体
 */

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Message implements Serializable {
    private Integer code;
    private Object data;
    private long timeStamp;
    /**
     * 请求成功
     * @return 返回信息
     */
    public static Message requestSuccess(Object data){
        long millis = System.currentTimeMillis();
       return new Message(ResponseCode.OK,data,millis);
    }

    /**
     * 请求失败
     * @return 返回信息
     */
    public static Message requestFail(Object data){
        long millis = System.currentTimeMillis();
        return new Message(ResponseCode.ERROR,data,millis);
    }

    /**
     * token权限不足
     * @param data
     */
    public static Message tokenFail(Object data){
        long millis = System.currentTimeMillis();
        return new Message(ResponseCode.TOKENFAIL,data,millis);
    }

    /**
     * token过期
     * @param data
     * @return
     */
    public static Message tokenExpired(Object data){
        long millis = System.currentTimeMillis();
        return new Message(ResponseCode.TOKENEXPIRED,data,millis);
    }
    /**
     * 登录失败
     * @param data
     * @return
     */
    public static Message loginFail(Object data){
        long millis = System.currentTimeMillis();
        return new Message(ResponseCode.LOGINFAIL,data,millis);
    }

    /*
     * 提前退租违约
     */
    public static Message breakRule(Object data){
        long millis = System.currentTimeMillis();
        return new Message(ResponseCode.BREAK,data,millis);
    }

    public static Message addError(Object data){
        long millis = System.currentTimeMillis();
        return new Message(ResponseCode.ADDERROR,data,millis);
    }
}
