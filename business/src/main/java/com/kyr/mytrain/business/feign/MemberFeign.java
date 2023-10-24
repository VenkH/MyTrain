package com.kyr.mytrain.business.feign;


import com.kyr.mytrain.business.dto.remote.Ticket;
import com.kyr.mytrain.business.req.ConfirmOrderDoReq;
import com.kyr.mytrain.common.req.MemberTicketSaveReq;
import com.kyr.mytrain.common.resp.CommonResp;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("member-service")
public interface MemberFeign {

    @PostMapping("/member-service/feign/ticket/save")
    CommonResp<Object> save(@Valid @RequestBody MemberTicketSaveReq req);

    @GetMapping("member-service/feign/query-bought-ticket")
    CommonResp<List<Ticket>> queryBoughtTicket(ConfirmOrderDoReq req);

}
