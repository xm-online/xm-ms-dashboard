package com.icthh.xm.ms.dashboard.service.tenant;

import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantProvisioner;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.service.DashboardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class TenantDefaultDashboardProvisioner implements TenantProvisioner {

    private static final String DEFAULT_TYPE_KEY = "DASHBOARD";

    private final DashboardService dashboardService;
    private final TenantContextHolder tenantContextHolder;

    /**
     * Create default dashboard.
     *
     * @param tenant - the tenant
     */
    @Override
    public void createTenant(final Tenant tenant) {
        Dashboard dashboard = buildDashboard(tenant);
        tenantContextHolder.getPrivilegedContext()
                           .execute(TenantContextUtils.buildTenant(tenant.getTenantKey()),
                                    () -> dashboardService.save(dashboard));
    }

    private Dashboard buildDashboard(final Tenant tenant) {
        String tenantKey = tenant.getTenantKey();
        Dashboard dashboard = new Dashboard();
        dashboard.setName(tenantKey.toLowerCase());
        dashboard.setOwner(tenantKey.toLowerCase());
        dashboard.setIsPublic(false);
        dashboard.setTypeKey(DEFAULT_TYPE_KEY);
        return dashboard;
    }

    @Override
    public void manageTenant(final String tenantKey, final String state) {
        log.info("Nothing to do with default default Dashboard manage tenant: {}, state = {}", tenantKey, state);
    }

    @Override
    public void deleteTenant(final String tenantKey) {
        log.info("Nothing to do with default Dashboard during delete tenant: {}", tenantKey);
    }
}
