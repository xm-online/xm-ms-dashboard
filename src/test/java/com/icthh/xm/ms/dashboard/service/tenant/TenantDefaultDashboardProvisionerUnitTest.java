package com.icthh.xm.ms.dashboard.service.tenant;

import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.internal.DefaultTenantContextHolder;
import com.icthh.xm.ms.dashboard.AbstractUnitTest;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.config.tenant.TenantManagerConfiguration;
import com.icthh.xm.ms.dashboard.service.DashboardSpecService;
import com.icthh.xm.ms.dashboard.service.ImportDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.ImportDashboardDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class TenantDefaultDashboardProvisionerUnitTest extends AbstractUnitTest {
    @Mock
    private ImportDashboardService importDashboardService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private DashboardSpecService dashboardSpecService;

    @Spy
    private final TenantContextHolder tenantContextHolder = new DefaultTenantContextHolder();

    private TenantDefaultDashboardProvisioner provisioner;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        provisioner = new TenantDefaultDashboardProvisioner(importDashboardService,
                                                            tenantContextHolder,
                                                            dashboardSpecService,
                                                            applicationProperties);
    }

    @Test
    public void createTenant() {

        AtomicReference<String> tenant = new AtomicReference<>();
        when(dashboardSpecService.getDashboardSpec()).thenAnswer(invocation -> {
            tenant.set(tenantContextHolder.getTenantKey());
            return Optional.empty();
        });

        when(applicationProperties.getSpecificationPathPattern())
                   .thenReturn("/config/tenants/{tenantName}/dashboard/dashboardspec.yml");
        provisioner.createTenant(new Tenant().tenantKey("NEWTENANT"));

        assertEquals("NEWTENANT", tenant.get());

        InOrder inOrder = Mockito.inOrder(importDashboardService, tenantContextHolder, dashboardSpecService);

        inOrder.verify(tenantContextHolder).getPrivilegedContext();
        inOrder.verify(dashboardSpecService).onInit(eq("/config/tenants/NEWTENANT/dashboard/dashboardspec.yml"),
            eq(TenantManagerConfiguration.readSpecResource()));
        inOrder.verify(importDashboardService).importDashboards(any(ImportDashboardDto.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void manageTenant() {
        provisioner.manageTenant("NEWTENANT", "ACTIVE");
        Mockito.verifyNoInteractions(tenantContextHolder);
        Mockito.verifyNoInteractions(importDashboardService);
    }

    @Test
    public void deleteTenant() {
        provisioner.deleteTenant("NEWTENANT");
        Mockito.verifyNoInteractions(tenantContextHolder);
        Mockito.verifyNoInteractions(importDashboardService);
    }

}
