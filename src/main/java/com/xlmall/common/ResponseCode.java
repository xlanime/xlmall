package com.xlmall.common;

//定义枚举类
public enum  ResponseCode {
    //声明枚举属性
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),//需要登录
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");//参数错误

    //枚举类私有属性
    private final int code;
    private final String desc;

    //构造方法
    ResponseCode(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    //获取枚举类的对应属性
    public int getCode(){
        return this.code;
    }

    public String getDesc(){
        return this.desc;
    }
}
