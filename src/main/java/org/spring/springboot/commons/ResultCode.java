package org.spring.springboot.commons;

/**
 * 响应码枚举，参考HTTP状态码的语义
 */
public enum ResultCode {
    SUCCESS("200"),//成功
    FAIL("400"),//失败
    UNAUTHORIZED("401", "用户登录身份认证失败"),//未认证
    LOGIN_FAILED("409"),//用户登录认证失败
    CHECK_TIME("408"),//不到开播时间
    NOT_FOUND("404"),//接口不存在
    BONUS_PACKAGE_ERROR("600"),//红包已抢完
    BONUS_ENOUGH_DIAMOND("601"),//红包已抢完
    BONUS_REPEAT_USER("602"),//用户已抢
    BONUS_EXIRED("603"),//红包已过期
    REPEAT_LIKE("402"),//重复点赞
    PARAM_ERROR("405", "参数为空"),//参数错误
    BONUS_LMAX_ERROR("406"),//砖石数超额
    INTERNAL_SERVER_ERROR("500"),
    INVALID_SIGN("403", "参数签名无效");  //无效果的Sgin

    private String code;
    private String message;

    ResultCode(String code) {
        this.code = code;
    }

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

}
