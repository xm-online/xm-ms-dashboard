package com.icthh.xm.ms.dashboard.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(2)
@Component
public class BulkDashboardValidationFilter implements Filter {

    @Value("${bulk.dashboard.items.size:100}")
    private Long batchSize;

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {

            isAllowedBulkItemSize(request);

            chain.doFilter(request, response);

        } catch (Exception ex) {
            log.info("Could not validate bulk request {}", ex);
        }
    }

    private void isAllowedBulkItemSize(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    }

    @Override
    public void destroy() {

    }
}
