package com.kyr.mytrain.business.mapper;

import java.util.Date;

public interface AfterConfirmOrderMapper {
    int updateDailyTrainTicket(
            Date date,
            String trainCode,
            String seatType,
            Integer minStartIndex,
            Integer maxStartIndex,
            Integer minEndIndex,
            Integer maxEndIndex
    );
}