package com.kyr.mytrain.business.enums;

import com.kyr.mytrain.common.constant.ExceptionInterface;

public enum BusinessExceptionEnum implements ExceptionInterface {

    SEAT_COUNT_OR_COL_ERROR("座位总数或者列数错误！");

    private String desc;

    BusinessExceptionEnum(String desc) {
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
