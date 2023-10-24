package com.kyr.mytrain.business.service.handler.ticket.purchase;

import com.kyr.mytrain.business.req.ConfirmOrderDoReq;
import org.springframework.stereotype.Component;

/**
 *购票流程过滤器之验证参数是否有效
 * 比如车次、车站等信息是否有效
 */
@Component
public class TicketPurchaseParamVerifyChainHandler implements AbstractTicketPurchaseChainHandler{
    @Override
    public void handler(ConfirmOrderDoReq param) {

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
