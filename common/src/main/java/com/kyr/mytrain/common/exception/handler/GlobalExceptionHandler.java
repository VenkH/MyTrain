package com.kyr.mytrain.common.exception.handler;

import cn.hutool.core.util.StrUtil;
import com.kyr.mytrain.common.exception.BusinessException;
import com.kyr.mytrain.common.resp.CommonResp;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理、数据预处理等
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 所有异常统一处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResp exceptionHandler(Exception e) throws Exception{
        if (StrUtil.isNotBlank(RootContext.getXID())) {
            // 如果是全局事务出现了异常，就不要包装返回值，把异常抛出，让调用方接收
            log.info("出现全局事务异常！Seata全局事务ID:{}", RootContext.getXID());
            throw e;
        }

        log.error("系统出错，异常信息为：" + e);
        CommonResp commonResp = CommonResp.generateErrorResp("系统错误，请联系管理员！");
        return commonResp;
    }

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public CommonResp businessExceptionHandler(BusinessException e) {
        log.warn("【业务异常】：" + e.getAnEnum().getDesc());
        CommonResp commonResp = CommonResp.generateErrorResp(e.getAnEnum().getDesc());
        return commonResp;
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonResp businessExceptionHandler(BindException e) {
        StringBuilder errors = new StringBuilder();
        for (ObjectError error: e.getBindingResult().getAllErrors()) {
            errors.append(error.getDefaultMessage());
        }
        log.warn("【校验异常】：" + errors );
        CommonResp commonResp = CommonResp.generateErrorResp(errors.toString());
        return commonResp;
    }

}
