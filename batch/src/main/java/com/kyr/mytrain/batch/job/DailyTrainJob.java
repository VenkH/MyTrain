package com.kyr.mytrain.batch.job;

import cn.hutool.core.util.RandomUtil;
import com.kyr.mytrain.batch.feign.BusinessFeign;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;

@Slf4j
public class DailyTrainJob implements Job {
    @Resource
    private BusinessFeign businessFeign;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        log.info("开始生成每日火车数据");
        String hello = businessFeign.hello();
        log.info("From Business Module:" + hello);

        log.info("结束生成每日火车数据");

    }
}
