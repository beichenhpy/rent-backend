package entity;

/**
 * @author: beichenhpy
 * @Date: 2020/4/14 13:50
 */

public class ResponseCode {

    // 成功
    public static final int OK = 200;
    // 服务器内部错误
    public static final int ERROR = 400;
    //权限问题
    public static final int TOKENFAIL = 410;
    //登录失败
    public static final int LOGINFAIL = 430;
    //token过期
    public static final int TOKENEXPIRED = 510;
    //服务器不在线异常
    public static final int SERVERERROR = 500;
    //提前退租
    public static final int BREAK = 440;
    public static final int ADDERROR = 450;
}
