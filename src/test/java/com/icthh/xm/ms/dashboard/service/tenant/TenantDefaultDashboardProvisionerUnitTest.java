package com.icthh.xm.ms.dashboard.service.tenant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.internal.DefaultTenantContextHolder;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.config.tenant.TenantManagerConfiguration;
import com.icthh.xm.ms.dashboard.service.DashboardSpecService;
import com.icthh.xm.ms.dashboard.service.ImportDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.ImportDashboardDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class TenantDefaultDashboardProvisionerUnitTest {
    @Mock
    private ImportDashboardService importDashboardService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private DashboardSpecService dashboardSpecService;

    @Spy
    private final TenantContextHolder tenantContextHolder = new DefaultTenantContextHolder();

    private TenantDefaultDashboardProvisioner provisioner;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        provisioner = new TenantDefaultDashboardProvisioner(importDashboardService,
                                                            tenantContextHolder,
                                                            dashboardSpecService,
                                                            applicationProperties);
    }

    @Test
    public void createTenant() {

        when(applicationProperties.getSpecificationPathPattern())
                   .thenReturn("/config/tenants/{tenantName}/dashboard/dashboardspec.yml");
        provisioner.createTenant(new Tenant().tenantKey("NEWTENANT"));

        InOrder inOrder = Mockito.inOrder(importDashboardService, tenantContextHolder, dashboardSpecService);

        inOrder.verify(dashboardSpecService).onInit(eq("/config/tenants/NEWTENANT/dashboard/dashboardspec.yml"),
                                                    eq(TenantManagerConfiguration.readSpecResource()));
        inOrder.verify(tenantContextHolder).getPrivilegedContext();
        inOrder.verify(importDashboardService).importDashboards(any(ImportDashboardDto.class));
        inOrder.verifyNoMoreInteractions();

    }

    @Test
    public void manageTenant() {
        provisioner.manageTenant("NEWTENANT", "ACTIVE");
        Mockito.verifyZeroInteractions(tenantContextHolder);
        Mockito.verifyZeroInteractions(importDashboardService);
    }

    @Test
    public void deleteTenant() {
        provisioner.deleteTenant("NEWTENANT");
        Mockito.verifyZeroInteractions(tenantContextHolder);
        Mockito.verifyZeroInteractions(importDashboardService);
    }

}
