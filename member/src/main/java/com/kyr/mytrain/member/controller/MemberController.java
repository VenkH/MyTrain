package com.kyr.mytrain.member.controller;

import com.kyr.mytrain.common.resp.CommonResp;
import com.kyr.mytrain.member.dto.LoginDto;
import com.kyr.mytrain.member.dto.SendCodeDto;
import com.kyr.mytrain.member.resp.MemberLoginResp;
import com.kyr.mytrain.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @PostMapping("/sendCode")
    public CommonResp<Object> sendCode(@Valid @RequestBody SendCodeDto mobile) {
        return memberService.sendCode(mobile);
    }

    @PostMapping("/login")
    public CommonResp<MemberLoginResp> login(@Valid @RequestBody LoginDto loginDto) {
        return memberService.login(loginDto);
    }
}
