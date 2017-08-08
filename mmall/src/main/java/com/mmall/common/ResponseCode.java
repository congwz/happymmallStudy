package com.mmall.common;

/**
 * Created by Administrator on 2017/6/6 0006.
 */
/*
public class ResponseCode {
}
*/

//响应扩展的枚举类（status的Code）
public enum ResponseCode {


    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;  //描述

    ResponseCode(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    //把code、desc开放出去
    public int getCode(){
        return code;
    }
    public String getDesc(){
        return desc;
    }
}
