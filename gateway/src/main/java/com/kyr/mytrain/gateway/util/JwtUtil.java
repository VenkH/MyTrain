package com.kyr.mytrain.gateway.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    private static final String KEY = "asdjkl!@159123";

    public static String createToken(Object object) {
        Map<String, Object> map = BeanUtil.beanToMap(object);
        DateTime now = DateTime.now();
        DateTime dateTime = now.offsetNew(DateField.SECOND, 3);
        // 签发时间
        map.put(JWTPayload.ISSUED_AT, now);
        // 过期时间
        map.put(JWTPayload.EXPIRES_AT, dateTime);
        // 生效时间
        map.put(JWTPayload.NOT_BEFORE, now);
        log.info("生成token，token中携带的数据是：" + object.toString());
        String token = JWTUtil.createToken(map, KEY.getBytes(StandardCharsets.UTF_8));
        return token;
    }

    public static JSONObject getJSONObject(String token) {

        JWT jwt = JWTUtil.parseToken(token).setKey(KEY.getBytes(StandardCharsets.UTF_8));

        JSONObject payloads = jwt.getPayloads();
        payloads.remove(JWTPayload.ISSUED_AT);
        payloads.remove(JWTPayload.EXPIRES_AT);
        payloads.remove(JWTPayload.NOT_BEFORE);
        log.info("解析token，token中携带的数据是：" + payloads);
        return payloads;
    }

    public static boolean validate(String token) {
        try {
            JWT jwt = JWTUtil.parseToken(token).setKey(KEY.getBytes(StandardCharsets.UTF_8));
            // 校验token是否合法且在有效期内
            boolean validate = jwt.validate(0);
            return validate;
        } catch (Exception e) {
            log.warn("token校验异常：" + e);
            return false;
        }
    }

}
