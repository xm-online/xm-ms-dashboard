package com.icthh.xm.ms.dashboard.web.rest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.icthh.xm.commons.config.client.repository.TenantConfigRepository;
import com.icthh.xm.commons.tenant.TenantContext;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantKey;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.repository.impl.ConfigDashboardRefreshableRepository;
import com.icthh.xm.ms.dashboard.repository.impl.IdRefreshableRepository;
import com.icthh.xm.ms.dashboard.service.DashboardSpecService;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;

/**
 * Test class for the DashboardResource REST controller for Config dashboard store type.
 *
 * @see DashboardResource
 */

public class DashboardResourceConfigIntTest extends DashboardResourceIntTest {

    public static final String CONFIG_SPECS_PATH = "/config/specs/dashboardspec.yml";
    public static final String CONFIG_SPECS_UPDATED_KEY = "/config/tenants/XM/dashboard/dashboardspec.yml";

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

    @Before
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

    public static void mockTenantConfigRepository(ConfigDashboardRefreshableRepository refreshableRepository,
                                                   IdRefreshableRepository idRefreshableRepository,
                                                   TenantConfigRepository tenantConfigRepository) {
        Mockito.doAnswer(invocation -> {
            String updateKey = invocation.getArgument(1);
            String content = invocation.getArgument(2);
            updateKey = prepareUpdateKey(updateKey);
            callOnRefresh(refreshableRepository, idRefreshableRepository, updateKey, content);
            return null;
        }).when(tenantConfigRepository).updateConfigFullPath(anyString(), anyString(), anyString(), anyString());

        Mockito.doAnswer(invocation -> {
            String updateKey = invocation.getArgument(1);
            updateKey = prepareUpdateKey(updateKey);
            callOnRefresh(refreshableRepository, idRefreshableRepository, updateKey, null);
            return null;
        }).when(tenantConfigRepository).deleteConfigFullPath(any(), any());
    }

    @SneakyThrows
    public static String loadFile(String path) {
        InputStream cfgInputStream = new ClassPathResource(path).getInputStream();
        return IOUtils.toString(cfgInputStream, UTF_8);
    }

    public static void cleanDashboardRepository(DashboardRepository dashboardRepository) {
        List<Dashboard> dashboards = dashboardRepository.findAll();
        dashboardRepository.deleteAll(dashboards);
    }

    private static void callOnRefresh(ConfigDashboardRefreshableRepository refreshableRepository,
                                      IdRefreshableRepository idRefreshableRepository,
                                      String updateKey,
                                      String content) {
        if (refreshableRepository.isListeningConfiguration(updateKey)) {
            refreshableRepository.onRefresh(updateKey, content);
        }

        if (idRefreshableRepository.isListeningConfiguration(updateKey)) {
            idRefreshableRepository.onRefresh(updateKey, content);
        }
    }

    private static String prepareUpdateKey(String updateKey) {
        return updateKey.replace("/api", "").replace("{tenantName}", "XM");
    }

}
