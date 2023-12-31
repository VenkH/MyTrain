package com.kyr.mytrain.business.enums;

import com.kyr.mytrain.common.constant.ExceptionInterface;

public enum BusinessExceptionEnum implements ExceptionInterface {

    SEAT_COUNT_OR_COL_ERROR("座位总数或者列数错误！"),
    CARRIAGE_INDEX_EXIST("该车次厢号重复！"),
    STATION_NAME_EXIST("车站已存在!"),
    TRAIN_STATION_TRAINCODE_INDEX_UNIQUE("同车次站序重复！"),
    TRAIN_STATION_TRAINCODE_NAME_UNIQUE("同车次站名重复！"),
    NONE_TRAIN_DATA("没有车次数据！"),

    CONFIRM_ORDER_TICKET_COUNT_ERROR("余票不足！"),
    CONFIRM_ORDER_SEAT_TYPE_ERROR("座位类型不存在！"),
    CONFIRM_ORDER_ERROR("服务器繁忙，请稍后重试！"),
    CONFIRM_ORDER_LOCK_FAIL("购票人数过多，请稍后重试！"),
    CONFIRM_ORDER_FLOW_EXCEPTION("购票人数过多，请稍后重试！"),

    TICKET_REPEAT_EXCEPTION("请勿重复购票"),
    ;

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
