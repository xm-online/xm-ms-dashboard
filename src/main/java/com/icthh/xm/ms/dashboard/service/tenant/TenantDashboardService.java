package com.icthh.xm.ms.dashboard.service.tenant;

import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.logging.aop.IgnoreLogginAspect;
import com.icthh.xm.ms.dashboard.config.tenant.TenantContext;
import com.icthh.xm.ms.dashboard.config.tenant.TenantInfo;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.service.DashboardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@IgnoreLogginAspect
public class TenantDashboardService {

    private final DashboardService dashboardService;

    /**
     * Create default dashboard.
     * @param tenant - the tenant
     */
    public void create(Tenant tenant) {
        final StopWatch stopWatch = StopWatch.createStarted();
        final String tenantKey = tenant.getTenantKey();
        TenantInfo info = TenantContext.getCurrent();
        try {
            log.info("START - SETUP:CreateTenant:dashboard tenantKey: {}", tenantKey);

            TenantContext.setCurrentQuite(tenantKey);

            Dashboard dashboard = new Dashboard();
            dashboard.setName(tenantKey.toLowerCase());
            dashboard.setOwner(tenantKey.toLowerCase());
            dashboard.setIsPublic(false);
            dashboardService.save(dashboard);

            log.info("STOP  - SETUP:CreateTenant:dashboard tenantKey: {}, result: OK, time = {} ms", tenantKey,
                stopWatch.getTime());
        } catch (Exception e) {
            log.info("STOP  - SETUP:CreateTenant:dashboard tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenantKey, e.getMessage(), stopWatch.getTime());
            throw e;
        } finally {
            TenantContext.setCurrentQuite(info);
        }
    }
}
