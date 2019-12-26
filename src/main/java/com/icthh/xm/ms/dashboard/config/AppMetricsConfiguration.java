package com.icthh.xm.ms.dashboard.config;

import com.codahale.metrics.MetricRegistry;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import com.zaxxer.hikari.HikariDataSource;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableMetrics(proxyTargetClass = true)
public class AppMetricsConfiguration extends MetricsConfigurerAdapter {

    private final MetricRegistry metricRegistry;

    private HikariDataSource hikariDataSource;

    public AppMetricsConfiguration(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Autowired(required = false)
    public void setHikariDataSource(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @PostConstruct
    public void init() {
        if (hikariDataSource != null) {
            log.debug("Monitoring the datasource");
            // remove the factory created by HikariDataSourceMetricsPostProcessor until JHipster migrate to Micrometer
            hikariDataSource.setMetricsTrackerFactory(null);
            hikariDataSource.setMetricRegistry(metricRegistry);
        }
    }
}
