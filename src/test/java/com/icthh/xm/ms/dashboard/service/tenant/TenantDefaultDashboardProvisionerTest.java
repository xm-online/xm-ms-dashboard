package com.icthh.xm.ms.dashboard.service.tenant;

import static org.mockito.ArgumentMatchers.any;

import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.internal.DefaultTenantContextHolder;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.service.DashboardService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class TenantDefaultDashboardProvisionerTest {
    @Mock
    private DashboardService profileService;
    @Spy
    private TenantContextHolder tenantContextHolder = new DefaultTenantContextHolder();

    private TenantDefaultDashboardProvisioner provisioner;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        provisioner = new TenantDefaultDashboardProvisioner(profileService, tenantContextHolder);
    }

    @Test
    public void createTenant() {

        provisioner.createTenant(new Tenant().tenantKey("NEWTENANT"));

        InOrder inOrder = Mockito.inOrder(profileService, tenantContextHolder);

        inOrder.verify(tenantContextHolder).getPrivilegedContext();
        inOrder.verify(profileService).save(any(Dashboard.class));
        inOrder.verifyNoMoreInteractions();

    }

    @Test
    public void manageTenant() {
        provisioner.manageTenant("NEWTENANT", "ACTIVE");
        Mockito.verifyZeroInteractions(tenantContextHolder);
        Mockito.verifyZeroInteractions(profileService);
    }

    @Test
    public void deleteTenant() {
        provisioner.deleteTenant("NEWTENANT");
        Mockito.verifyZeroInteractions(tenantContextHolder);
        Mockito.verifyZeroInteractions(profileService);
    }

}
