package com.icthh.xm.ms.dashboard.service.tenant;

import static com.icthh.xm.ms.dashboard.config.Constants.CHANGE_LOG_PATH;
import static org.apache.commons.lang3.time.StopWatch.createStarted;

import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.logging.aop.IgnoreLogginAspect;
import com.icthh.xm.commons.migration.db.tenant.DropSchemaResolver;


import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;

import com.icthh.xm.commons.migration.db.util.DatabaseUtil;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@IgnoreLogginAspect
public class TenantDatabaseService {

    private DataSource dataSource;
    private LiquibaseProperties liquibaseProperties;
    private ResourceLoader resourceLoader;
    private DropSchemaResolver schemaDropResolver;

    /**
     * Create database schema for tenant.
     *
     * @param tenant - the tenant
     */
    public void create(Tenant tenant) {
        final StopWatch stopWatch = createStarted();
        final String tenantKey = tenant.getTenantKey();

        log.info("START - SETUP:CreateTenant:schema tenantKey: {}", tenantKey);
        DatabaseUtil.createSchema(dataSource, tenantKey);
        log.info("STOP  - SETUP:CreateTenant:schema tenantKey: {}, time = {} ms", tenantKey,
            stopWatch.getTime());
        try {
            stopWatch.reset();
            stopWatch.start();
            log.info("START - SETUP:CreateTenant:liquibase tenantKey: {}", tenantKey);
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setResourceLoader(resourceLoader);
            liquibase.setDataSource(dataSource);
            liquibase.setChangeLog(CHANGE_LOG_PATH);
            liquibase.setContexts(liquibaseProperties.getContexts());
            liquibase.setDefaultSchema(tenantKey);
            liquibase.setDropFirst(liquibaseProperties.isDropFirst());
            liquibase.setShouldRun(true);
            liquibase.afterPropertiesSet();
            log.info("STOP  - SETUP:CreateTenant:liquibase tenantKey: {}, result: OK, time = {} ms", tenantKey,
                stopWatch.getTime());
        } catch (LiquibaseException e) {
            log.info("STOP  - SETUP:CreateTenant:liquibase tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenantKey, e.getMessage(), stopWatch.getTime());
            throw new RuntimeException("Can not migrate database for creation tenant " + tenantKey, e);
        }
    }

    /**
     * Drop database schema for tenant.
     *
     * @param tenantKey - the tenant key
     */
    @SneakyThrows
    public void drop(String tenantKey) {
        StopWatch stopWatch = createStarted();
        log.info("START - SETUP:DeleteTenant:schema tenantKey: {}", tenantKey);
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format(schemaDropResolver.getSchemaDropCommand(), tenantKey));
            log.info("STOP  - SETUP:DeleteTenant:schema tenantKey: {}, result: OK, time = {} ms",
                tenantKey, stopWatch.getTime());
        } catch (Exception e) {
            log.info("STOP  - SETUP:DeleteTenant:schema tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenantKey, e.getMessage(), stopWatch.getTime());
            throw e;
        }
    }
}
