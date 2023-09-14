package com.kyr.mytrain.common.exception;

import com.kyr.mytrain.common.constant.BasicExceptionEnum;
import com.kyr.mytrain.common.constant.ExceptionInterface;

public class BusinessException extends RuntimeException{
    private ExceptionInterface anEnum;

    public BusinessException(ExceptionInterface anEnum) {
        this.anEnum = anEnum;
    }

    public ExceptionInterface getAnEnum() {
        return anEnum;
    }

    public void setAnEnum(BasicExceptionEnum anEnum) {
        this.anEnum = anEnum;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "anEnum=" + anEnum +
                '}';
    }
}
