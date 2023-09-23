package com.kyr.mytrain.batch.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.kyr.mytrain.batch.feign.BusinessFeign;
import com.kyr.mytrain.common.resp.CommonResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;

import java.util.Date;

@Slf4j
public class DailyTrainJob implements Job {
    @Resource
    private BusinessFeign businessFeign;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        log.info("开始生成15天后车次数据");
        Date date = new Date();
        DateTime offset = DateUtil.offsetDay(date, 15);
        Date JDKDate = offset.toJdkDate();
        CommonResp<Object> objectCommonResp = businessFeign.genDailyTrain(JDKDate);
        log.info("结束生成15天后车次数据，结果：{}", objectCommonResp);

    }
}
