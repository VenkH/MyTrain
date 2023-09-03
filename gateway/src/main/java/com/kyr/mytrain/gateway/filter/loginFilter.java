package com.kyr.mytrain.gateway.filter;

import cn.hutool.core.util.ObjectUtil;
import com.kyr.mytrain.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class loginFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (path.contains("/admin")
                || path.contains("/login")
                || path.contains("/sendCode")) {
            log.info("【放行】不需要登录校验的接口：{}", path);
            return chain.filter(exchange);
        } else {
            log.info("【校验中】需要登录校验的接口：{}", path);
        }

        String token = exchange.getRequest().getHeaders().getFirst("token");

        if (ObjectUtil.isNull(token) || ObjectUtil.isEmpty(token)) {
            log.info("【拦截】token为空，请求被拦截");
            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
            return exchange.getResponse().setComplete();
        }

        if (JwtUtil.validate(token)) {
            log.info("【放行】token有效，请求通过");
            return chain.filter(exchange);
        } else {
            log.info("【拦截】token无效，请求被拦截");
            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        // 返回的值越小，优先级越高
        return 0;
    }
}
