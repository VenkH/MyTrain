package com.kyr.mytrain.business.service;

import com.kyr.mytrain.business.domain.DailyTrainSeat;
import com.kyr.mytrain.business.domain.DailyTrainTicket;
import com.kyr.mytrain.business.mapper.AfterConfirmOrderMapper;
import com.kyr.mytrain.business.mapper.DailyTrainSeatMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AfterConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);


    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Resource
    private AfterConfirmOrderMapper afterConfirmOrderMapper;

    /**
     * 确认订单后保存数据到数据库
     * * 选座表修改售卖情况Sell
     * * 余票详情表修改余票
     * * 为会员增加购票记录
     * * 更新订单状态为成功
     */
    @Transactional
    public void afterDoConfirm(List<DailyTrainSeat> finalSeatList, DailyTrainTicket dailyTrainTicket) {

        Date date = dailyTrainTicket.getDate();
        String trainCode = dailyTrainTicket.getTrainCode();


        for (DailyTrainSeat dailyTrainSeat : finalSeatList) {

            DailyTrainSeat trainSeat = new DailyTrainSeat();
            trainSeat.setId(dailyTrainSeat.getId());
            trainSeat.setSell(dailyTrainSeat.getSell());
            trainSeat.setUpdateTime(new Date());
            dailyTrainSeatMapper.updateByPrimaryKeySelective(trainSeat);

            // 原来：  010000001
            // 被购买：000111000
            // 最终：  010111001
            // 影响：  xx111111x
            Integer startIndex = dailyTrainTicket.getStartIndex();
            Integer endIndex = dailyTrainTicket.getEndIndex();
            Integer minEndIndex = startIndex + 1;
            Integer maxStartIndex = endIndex - 1;
            Integer minStartIndex = startIndex;
            char[] sellChar = dailyTrainSeat.getSell().toCharArray();
            for (int i = startIndex - 2; i >= 0; i--) {
                if (sellChar[i] == '1') {
                    break;
                } else {
                    minStartIndex--;
                }
            }
            Integer maxEndIndex = endIndex;
            for (int i = endIndex - 1; i < sellChar.length; i++) {
                if (sellChar[i] == '1') {
                    break;
                } else {
                    maxEndIndex++;
                }
            }

            log.info("受影响的起点站：{}-{}", minStartIndex, maxStartIndex);
            log.info("受影响的终点站：{}-{}", minEndIndex, maxEndIndex);

            afterConfirmOrderMapper.updateDailyTrainTicket(
                    date,
                    trainCode,
                    dailyTrainSeat.getSeatType(),
                    minStartIndex,
                    maxStartIndex,
                    minEndIndex,
                    maxEndIndex
            );

        }

    }
}
