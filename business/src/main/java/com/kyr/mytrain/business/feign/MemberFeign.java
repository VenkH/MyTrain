package com.kyr.mytrain.business.feign;

import com.kyr.mytrain.common.req.MemberTicketSaveReq;
import com.kyr.mytrain.common.resp.CommonResp;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="member-service", url= "http://127.0.0.1:8001/member-service")
public interface MemberFeign {

    @PostMapping("/feign/ticket/save")
    CommonResp<Object> save(@Valid @RequestBody MemberTicketSaveReq req);

}
