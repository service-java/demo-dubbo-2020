package com.alibaba.dubbo.samples.echo.refer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.samples.echo.api.EchoService;

import org.springframework.stereotype.Component;

/**
 * @author yiji@apache.org
 */
@Component
public class EchoConsumer {

    @Reference
    private EchoService echoService;

    public String echo(String name) {
        return echoService.echo(name);
    }
}
