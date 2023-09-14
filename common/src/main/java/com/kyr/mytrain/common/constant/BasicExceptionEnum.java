package com.kyr.mytrain.common.constant;

public enum BasicExceptionEnum implements ExceptionInterface{
    MOBILE_NOT_EXIST("请先获取验证码"),
    CODE_ERROR("短信验证码错误"),

    TOKEN_EXPIRE("token已过期");



    private String desc;

    BasicExceptionEnum(String desc) {
        this.desc = desc;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "BusinessExceptionEnum{" +
                "desc='" + desc + '\'' +
                '}';
    }
}
