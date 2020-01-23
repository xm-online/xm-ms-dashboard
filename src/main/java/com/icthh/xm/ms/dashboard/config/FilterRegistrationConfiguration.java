package com.icthh.xm.ms.dashboard.config;

import com.icthh.xm.ms.dashboard.web.filter.BulkDashboardValidationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterRegistrationConfiguration {

    private final BulkDashboardValidationFilter bulkDashboardValidationFilter;

    @Bean
    public FilterRegistrationBean<BulkDashboardValidationFilter> loggingFilter(){
        FilterRegistrationBean<BulkDashboardValidationFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(bulkDashboardValidationFilter);
        registrationBean.addUrlPatterns("/api/bulk/**");

        return registrationBean;
    }
}
