package com.icthh.xm.ms.dashboard.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.icthh.xm.ms.dashboard")
public class FeignConfiguration {

}
