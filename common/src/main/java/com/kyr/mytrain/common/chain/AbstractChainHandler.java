package com.kyr.mytrain.common.chain;

import org.springframework.core.Ordered;

/**
 * 抽象责任链接口
 * @param <T>
 */
public interface AbstractChainHandler<T> extends Ordered {

    /**
     * 责任链中具体某个环节的处理方法
     * @param param 形参
     */
    void handler(T param);

    /**
     *
     * @return 组件标识，用于标识不同的责任链
     */
    String mark();
}
