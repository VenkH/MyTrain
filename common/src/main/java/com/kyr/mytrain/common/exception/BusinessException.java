package com.kyr.mytrain.common.exception;

import com.kyr.mytrain.common.constant.BusinessExceptionEnum;

public class BusinessException extends RuntimeException{
    private BusinessExceptionEnum anEnum;

    public BusinessException(BusinessExceptionEnum anEnum) {
        this.anEnum = anEnum;
    }

    public BusinessExceptionEnum getAnEnum() {
        return anEnum;
    }

    public void setAnEnum(BusinessExceptionEnum anEnum) {
        this.anEnum = anEnum;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "anEnum=" + anEnum +
                '}';
    }
}
