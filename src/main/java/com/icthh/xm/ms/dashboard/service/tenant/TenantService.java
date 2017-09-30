package com.icthh.xm.ms.dashboard.service.tenant;

import static org.apache.commons.lang3.time.StopWatch.createStarted;

import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.logging.aop.IgnoreLogginAspect;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@IgnoreLogginAspect
public class TenantService {

    private final TenantDatabaseService tenantDatabaseService;
    private final TenantDashboardService tenantDashboardService;

    /**
     * Create tenant.
     *
     * @param tenant the tenant
     * @return the tenant
     */
    public Tenant createTenant(Tenant tenant) {
        StopWatch stopWatch = createStarted();
        log.info("START - SETUP:CreateTenant: tenantKey: {}", tenant.getTenantKey());

        try {
            tenantDatabaseService.create(tenant);
            tenantDashboardService.create(tenant);
            log.info("STOP  - SETUP:CreateTenant: tenantKey: {}, result: OK, time = {} ms",
                tenant.getTenantKey(), stopWatch.getTime());
            return tenant;
        } catch (Exception e) {
            log.info("STOP  - SETUP:CreateTenant: tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenant.getTenantKey(), e.getMessage(), stopWatch.getTime());
            throw e;
        }
    }

    /**
     * Delete tenant.
     *
     * @param tenantKey - the tenant key
     */
    @SneakyThrows
    public void deleteTenant(String tenantKey) {
        StopWatch stopWatch = createStarted();
        log.info("START - SETUP:DeleteTenant: tenantKey: {}", tenantKey);

        try {
            tenantDatabaseService.drop(tenantKey);
            log.info("STOP  - SETUP:DeleteTenant: tenantKey: {}, time = {} ms", tenantKey, stopWatch.getTime());
        } catch (Exception e) {
            log.info("STOP  - SETUP:DeleteTenant: tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenantKey, e.getMessage(), stopWatch.getTime());
            throw e;
        }
    }

    public void manageTenant(String tenantKey, String state) {
        StopWatch stopWatch = createStarted();
        log.info("START - SETUP:ManageTenant: tenantKey: {}, state: {}", tenantKey, state);

        try {
            tenantDatabaseService.drop(tenantKey);
            log.info("STOP  - SETUP:ManageTenant: tenantKey: {}, state: {}, time = {} ms",
                tenantKey, state, stopWatch.getTime());
        } catch (Exception e) {
            log.info("STOP  - SETUP:ManageTenant: tenantKey: {}, state: {}, result: FAIL, error: {}, time = {} ms",
                tenantKey, state, e.getMessage(), stopWatch.getTime());
            throw e;
        }
    }
}
