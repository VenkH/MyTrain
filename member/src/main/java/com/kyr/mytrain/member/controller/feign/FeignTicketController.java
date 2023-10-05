package com.kyr.mytrain.member.controller.feign;

import com.kyr.mytrain.common.req.MemberTicketSaveReq;
import com.kyr.mytrain.common.resp.CommonResp;
import com.kyr.mytrain.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign/ticket")
public class FeignTicketController {


    @Resource
    private TicketService ticketService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody MemberTicketSaveReq req) {
        ticketService.save(req);
        return new CommonResp<>();
    }
}
