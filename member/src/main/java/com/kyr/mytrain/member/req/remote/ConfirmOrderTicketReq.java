package com.kyr.mytrain.member.req.remote;

import jakarta.validation.constraints.NotNull;

public class ConfirmOrderTicketReq {

    @NotNull(message = "【乘客ID】不能为空")
    private Long passengerId;

    @NotNull(message = "【乘客票种】不能为空")
    private String passengerType;

    @NotNull(message = "【乘客名称】不能为空")
    private String passengerName;

    @NotNull(message = "【乘客身份证】不能为空")
    private String passengerIdCard;

    private String seat;

    @NotNull(message = "【座位类型】不能为空")
    private String seatTypeCode;

    public String getSeatTypeCode() {
        return seatTypeCode;
    }

    public void setSeatTypeCode(String seatTypeCode) {
        this.seatTypeCode = seatTypeCode;
    }

    @Override
    public String toString() {
        return "ConfirmOrderTicketReq{" +
                "passengerId=" + passengerId +
                ", passengerType='" + passengerType + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", passengerIdCard='" + passengerIdCard + '\'' +
                ", seat='" + seat + '\'' +
                ", seatTypeCode='" + seatTypeCode + '\'' +
                '}';
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerIdCard() {
        return passengerIdCard;
    }

    public void setPassengerIdCard(String passengerIdCard) {
        this.passengerIdCard = passengerIdCard;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }
}
