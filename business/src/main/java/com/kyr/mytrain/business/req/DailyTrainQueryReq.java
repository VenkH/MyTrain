package com.kyr.mytrain.business.req;

import com.kyr.mytrain.common.dto.PageDto;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainQueryReq extends PageDto {

    private String code;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    public Date getDate() {
        return date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DailyTrainQueryReq{" +
                "} " + super.toString();
    }
}
