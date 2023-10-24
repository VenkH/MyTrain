package com.kyr.mytrain.member.controller.feign;

import com.kyr.mytrain.common.req.MemberTicketSaveReq;
import com.kyr.mytrain.common.resp.CommonResp;
import com.kyr.mytrain.member.domain.Ticket;
import com.kyr.mytrain.member.req.remote.ConfirmOrderDoReq;
import com.kyr.mytrain.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feign/ticket")
public class FeignTicketController {


    @Resource
    private TicketService ticketService;

    @PostMapping("/save")
    public CommonResp<Object> save (@Valid @RequestBody MemberTicketSaveReq req) throws Exception {
        ticketService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-bought-ticket")
    public CommonResp<List<Ticket>> queryBoughtTicket(ConfirmOrderDoReq req) {
        return new CommonResp<>(ticketService.queryBoughtTicket(req));
    }
}
