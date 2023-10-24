package com.kyr.mytrain.business.service.handler.ticket.purchase;

import com.kyr.mytrain.business.domain.DailyTrainTicket;
import com.kyr.mytrain.business.enums.BusinessExceptionEnum;
import com.kyr.mytrain.business.enums.SeatTypeEnum;
import com.kyr.mytrain.business.req.ConfirmOrderDoReq;
import com.kyr.mytrain.business.req.ConfirmOrderTicketReq;
import com.kyr.mytrain.business.service.ConfirmOrderService;
import com.kyr.mytrain.business.service.DailyTrainTicketService;
import com.kyr.mytrain.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 购票流程过滤器之验证余票是否充足
 */
@Component
public class TicketPurchaseParamStockChainHandler implements AbstractTicketPurchaseChainHandler{

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    DailyTrainTicketService dailyTrainTicketService;

    @Override
    public void handler(ConfirmOrderDoReq param) {
        // 查出真实的余票记录
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(
                param.getTrainCode(),
                param.getDate(),
                param.getStart(),
                param.getEnd());
        LOG.info("【余票记录-扣减前】一等座：{}，二等座：{}，软卧：{}，硬卧：{}", dailyTrainTicket.getYdz(), dailyTrainTicket.getEdz(), dailyTrainTicket.getRw(), dailyTrainTicket.getYw());

        // 扣减余票，确认余票是否足够
        reduceTicket(param, dailyTrainTicket);
        LOG.info("【余票记录-扣减后】一等座：{}，二等座：{}，软卧：{}，硬卧：{}", dailyTrainTicket.getYdz(), dailyTrainTicket.getEdz(), dailyTrainTicket.getRw(), dailyTrainTicket.getYw());
    }

    @Override
    public int getOrder() {
        return 0;
    }
    private void reduceTicket(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
            SeatTypeEnum enumByCode = SeatTypeEnum.getEnumByCode(ticketReq.getSeatTypeCode());
            switch (enumByCode) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
                default -> throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SEAT_TYPE_ERROR);

            }
        }
    }
}
