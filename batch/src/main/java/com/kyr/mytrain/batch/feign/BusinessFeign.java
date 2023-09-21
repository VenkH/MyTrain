package com.kyr.mytrain.batch.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="business-service", url= "http://127.0.0.1:8002/business-service/station")
public interface BusinessFeign {

    @GetMapping("/hello")
    String hello();
}
