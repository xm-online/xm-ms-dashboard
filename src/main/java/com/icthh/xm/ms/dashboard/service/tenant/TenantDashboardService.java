package com.icthh.xm.ms.dashboard.service.tenant;

import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.logging.aop.IgnoreLogginAspect;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
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

    private static final String DEFAULT_TYPE_KEY = "DASHBOARD";

    private final DashboardService dashboardService;
    private final TenantContextHolder tenantContextHolder;

    /**
     * Create default dashboard.
     * @param tenant - the tenant
     */
    public void create(Tenant tenant) {
        final StopWatch stopWatch = StopWatch.createStarted();
        final String tenantKey = tenant.getTenantKey();
        String oldTenantKey = TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder);
        try {
            log.info("START - SETUP:CreateTenant:dashboard tenantKey: {}", tenantKey);

            TenantContextUtils.setTenant(tenantContextHolder, tenant.getTenantKey());

            Dashboard dashboard = new Dashboard();
            dashboard.setName(tenantKey.toLowerCase());
            dashboard.setOwner(tenantKey.toLowerCase());
            dashboard.setIsPublic(false);
            dashboard.setTypeKey(DEFAULT_TYPE_KEY);
            dashboardService.save(dashboard);

            log.info("STOP  - SETUP:CreateTenant:dashboard tenantKey: {}, result: OK, time = {} ms", tenantKey,
                stopWatch.getTime());
        } catch (Exception e) {
            log.info("STOP  - SETUP:CreateTenant:dashboard tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenantKey, e.getMessage(), stopWatch.getTime());
            throw e;
        } finally {
            tenantContextHolder.getPrivilegedContext().destroyCurrentContext();
            TenantContextUtils.setTenant(tenantContextHolder, oldTenantKey);
        }
    }
}
