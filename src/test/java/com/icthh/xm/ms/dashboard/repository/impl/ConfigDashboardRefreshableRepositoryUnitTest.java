package com.icthh.xm.ms.dashboard.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icthh.xm.commons.config.client.repository.TenantConfigRepository;
import com.icthh.xm.commons.tenant.TenantContext;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantKey;
import com.icthh.xm.ms.dashboard.AbstractUnitTest;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.DashboardSpec;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.DashboardSpecService;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import com.icthh.xm.ms.dashboard.service.dto.WidgetDto;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConfigDashboardRefreshableRepositoryUnitTest extends AbstractUnitTest {

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private ApplicationProperties.Storage storage;

    @Mock
    private ApplicationProperties.Storage.MsConfigStorageProperties msConfig;

    @Mock
    private TenantContextHolder tenantContextHolder;

    @Mock
    private TenantConfigRepository tenantConfigRepository;

    @Mock
    private IdRefreshableRepository idRefreshableRepository;

    @Mock
    private DashboardSpecService dashboardSpecService;

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private DashboardMapper dashboardMapper;

    @InjectMocks
    private ConfigDashboardRefreshableRepository repository;

    @Captor
    private ArgumentCaptor<String> configContentCaptor;

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @BeforeEach
    public void setUp() {
        when(applicationProperties.getStorage()).thenReturn(storage);
        when(storage.getMsConfig()).thenReturn(msConfig);
        when(msConfig.getTenantDashboardsFolderPathPattern())
                .thenReturn("/config/tenants/{tenantName}/dashboard/*.yml");
        when(msConfig.getTenantDashboardsFolderPath()).thenReturn("/config/tenants/{tenantName}/dashboard");


        TenantContext tenantContext = mock(TenantContext.class);
        when(tenantContext.getTenantKey()).thenReturn(Optional.of(TenantKey.valueOf("XM")));
        when(tenantContextHolder.getContext()).thenReturn(tenantContext);

        when(dashboardMapper.toFullEntity(any(DashboardDto.class))).thenAnswer(invocation -> {
            DashboardDto dto = invocation.getArgument(0);
            Dashboard dashboard = new Dashboard();
            dashboard.setId(dto.getId());
            dashboard.setName(dto.getName());
            dashboard.setTypeKey(dto.getTypeKey());
            dashboard.setOwner(dto.getOwner());
            dashboard.setConfig(dto.getConfig());
            dashboard.setLayout(dto.getLayout());
            dashboard.setIsPublic(dto.getIsPublic());

            if (dto.getWidgets() != null) {
                Set<Widget> widgets = new HashSet<>();
                for (WidgetDto widgetDto : dto.getWidgets()) {
                    Widget widget = new Widget();
                    widget.setId(widgetDto.getId());
                    widget.setName(widgetDto.getName());
                    widget.setConfig(widgetDto.getConfig());
                    widget.setDashboard(dashboard);
                    widgets.add(widget);
                }
                dashboard.setWidgets(widgets);
            }
            
            return dashboard;
        });
    }

    @Test
    public void updateByFileState_shouldAssignNewId_whenDashboardIdIsAlreadyUsed() throws Exception {
        String configPath1 = "/config/tenants/XM/dashboard/dashboard1.yml";
        String configPath2 = "/config/tenants/XM/dashboard/dashboard2.yml";

        String dashboard1Yaml = "id: 1\n" +
                "name: \"Dashboard 1\"\n" +
                "typeKey: \"TEST\"\n" +
                "owner: \"user1\"\n" +
                "widgets:\n" +
                "  - id: 101\n" +
                "    name: \"Widget 1\"\n" +
                "    dashboard: 1\n" +
                "  - id: 102\n" +
                "    name: \"Widget 2\"\n" +
                "    dashboard: 1\n";

        String dashboard2Yaml = "id: 1\n" +
                "name: \"Dashboard 2\"\n" +
                "typeKey: \"TEST2\"\n" +
                "owner: \"user2\"\n" +
                "widgets:\n" +
                "  - id: 101\n" +
                "    name: \"Widget 1 Copy\"\n" +
                "    dashboard: 1\n" +
                "  - id: 103\n" +
                "    name: \"Widget 3\"\n" +
                "    dashboard: 1\n";

        DashboardSpec spec = new DashboardSpec();
        spec.setDashboardStoreType(DashboardSpec.DashboardStoreType.MSCONFG);
        spec.setOverrideId(true);
        when(dashboardSpecService.getDashboardSpec()).thenReturn(Optional.of(spec));

        when(idRefreshableRepository.getNextId())
                .thenReturn(2L)
                .thenReturn(201L)
                .thenReturn(202L);

        repository.onRefresh(configPath1, dashboard1Yaml);

        reset(tenantConfigRepository);

        repository.onRefresh(configPath2, dashboard2Yaml);

        verify(tenantConfigRepository).updateConfigFullPath(
                eq("XM"),
                eq("/api" + configPath2),
                configContentCaptor.capture(),
                anyString()
        );

        String updatedConfig = configContentCaptor.getValue();
        DashboardDto updatedDashboard = yamlMapper.readValue(updatedConfig, DashboardDto.class);

        assertThat(updatedDashboard.getId()).isEqualTo(2L);

        Set<WidgetDto> widgets = updatedDashboard.getWidgets();
        assertThat(widgets.size()).isEqualTo(2);

        Set<Long> widgetDashboardIds = updatedDashboard.getWidgets().stream()
                .map(WidgetDto::getDashboard)
                .collect(Collectors.toSet());

        assertThat(widgetDashboardIds)
                .hasSize(1)
                .contains(updatedDashboard.getId());

    }

    @Test
    public void updateByFileState_shouldNotUpdateId_whenIdIsNotUsed() throws Exception {
        String configPath = "/config/tenants/XM/dashboard/unique-dashboard.yml";
        String dashboardYaml = "id: 100\n" +
                "name: \"Unique Dashboard\"\n" +
                "typeKey: \"TEST\"\n" +
                "owner: \"user1\"\n" +
                "widgets:\n" +
                "  - id: 1011\n" +
                "    name: \"Widget 1\"\n" +
                "    dashboard: 1\n" +
                "  - id: 1012\n" +
                "    name: \"Widget 3\"\n" +
                "    dashboard: 100\n";

        DashboardSpec spec = new DashboardSpec();
        spec.setDashboardStoreType(DashboardSpec.DashboardStoreType.MSCONFG);
        spec.setOverrideId(true);
        when(dashboardSpecService.getDashboardSpec()).thenReturn(Optional.of(spec));

        repository.onRefresh(configPath, dashboardYaml);

        verify(tenantConfigRepository).updateConfigFullPath(
                eq("XM"),
                eq("/api" + configPath),
                configContentCaptor.capture(),
                anyString()
        );

        String updatedConfig = configContentCaptor.getValue();
        DashboardDto updatedDashboard = yamlMapper.readValue(updatedConfig, DashboardDto.class);
        assertThat(updatedDashboard.getId()).isEqualTo(100L);


        Set<WidgetDto> widgets = updatedDashboard.getWidgets();
        assertThat(widgets.size()).isEqualTo(2);

        Set<Long> widgetDashboardIds = updatedDashboard.getWidgets().stream()
                .map(WidgetDto::getDashboard)
                .collect(Collectors.toSet());

        assertThat(widgetDashboardIds)
                .hasSize(1)
                .contains(updatedDashboard.getId());

    }
}