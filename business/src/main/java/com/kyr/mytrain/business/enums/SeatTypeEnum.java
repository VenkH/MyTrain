package com.kyr.mytrain.business.enums;

import java.math.BigDecimal;

public enum SeatTypeEnum {

    YDZ("1", "一等座", new BigDecimal("0.4")),
    EDZ("2", "二等座", new BigDecimal("0.3")),
    RW("3", "软卧", new BigDecimal("0.6")),
    YW("4", "硬卧", new BigDecimal("0.5"));


    private String code;
    private String desc;
    /**
     * 基础票价 N源/公里，0.4即0.4元/公里
     */
    private BigDecimal price;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public static SeatTypeEnum getEnumByCode(String code) {
        for (SeatTypeEnum value : SeatTypeEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    SeatTypeEnum(String code, String desc, BigDecimal price) {
        this.code = code;
        this.desc = desc;
        this.price = price;
    }

    @Override
    public String toString() {
        return "SeatTypeEnum{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                ", price=" + price +
                '}';
    }
}
