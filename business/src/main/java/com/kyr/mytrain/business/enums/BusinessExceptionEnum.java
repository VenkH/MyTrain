package com.kyr.mytrain.business.enums;

import com.kyr.mytrain.common.constant.ExceptionInterface;

public enum BusinessExceptionEnum implements ExceptionInterface {

    SEAT_COUNT_OR_COL_ERROR("座位总数或者列数错误！"),
    CARRIAGE_INDEX_EXIST("该车次厢号重复！"),
    STATION_NAME_EXIST("车站已存在!");

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
