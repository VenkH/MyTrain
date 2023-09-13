package com.kyr.mytrain.business.req;

import com.kyr.mytrain.common.dto.PageDto;

public class TrainStationQueryReq extends PageDto {

    private String trainCode;

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    @Override
    public String toString() {
        return "TrainStationQueryReq{" +
                "trainCode='" + trainCode + '\'' +
                '}' + super.toString();
    }
}
