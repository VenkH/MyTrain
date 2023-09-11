package com.kyr.mytrain.business.enums;

import java.util.ArrayList;
import java.util.List;

public enum SeatColEnum {
    YDZ_A("A","A","1"),
    YDZ_B("B","B","1"),
    YDZ_C("C","C","1"),
    YDZ_D("D","D","1"),
    EDZ_A("A","A","2"),
    EDZ_B("B","B","2"),
    EDZ_C("C","C","2"),
    EDZ_D("D","D","2"),
    EDZ_E("E","E","2"),
    RW_A("A","A","3"),
    RW_B("B","B","3"),
    YW_A("A","A","4"),
    YW_B("B","B","4");


    private String code;
    private String desc;

    /**
     * 对应SeatTypeEnum.code
     */
    private String type;

    @Override
    public String toString() {
        return "SeatColEnum{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    SeatColEnum(String code, String desc, String type) {
        this.code = code;
        this.desc = desc;
        this.type = type;
    }

    public static List<SeatColEnum> getColsByType(String seatType) {
        List<SeatColEnum> colList = new ArrayList<>();
        SeatColEnum[] enums = SeatColEnum.values();
        for (SeatColEnum enum1: enums) {
            if (seatType.equals(enum1.getType())) {
                colList.add(enum1);
            }
        }
        return colList;
    }
}
