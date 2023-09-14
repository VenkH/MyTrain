package com.kyr.mytrain.member.enums;

import com.kyr.mytrain.common.constant.ExceptionInterface;

public enum MemberExceptionEnum implements ExceptionInterface {
    MOBILE_EXIST("手机号已被注册！");

    private String desc;

    MemberExceptionEnum(String desc) {
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
        return "MemberExceptionEnum{" +
                "desc='" + desc + '\'' +
                '}';
    }


}
