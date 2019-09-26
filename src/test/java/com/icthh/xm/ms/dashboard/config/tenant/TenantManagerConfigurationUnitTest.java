package com.icthh.xm.ms.dashboard.config.tenant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.migration.db.tenant.provisioner.TenantDatabaseProvisioner;
import com.icthh.xm.commons.tenantendpoint.TenantManager;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantAbilityCheckerProvisioner;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantListProvisioner;
import com.icthh.xm.ms.dashboard.service.tenant.TenantDefaultDashboardProvisioner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TenantManagerConfigurationUnitTest {

    private TenantManager tenantManager;

    @Spy
    private TenantManagerConfiguration configuration = new TenantManagerConfiguration();

    @Mock
    private TenantAbilityCheckerProvisioner abilityCheckerProvisioner;
    @Mock
    private TenantDatabaseProvisioner databaseProvisioner;
    @Mock
    private TenantListProvisioner tenantListProvisioner;
    @Mock
    private TenantDefaultDashboardProvisioner dashboardProvisioner;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        tenantManager = configuration.tenantManager(abilityCheckerProvisioner,
                                                    databaseProvisioner,
                                                    dashboardProvisioner,
                                                    tenantListProvisioner);
    }

    @Test
    public void testCreateTenantProvisioningOrder() {

        tenantManager.createTenant(new Tenant().tenantKey("newtenant"));

        InOrder inOrder = Mockito.inOrder(abilityCheckerProvisioner,
                                          tenantListProvisioner,
                                          databaseProvisioner,
                                          dashboardProvisioner);

        inOrder.verify(abilityCheckerProvisioner).createTenant(any(Tenant.class));
        inOrder.verify(tenantListProvisioner).createTenant(any(Tenant.class));
        inOrder.verify(databaseProvisioner).createTenant(any(Tenant.class));
        inOrder.verify(dashboardProvisioner).createTenant(any(Tenant.class));

        verifyNoMoreInteractions(abilityCheckerProvisioner,
                                 tenantListProvisioner,
                                 databaseProvisioner,
                                 dashboardProvisioner);
    }

}
