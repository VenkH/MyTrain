package com.kyr.mytrain.business.service.handler.ticket.purchase;

import cn.hutool.core.collection.CollUtil;
import com.kyr.mytrain.business.dto.remote.Ticket;
import com.kyr.mytrain.business.enums.BusinessExceptionEnum;
import com.kyr.mytrain.business.feign.MemberFeign;
import com.kyr.mytrain.business.req.ConfirmOrderDoReq;
import com.kyr.mytrain.common.exception.BusinessException;
import com.kyr.mytrain.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 购票流程过滤器之验证车票是否重复
 * 检查每一个乘车人是否已有车票数据
 */
@Component
public class TicketPurchaseOrderRepeatChainHandler implements AbstractTicketPurchaseChainHandler{

    @Resource
    MemberFeign memberFeign;

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void handler(ConfirmOrderDoReq param) {
        CommonResp<List<Ticket>> listCommonResp = memberFeign.queryBoughtTicket(param);
        if (CollUtil.isNotEmpty(listCommonResp.getContent())) {
            throw new BusinessException(BusinessExceptionEnum.TICKET_REPEAT_EXCEPTION);
        }
    }
}
