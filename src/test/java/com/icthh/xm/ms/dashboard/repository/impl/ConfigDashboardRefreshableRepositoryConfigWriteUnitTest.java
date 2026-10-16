package com.icthh.xm.ms.dashboard.repository.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.icthh.xm.commons.config.client.repository.CommonConfigRepository;
import com.icthh.xm.commons.config.client.repository.TenantConfigRepository;
import com.icthh.xm.commons.config.domain.Configuration;
import com.icthh.xm.commons.tenant.TenantContext;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantKey;
import com.icthh.xm.ms.dashboard.AbstractUnitTest;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.service.DashboardSpecService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Dashboard files go to xm-ms-config through the HTTP API by default and through the config Kafka queue
 * when {@code application.storage.ms-config.update-via-kafka} is on.
 */
public class ConfigDashboardRefreshableRepositoryConfigWriteUnitTest extends AbstractUnitTest {

    private static final String API_PATH = "/api/config/tenants/{tenantName}/dashboard/dashboards/DEMO-5.yml";
    private static final String CONFIG_PATH = "/config/tenants/XM/dashboard/dashboards/DEMO-5.yml";

    private final ApplicationProperties.Storage.MsConfigStorageProperties msConfig =
        new ApplicationProperties.Storage.MsConfigStorageProperties();
    private final TenantConfigRepository tenantConfigRepository = mock(TenantConfigRepository.class);
    private final CommonConfigRepository commonConfigRepository = mock(CommonConfigRepository.class);
    private ConfigDashboardRefreshableRepository repository;

    @BeforeEach
    public void setUp() {
        msConfig.setTenantDashboardsFolderPath("/config/tenants/{tenantName}/dashboard/dashboards/");
        ApplicationProperties applicationProperties = new ApplicationProperties();
        applicationProperties.getStorage().setMsConfig(msConfig);

        TenantContext tenantContext = mock(TenantContext.class);
        when(tenantContext.getTenantKey()).thenReturn(Optional.of(TenantKey.valueOf("XM")));
        TenantContextHolder tenantContextHolder = mock(TenantContextHolder.class);
        when(tenantContextHolder.getContext()).thenReturn(tenantContext);

        repository = new ConfigDashboardRefreshableRepository(applicationProperties, tenantContextHolder,
            tenantConfigRepository, commonConfigRepository, mock(DashboardSpecService.class));
    }

    @Test
    public void saveAndDelete_goThroughHttpApiByDefault() {
        repository.saveDashboard(dashboard());
        repository.deleteDashboard(dashboard());

        verify(tenantConfigRepository).updateConfigFullPath(eq("XM"), eq(API_PATH), anyString(), eq(""));
        verify(tenantConfigRepository).deleteConfigFullPath("XM", API_PATH);
        verify(commonConfigRepository, never()).updateConfigFullPath(any(), any());
    }

    @Test
    public void saveAndDelete_goThroughConfigQueueWhenEnabled() {
        msConfig.setUpdateViaKafka(true);

        Dashboard dashboard = dashboard();
        repository.saveDashboard(dashboard);
        repository.deleteDashboard(dashboard);

        verify(commonConfigRepository).updateConfigFullPath(
            argThat((Configuration c) -> CONFIG_PATH.equals(c.getPath()) && c.getContent().contains("DEMO")),
            isNull());
        verify(commonConfigRepository).updateConfigFullPath(
            argThat((Configuration c) -> CONFIG_PATH.equals(c.getPath()) && c.getContent().isEmpty()),
            isNull());
        verify(tenantConfigRepository, never()).updateConfigFullPath(anyString(), anyString(), anyString(), anyString());
        verify(tenantConfigRepository, never()).deleteConfigFullPath(anyString(), anyString());
    }

    private static Dashboard dashboard() {
        Dashboard dashboard = new Dashboard().typeKey("DEMO").name("Demo").owner("admin");
        dashboard.setId(5L);
        return dashboard;
    }
}
