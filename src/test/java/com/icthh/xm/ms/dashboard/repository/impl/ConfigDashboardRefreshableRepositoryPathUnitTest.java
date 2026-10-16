package com.icthh.xm.ms.dashboard.repository.impl;

import static com.icthh.xm.ms.dashboard.repository.impl.ConfigDashboardRefreshableRepository.ERROR_INVALID_TYPE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.icthh.xm.commons.exceptions.BusinessException;
import com.icthh.xm.ms.dashboard.AbstractUnitTest;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The dashboard typeKey becomes part of the config file path in the MSCONFG mode; it must not lead outside
 * the tenant dashboards folder.
 */
@ExtendWith(MockitoExtension.class)
public class ConfigDashboardRefreshableRepositoryPathUnitTest extends AbstractUnitTest {

    private static final String DASHBOARDS_FOLDER = "/config/tenants/{tenantName}/dashboard/dashboards/";

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private ApplicationProperties.Storage storage;

    @Mock
    private ApplicationProperties.Storage.MsConfigStorageProperties msConfig;

    @InjectMocks
    private ConfigDashboardRefreshableRepository repository;

    @BeforeEach
    public void setUp() {
        when(applicationProperties.getStorage()).thenReturn(storage);
        when(storage.getMsConfig()).thenReturn(msConfig);
        when(msConfig.getTenantDashboardsFolderPath()).thenReturn(DASHBOARDS_FOLDER);
    }

    @Test
    public void getFullPath_keepsPlainTypeKey() {
        assertThat(repository.getFullPath(dashboard("DEMO")))
            .isEqualTo("/api" + DASHBOARDS_FOLDER + "DEMO-5.yml");
    }

    @Test
    public void getFullPath_allowsSubFolderInTypeKey() {
        assertThat(repository.getFullPath(dashboard("ext-ee-core/ee-entity-function")))
            .isEqualTo("/api" + DASHBOARDS_FOLDER + "ext-ee-core/ee-entity-function-5.yml");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "../../entity/xmentityspec/evil",
        "../../../../OTHER/dashboard/dashboards/evil",
        "a/../../evil",
        "a/../b",
        "./a",
        "a//b",
        "/abs"
    })
    public void getFullPath_rejectsTypeKeyWithDotSegments(String typeKey) {
        assertThatThrownBy(() -> repository.getFullPath(dashboard(typeKey)))
            .isInstanceOf(BusinessException.class)
            .extracting(e -> ((BusinessException) e).getCode())
            .isEqualTo(ERROR_INVALID_TYPE_KEY);
    }

    private static Dashboard dashboard(String typeKey) {
        Dashboard dashboard = new Dashboard().typeKey(typeKey);
        dashboard.setId(5L);
        return dashboard;
    }
}
