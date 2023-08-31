package com.kyr.mytrain.member.controller;

import com.kyr.mytrain.member.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/count")
    public int count() {
        return memberService.count();
    }
}
