package entity;

/**
 * @author beichenhpy
 * 响应 Message 信息定义类
 */
public class ResponseConstant {
    private ResponseConstant() {}

    public static final String REQUEST_SUCCESS            = "请求成功";
    public static final String REQUEST_FAILED             = "请求失败";
    public static final String OAUTH_TOKEN_MISSING        = "token 缺失";
    public static final String OAUTH_TOKEN_ILLEGAL        = "已过期请重新登录";
    public static final String OAUTH_TOKEN_DENIED         = "权限不足";
    public static final String UPDATE_SUCCESS           = "修改成功";
    public static final String INSERT_SUCCESS           = "添加成功";
    public static final String DELETE_SUCCESS           = "删除成功";
    public static final String GETCODE_FAILED           = "获取验证码失败/已失效";
    public static final String GETCODE_SUCCESS           = "获取验证码成功";
    public static final String CODE_WRONG           = "验证码错误";
    public static final String CODE_RIGHT           = "验证码正确";
    public static final String SERVER_ERROR           = "服务器异常";
    public static final String NOPAY           = "有账单未支付/未确认支付";
    public static final String UNSUCCEED           = "未认证";
    public static final String REGISTOK           = "注册成功";
    public static final String LOGINFAIL           = "登录失败";
    public static final String ASKDEPOSITSUCCESS           = "成功申请退还押金";
    public static final String OWNERNOCHECK           = "房东未确认支付";
    public static final String CONFIRMOK           = "确认支付成功";
    public static final String OUTRENTOK           = "成功退租";
    public static final String UNRENT           = "未退租";
    public static final String CANCELOK           = "取消订单成功";
    public static final String ADDORDEROK           = "添加订单成功";
    public static final String ADDORDERERROR           = "添加订单失败";
    public static final String ADDRENTERROR           = "添加出租信息失败";
    public static final String NOUSER           = "用户不存在";
    public static final String UPLOADOK           = "上传成功";
    public static final String HOUSEDELETEFAILD           = "房屋锁定不能删除";
    public static final String ORDERDELETEFAILD           = "订单锁定不能删除";
    public static final String ORDERCANCELFAILD           = "订单锁定不能取消";
    public static final String RENTUPDATEFAILD           = "出租信息锁定不能更改或删除";
    public static final String ADDCOMMENTOK           = "留言成功";
    public static final String REAPEATADDERROR           = "已经添加了，请勿重复添加";
    public static final String DELETEFAIL           = "删除失败";
    public static final String DELETEProvince           = "删除失败,还有出租房屋";
    public static final String UPDATEFAIL           = "更新失败，检查是否有重复项";
    public static final String UPDATEADMINHOUSE           = "更新失败，还有出租房屋";



}