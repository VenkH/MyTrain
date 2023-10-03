package com.kyr.mytrain.member.controller;

import com.kyr.mytrain.common.context.LoginContext;
import com.kyr.mytrain.common.resp.CommonResp;
import com.kyr.mytrain.common.resp.PageResp;
import com.kyr.mytrain.member.dto.PassengerQueryDto;
import com.kyr.mytrain.member.dto.PassengerSaveDto;
import com.kyr.mytrain.member.resp.PassengerQueryResp;
import com.kyr.mytrain.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Resource
    private PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp<Object> sendCode(@Valid @RequestBody PassengerSaveDto passenger) {
        passengerService.savePassenger(passenger);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<PassengerQueryResp>> queryList(@Valid PassengerQueryDto passengerQueryDto) {
        passengerQueryDto.setMemberId(LoginContext.getId());
        PageResp<PassengerQueryResp> list = passengerService.queryPassenger(passengerQueryDto);
        return new CommonResp(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        passengerService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query-mine")
    public CommonResp<List<PassengerQueryResp>> queryMine() {
        return new CommonResp<>(passengerService.queryMine());
    }

}
