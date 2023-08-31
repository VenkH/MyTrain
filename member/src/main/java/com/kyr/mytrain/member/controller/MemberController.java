package com.kyr.mytrain.member.controller;

import com.kyr.mytrain.common.resp.CommonResp;
import com.kyr.mytrain.member.dto.MemberRegisterDto;
import com.kyr.mytrain.member.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @PostMapping("/register")
    public CommonResp<Long> register(MemberRegisterDto mobile) {
        return memberService.register(mobile);
    }
}
