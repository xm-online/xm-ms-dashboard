package com.icthh.xm.ms.dashboard.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icthh.xm.commons.config.client.api.RefreshableConfiguration;
import com.icthh.xm.commons.config.client.repository.TenantConfigRepository;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties.Storage.MsConfigStorageProperties;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.DashboardSpec;
import com.icthh.xm.ms.dashboard.service.DashboardSpecService;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import com.icthh.xm.ms.dashboard.service.dto.WidgetDto;
import com.icthh.xm.ms.dashboard.util.ServiceUtil;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigDashboardRefreshableRepository implements RefreshableConfiguration {

    private static final String TENANT_NAME = "tenantName";

    private final Map<String, Map<String, DashboardConfig>> dashboardsByTenantByFile = new ConcurrentHashMap<>();
    private volatile Map<String, Map<String, String>> dashboardPathByTenantByTypeKey = new ConcurrentHashMap<>();

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final ApplicationProperties applicationProperties;
    private final TenantContextHolder tenantContextHolder;
    private final TenantConfigRepository tenantConfigRepository;
    private final DashboardSpecService dashboardSpecService;

    @Override
    public void onRefresh(String updatedKey, String config) {
        try {
            MsConfigStorageProperties msConfigProperties = applicationProperties.getStorage().getMsConfig();
            String pathPattern = msConfigProperties.getTenantDashboardsFolderPathPattern();
            String tenant = matcher.extractUriTemplateVariables(pathPattern, updatedKey).get(TENANT_NAME);
            updateByFileState(updatedKey, config, tenant);
            log.info("Specification was for tenant {} updated from file {}", tenant, updatedKey);
        } catch (Exception e) {
            log.error("Error read xm specification from path " + updatedKey, e);
        }
    }

    @Override
    public void refreshFinished(Collection<String> paths) {
        MsConfigStorageProperties msConfigProperties = applicationProperties.getStorage().getMsConfig();
        String pathPattern = msConfigProperties.getTenantDashboardsFolderPathPattern();

        paths.forEach(path -> processTenantPath(path, pathPattern));

        Map<String, Map<String, String>> dashboardPathByTenantByTypeKey = new ConcurrentHashMap<>();
        dashboardsByTenantByFile.forEach((tenant, dashboardsByFile) -> {
            Map<String, String> dashboardByTypeKey = typeKeyToPath(dashboardsByFile);
            dashboardPathByTenantByTypeKey.put(tenant, dashboardByTypeKey);
        });
        this.dashboardPathByTenantByTypeKey = dashboardPathByTenantByTypeKey;
    }

    private Map<String, String> typeKeyToPath(Map<String, DashboardConfig> dashboardsByFile) {
        Map<String, String> dashboardByTypeKey = new LinkedHashMap<>();
        dashboardsByFile.forEach((path, dashboardConfig) -> {
            DashboardDto dashboard = dashboardConfig.getDashboardDto();
            if (dashboard != null) {
                dashboardByTypeKey.put(dashboard.getTypeKey(), path);
            }
        });
        return dashboardByTypeKey;
    }

    private void processTenantPath(String path, String pathPattern) {
        String tenant = matcher.extractUriTemplateVariables(pathPattern, path).get(TENANT_NAME);
        if (StringUtils.isNotBlank(tenant)) {
            dashboardSpecService.getDashboardSpec(tenant)
                    .filter(this::isOverrideIdEnabled)
                    .ifPresent(dashboardSpec -> processOverrideDashboardId(tenant));
        }
    }

    private boolean isOverrideIdEnabled(DashboardSpec dashboardSpec) {
        boolean isMsConfigType = dashboardSpec.getDashboardStoreType().equals(DashboardSpec.DashboardStoreType.MSCONFG);
        boolean enableOverrideId = Optional.ofNullable(dashboardSpec.getOverrideId()).orElse(true);

        return isMsConfigType && enableOverrideId;
    }

    private void processOverrideDashboardId(String tenant) {
        Set<Long> usedIds = new HashSet<>();
        List<DashboardDto> dashboards = getDashboards(tenant).stream()
                .sorted(Comparator.comparing(DashboardDto::getTypeKey))
                .toList();

        Set<Long> allDashboardIds = dashboards.stream().map(DashboardDto::getId).collect(Collectors.toSet());

        for (DashboardDto dashboard : dashboards) {
            if (usedIds.contains(dashboard.getId())) {
                updatedDashboardId(dashboard, allDashboardIds, usedIds);
            } else {
                usedIds.add(dashboard.getId());
                updateWidgetsDashboardId(dashboard);
            }
        }
    }

    @SneakyThrows
    private void updateByFileState(String updatedKey, String config, String tenant) {
        Map<String, DashboardConfig> dashboards = dashboardsByTenantByFile
            .computeIfAbsent(tenant, key -> new LinkedHashMap<>());

        if (StringUtils.isBlank(config)) {
            dashboards.remove(updatedKey);
            return;
        }

        DashboardDto dashboard = mapper.readValue(config, DashboardDto.class);
        dashboards.put(updatedKey, new DashboardConfig(dashboard, DigestUtils.sha1Hex(config)));
    }

    @Override
    public boolean isListeningConfiguration(String updatedKey) {
        MsConfigStorageProperties msConfigProperties = applicationProperties.getStorage().getMsConfig();
        String tenantDashboardsFolderPathPattern = msConfigProperties.getTenantDashboardsFolderPathPattern();
        return matcher.match(tenantDashboardsFolderPathPattern, updatedKey);
    }

    @Override
    public void onInit(String configKey, String configValue) {
        onRefresh(configKey, configValue);
    }

    public List<DashboardDto> getDashboards() {
        String tenant = getTenantKeyValue();
        return dashboardsByTenantByFile.getOrDefault(tenant, Map.of()).values()
            .stream()
            .map(DashboardConfig::getDashboardDto)
            .collect(toList());

    }

    public List<DashboardDto> getDashboards(String tenant) {
        return dashboardsByTenantByFile.getOrDefault(tenant, Map.of()).values()
            .stream()
            .map(DashboardConfig::getDashboardDto)
            .collect(toList());

    }

    @SneakyThrows
    public <S extends Dashboard> S saveDashboard(S dashboard) {
        String tenant = getTenantKeyValue();
        String fullPath = getFullPath(dashboard);
        String dashboardConfigPath = getDashboardConfigApiPath(dashboard.getTypeKey(), tenant, fullPath);

        DashboardConfig dashboardConfig = dashboardsByTenantByFile.getOrDefault(tenant, Map.of()).get(dashboardConfigPath);

        String oldConfigHash = "";
        if (dashboardConfig != null) {
            oldConfigHash = dashboardConfig.getOldHash();
        }
        tenantConfigRepository.updateConfigFullPath(tenant,
                                                    dashboardConfigPath,
                                                    mapper.writeValueAsString(dashboard),
                                                    oldConfigHash);
        return dashboard;
    }

    public <S extends Dashboard> String getDashboardConfigApiPath(String dashBoardTypeKey, String tenant, String fullPath) {
        String path = dashboardPathByTenantByTypeKey.getOrDefault(tenant, Map.of()).get(dashBoardTypeKey);
        if (path != null) {
            return mapApiFullPath(path);
        }
        return fullPath;
    }

    private String mapApiFullPath(String fullPath) {
        return "/api" + fullPath;
    }

    private String getTenantKeyValue() {
        return TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder).toUpperCase();
    }

    public void deleteDashboard(Dashboard dashboard) {
        String tenant = getTenantKeyValue();
        String fullPath = getFullPath(dashboard);
        String dashboardConfigApiPath = getDashboardConfigApiPath(dashboard.getTypeKey(), tenant, fullPath);
        dashboardPathByTenantByTypeKey.getOrDefault(tenant, Map.of()).remove(dashboard.getTypeKey());
        tenantConfigRepository.deleteConfigFullPath(tenant, dashboardConfigApiPath);
    }

    public  <S extends Dashboard> String getFullPath(S dashboard) {
        MsConfigStorageProperties msConfigProperties = applicationProperties.getStorage().getMsConfig();
        String tenantDashboardsFolderPath = msConfigProperties.getTenantDashboardsFolderPath();
        return "/api"
            + tenantDashboardsFolderPath
            + dashboard.getTypeKey()
            + "-"
            + dashboard.getId()
            + ".yml";
    }

    private void updatedDashboardId(DashboardDto dashboard, Set<Long> allIds, Set<Long> usedIds) {
        Long overrideId = getNotExistValueCompareSet(allIds, usedIds, dashboard.getId());
        dashboard.setId(overrideId);
        usedIds.add(overrideId);

        updateWidgetsDashboardId(dashboard);
    }

    private void updateWidgetsDashboardId(DashboardDto dashboard) {
        Set<WidgetDto> widgets = dashboard.getWidgets();

        if (!CollectionUtils.isEmpty(widgets)) {
            widgets.stream()
                    .filter(Objects::nonNull)
                    .filter(widget -> !dashboard.getId().equals(widget.getDashboard()))
                    .forEach(widget -> widget.setDashboard(dashboard.getId()));
        }
    }

    private Long getNotExistValueCompareSet(Set<Long> allValues, Set<Long> usedValues, Long value) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(allValues)) {
            return value;
        }

        Long candidate = value;

        while (allValues.contains(candidate) || usedValues.contains(candidate)) {
            candidate++;
        }

        return candidate;
    }

    @Data
    @AllArgsConstructor
    private static class DashboardConfig {
        private DashboardDto dashboardDto;
        private String oldHash;
    }

}
