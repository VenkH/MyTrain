package com.kyr.mytrain.member.controller;

import com.kyr.mytrain.common.resp.CommonResp;
import com.kyr.mytrain.member.dto.PassengerDto;
import com.kyr.mytrain.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Resource
    private PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp<Object> sendCode(@Valid @RequestBody PassengerDto passenger) {
        passengerService.savePassenger(passenger);
        return new CommonResp<>();
    }
}
