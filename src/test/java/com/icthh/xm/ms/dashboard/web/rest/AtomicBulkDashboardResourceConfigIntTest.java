package com.icthh.xm.ms.dashboard.web.rest;

import static com.icthh.xm.ms.dashboard.web.rest.DashboardResourceConfigIntTest.CONFIG_SPECS_PATH;
import static com.icthh.xm.ms.dashboard.web.rest.DashboardResourceConfigIntTest.CONFIG_SPECS_UPDATED_KEY;
import static com.icthh.xm.ms.dashboard.web.rest.DashboardResourceConfigIntTest.cleanDashboardRepository;
import static com.icthh.xm.ms.dashboard.web.rest.DashboardResourceConfigIntTest.loadFile;
import static com.icthh.xm.ms.dashboard.web.rest.DashboardResourceConfigIntTest.mockTenantConfigRepository;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.icthh.xm.commons.config.client.repository.TenantConfigRepository;
import com.icthh.xm.commons.tenant.TenantContext;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantKey;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.repository.impl.ConfigDashboardRefreshableRepository;
import com.icthh.xm.ms.dashboard.repository.impl.IdRefreshableRepository;
import com.icthh.xm.ms.dashboard.service.DashboardSpecService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Test class for the BulkDashboardResource REST controller for Config dashboard store type.
 *
 * @see BulkDashboardResource
 */

public class AtomicBulkDashboardResourceConfigIntTest extends AtomicBulkDashboardResourceIntTest {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private DashboardSpecService dashboardSpecService;

    @Autowired
    private ConfigDashboardRefreshableRepository refreshableRepository;

    @Autowired
    private IdRefreshableRepository idRefreshableRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @MockBean
    private TenantConfigRepository tenantConfigRepository;

    private TenantContextHolder tenantContextHolder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        TenantContext tenantContext = mock(TenantContext.class);
        when(tenantContext.getTenantKey()).thenReturn(Optional.of(TenantKey.valueOf("XM")));
        tenantContextHolder = mock(TenantContextHolder.class);
        when(tenantContextHolder.getContext()).thenReturn(tenantContext);
        applicationProperties.getStorage().setStoreConfigurationEnabled(true);

        dashboardSpecService.onRefresh(CONFIG_SPECS_UPDATED_KEY, loadFile(CONFIG_SPECS_PATH));
        mockTenantConfigRepository(refreshableRepository, idRefreshableRepository, tenantConfigRepository);

        cleanDashboardRepository(dashboardRepository);
    }

}
