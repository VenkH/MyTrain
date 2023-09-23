package com.kyr.mytrain.batch.feign;

import com.kyr.mytrain.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

@FeignClient(name="business-service", url= "http://127.0.0.1:8002/business-service")
public interface BusinessFeign {

    @GetMapping("/admin/daily-train/gen-daily/{date}")
    CommonResp<Object> genDailyTrain(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);
}
