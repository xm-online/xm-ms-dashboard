package com.icthh.xm.ms.dashboard.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for RestTemplate beans.
 */
@Configuration
public class RestTemplateConfiguration {

    @LoadBalanced
    @Bean
    public RestTemplate loadBalancedRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * To propagate traceId across third services with RestTemplate client,
     * rest template should be created using builder
     */
    @Bean
    public RestTemplate plainRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
