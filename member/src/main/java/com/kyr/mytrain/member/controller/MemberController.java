package com.kyr.mytrain.member.controller;

import com.kyr.mytrain.common.resp.CommonResp;
import com.kyr.mytrain.member.dto.LoginDto;
import com.kyr.mytrain.member.dto.MemberRegisterDto;
import com.kyr.mytrain.member.dto.SendCodeDto;
import com.kyr.mytrain.member.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @PostMapping("/register")
    public CommonResp<Long> register(@Validated MemberRegisterDto mobile) {
        return memberService.register(mobile);
    }

    @PostMapping("/sendCode")
    public CommonResp sendCode(@Validated SendCodeDto mobile) {return memberService.sendCode(mobile);}

    @PostMapping("/login")
    public CommonResp login(@Validated LoginDto loginDto) {return memberService.login(loginDto);}
}
