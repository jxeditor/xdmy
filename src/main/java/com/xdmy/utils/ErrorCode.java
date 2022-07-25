package com.xdmy.utils;

public enum ErrorCode {

    PARAMS_ERROR(-1, "Params error!!"),
    UNKNOWN_ERROR(-2, "Unknown error!!"),
    DELETE_FAILED(-3, "Delete failed!!"),
    MD5_ERROR(-4, "md5 verified error!!"),
    INSERT_ERROR(-5,"Insert failed!!");

    public Integer code;
    public String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
