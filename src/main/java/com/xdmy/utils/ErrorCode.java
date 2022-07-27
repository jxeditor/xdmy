package com.xdmy.utils;

public enum ErrorCode {

    PARAMS_ERROR(-1, "参数错误!!"),
    UNKNOWN_ERROR(-2, "未知错误!!"),
    DELETE_FAILED(-3, "删除失败!!"),
    MD5_ERROR(-4, "MD5校验错误!!"),
    INSERT_FAILED(-5, "插入失败!!"),
    UPDATE_FAILED(-6, "更新失败!!"),
    VERIFY_ERROR(-7, "账号密码错误!!");


    public Integer code;
    public String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
