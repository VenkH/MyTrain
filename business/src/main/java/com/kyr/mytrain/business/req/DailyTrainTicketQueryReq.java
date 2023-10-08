package com.kyr.mytrain.business.req;

import com.kyr.mytrain.common.dto.PageDto;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

public class DailyTrainTicketQueryReq extends PageDto {

    private String trainCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String start;

    private String end;

    public String getStart() {
        return start;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyTrainTicketQueryReq)) return false;
        if (!super.equals(o)) return false;
        DailyTrainTicketQueryReq that = (DailyTrainTicketQueryReq) o;
        return Objects.equals(getTrainCode(), that.getTrainCode()) && Objects.equals(getDate(), that.getDate()) && Objects.equals(getStart(), that.getStart()) && Objects.equals(getEnd(), that.getEnd()) && Objects.equals(getPage(), that.getPage()) && Objects.equals(getSize(), that.getSize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTrainCode(), getDate(), getStart(), getEnd(), getPage(), getSize());
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }


    @Override
    public String toString() {
        return "DailyTrainTicketQueryReq{" +
                "trainCode='" + trainCode + '\'' +
                ", date=" + date +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                "} " + super.toString();
    }
}
