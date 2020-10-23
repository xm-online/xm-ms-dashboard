package com.icthh.xm.ms.dashboard.service.tenant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantProvisioner;
import com.icthh.xm.ms.dashboard.service.ImportDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.ImportDashboardDto;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class TenantDefaultDashboardProvisioner implements TenantProvisioner {

    public static final String DEFAULT_DASHBOARD_FILE = "config/dashboard/default-dashboards.json";

    private final ImportDashboardService importDashboardService;
    private final TenantContextHolder tenantContextHolder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Create default dashboard.
     *
     * @param tenant - the tenant
     */
    @Override
    public void createTenant(final Tenant tenant) {
        ImportDashboardDto dashboards = readDefaultDashboards();
        tenantContextHolder.getPrivilegedContext()
                           .execute(TenantContextUtils.buildTenant(tenant.getTenantKey()),
                                    () -> importDashboardService.importDashboards(dashboards));
    }

    @SneakyThrows
    private ImportDashboardDto readDefaultDashboards() {
        return objectMapper.readValue(new ClassPathResource(DEFAULT_DASHBOARD_FILE)
                                          .getInputStream(), ImportDashboardDto.class);
    }

    @Override
    public void manageTenant(final String tenantKey, final String state) {
        log.info("Nothing to do with default Dashboard manage tenant: {}, state = {}", tenantKey, state);
    }

    @Override
    public void deleteTenant(final String tenantKey) {
        log.info("Nothing to do with default Dashboard during delete tenant: {}", tenantKey);
    }
}
