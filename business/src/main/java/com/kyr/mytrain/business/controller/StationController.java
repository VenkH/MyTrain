package com.kyr.mytrain.business.controller;

import com.kyr.mytrain.business.req.StationQueryReq;
import com.kyr.mytrain.business.resp.StationQueryResp;
import com.kyr.mytrain.business.service.StationService;
import com.kyr.mytrain.common.resp.CommonResp;
import com.kyr.mytrain.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/station")
public class StationController {

    @Resource
    private StationService stationService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<StationQueryResp>> queryList(@Valid StationQueryReq req) {
        PageResp<StationQueryResp> list = stationService.queryList(req);
        return new CommonResp<>(list);
    }

    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryAll() {
        List<StationQueryResp> resp = stationService.queryAll();
        return new CommonResp<>(resp);
    }

}
