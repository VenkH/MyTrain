package com.kyr.mytrain.common.chain;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kyr.mytrain.common.util.ApplicationContextUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AbstractChainContext<T> implements CommandLineRunner {

    /**
     * Key: AbstractChainHandler.Mark
     * Value: AbstractChainHandler
     */
    private final Map<String, List<AbstractChainHandler>> chainHandlerContainer = new HashMap<>();

    public void handler(String mark, T param) {
        List<AbstractChainHandler> abstractChainHandlers = chainHandlerContainer.get(mark);
        if (ObjectUtil.isNull(abstractChainHandlers)) {
            throw new RuntimeException(String.format("[%s] 责任链标识不存在", mark));
        }
        for (AbstractChainHandler chainHandler :
                abstractChainHandlers) {
            chainHandler.handler(param);
        }
    }


    @Override
    public void run(String... args) throws Exception {
        Map<String, AbstractChainHandler> beans = ApplicationContextUtil.getBeansOfType(AbstractChainHandler.class);
        beans.forEach((beanName, bean) -> {
            String mark = bean.mark();
            List<AbstractChainHandler> abstractChainHandlers = chainHandlerContainer.get(mark);
            if (CollUtil.isEmpty(abstractChainHandlers)) {
                abstractChainHandlers = new ArrayList<>();
            }
            abstractChainHandlers.add(bean);
            List<AbstractChainHandler> sortedAbstractChainHandlers = abstractChainHandlers.stream()
                    .sorted(Comparator.comparing(AbstractChainHandler::getOrder))
                    .collect(Collectors.toList());
            chainHandlerContainer.put(mark, sortedAbstractChainHandlers);
        });
    }
}
