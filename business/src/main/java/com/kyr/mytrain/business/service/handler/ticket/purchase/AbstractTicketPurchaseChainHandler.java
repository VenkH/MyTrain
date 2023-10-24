package com.kyr.mytrain.business.service.handler.ticket.purchase;

import com.kyr.mytrain.business.req.ConfirmOrderDoReq;
import com.kyr.mytrain.common.chain.AbstractChainHandler;
import com.kyr.mytrain.common.constant.TicketChainMark;

/**
 * 抽象购买车票责任链组件
 */
public interface AbstractTicketPurchaseChainHandler extends AbstractChainHandler<ConfirmOrderDoReq>{
    @Override
    default String mark() {
        return TicketChainMark.TICKET_PURCHASE_MARK.name();
    }
}
