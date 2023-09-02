package com.kyr.mytrain.common.util;

import cn.hutool.core.util.IdUtil;

public class SnowUtil {

    /**
     * 机器标识
     */
    private static long workId = 1;
    /**
     * 数据中心
     */
    private static long dataCenterId = 1;

    public static Long getSnowIdLong() {
        return IdUtil.getSnowflake(workId, dataCenterId).nextId();
    }
    public static String getSnowIdStr() {
        return IdUtil.getSnowflake(workId, dataCenterId).nextIdStr();
    }
}
