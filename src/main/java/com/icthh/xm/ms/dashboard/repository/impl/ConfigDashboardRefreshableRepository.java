package com.icthh.xm.ms.dashboard.repository.impl;

import static java.util.stream.Collectors.toList;

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
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.DashboardSpecService;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import com.icthh.xm.ms.dashboard.service.dto.WidgetDto;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigDashboardRefreshableRepository implements RefreshableConfiguration {

    private static final String TENANT_NAME = "tenantName";

    private final Map<String, Map<String, DashboardConfig>> dashboardsByTenantByFile = new ConcurrentHashMap<>();

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final ApplicationProperties applicationProperties;
    private final TenantContextHolder tenantContextHolder;
    private final TenantConfigRepository tenantConfigRepository;
    private final IdRefreshableRepository idRefreshableRepository;
    private final DashboardSpecService dashboardSpecService;
    private final DashboardMapper dashboardMapper;

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

    @SneakyThrows
    private void updateByFileState(String updatedKey, String config, String tenant) {
        Map<String, DashboardConfig> dashboards = dashboardsByTenantByFile
            .computeIfAbsent(tenant, key -> new LinkedHashMap<>());

        if (StringUtils.isBlank(config)) {
            dashboards.remove(updatedKey);
            return;
        }

        DashboardDto dashboard = mapper.readValue(config, DashboardDto.class);
        String configHash = DigestUtils.sha1Hex(config);
        
        dashboardSpecService.getDashboardSpec().ifPresentOrElse(dashboardSpec -> {
            checkAndOverrideDashboardIds(updatedKey, dashboardSpec, dashboard, dashboards, configHash);
        }, () -> updateDashboard(dashboards, updatedKey, dashboard, configHash));
    }

    private void checkAndOverrideDashboardIds(String updatedKey, DashboardSpec dashboardSpec, DashboardDto dashboard,
                                              Map<String, DashboardConfig> dashboards, String configHash) {
        boolean isMsConfigType = dashboardSpec.getDashboardStoreType().equals(DashboardSpec.DashboardStoreType.MSCONFG);
        Boolean isOverrideId = Optional.ofNullable(dashboardSpec.getOverrideId()).orElse(true);
        if (isMsConfigType && isOverrideId) {
            boolean updatedDashboardId = updatedDashboardIdIfUsed(dashboard);
            updateDashboard(dashboards, updatedKey, dashboard, configHash);
            if (updatedDashboardId) {
                Dashboard entity = dashboardMapper.toFullEntity(dashboard);
                saveDashboard(entity);
            }
        } else {
            updateDashboard(dashboards, updatedKey, dashboard, configHash);
        }
    }

    private void updateDashboard(Map<String, DashboardConfig> dashboards, String key, DashboardDto dashboard, String configHash) {
        dashboards.put(key, new DashboardConfig(dashboard, configHash));
    }

    private boolean updatedDashboardIdIfUsed(DashboardDto dashboard) {
        Set<WidgetDto> widgets = dashboard.getWidgets();
        List<DashboardDto> dashboards = getDashboards();
        boolean isAlreadyUsedDashboardId = dashboards.stream().anyMatch(it -> it.getId().equals(dashboard.getId()));

        boolean wasUpdated = false;

        if (isAlreadyUsedDashboardId) {
            final Long nextId = idRefreshableRepository.getNextId();
            dashboard.setId(nextId);
            wasUpdated = true;
        }

        if (widgets != null) {
            boolean widgetsUpdated = widgets.stream()
                    .filter(Objects::nonNull)
                    .filter(widget -> !dashboard.getId().equals(widget.getDashboard()))
                    .peek(widget -> widget.setDashboard(dashboard.getId()))
                    .findAny()
                    .isPresent();

            wasUpdated = wasUpdated || widgetsUpdated;
        }

        return wasUpdated;
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

    @SneakyThrows
    public <S extends Dashboard> S saveDashboard(S dashboard) {
        String tenant = getTenantKeyValue();
        String fullPath = getFullPath(dashboard);
        String dashboardConfigPath = getDashboardConfigApiPath(dashboard, tenant, fullPath);

        DashboardConfig dashboardConfig = dashboardsByTenantByFile.getOrDefault(tenant, Map.of()).get(dashboardConfigPath);

        String oldConfigHash = "";
        if (dashboardConfig != null) {
            oldConfigHash = dashboardConfig.getOldHash();
        }
        tenantConfigRepository.updateConfigFullPath(tenant,
                                                    getApiFullPath(dashboardConfigPath),
                                                    mapper.writeValueAsString(dashboard),
                                                    oldConfigHash);
        return dashboard;
    }

    private <S extends Dashboard> String getDashboardConfigApiPath(S dashboard, String tenant, String fullPath) {
        return dashboardsByTenantByFile.getOrDefault(tenant, Map.of())
                .entrySet()
                .stream()
                .filter(it -> isEqualsTypeKey(dashboard, it))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(fullPath);
    }

    private static <S extends Dashboard> boolean isEqualsTypeKey(S dashboard, Map.Entry<String, DashboardConfig> it) {
        return Optional.ofNullable(it.getValue())
                .filter(dashboardConfig -> dashboardConfig.getDashboardDto() != null)
                .map(DashboardConfig::getDashboardDto)
                .stream()
                .anyMatch(dashboardDto -> dashboardDto.getTypeKey().equals(dashboard.getTypeKey()));
    }

    private String getApiFullPath(String fullPath) {
        return "/api" + fullPath;
    }

    private String resolvePathWithTenant(String tenantKey, String specPath, Dashboard dashboard) {
        return specPath.replace("*.yml", dashboard.getTypeKey() + "-" + dashboard.getId() + ".yml")
                       .replace("{" + TENANT_NAME + "}", tenantKey);
    }

    private String getTenantKeyValue() {
        return TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder).toUpperCase();
    }

    public void deleteDashboard(Dashboard dashboard) {
        String tenant = getTenantKeyValue();
        String fullPath = getFullPath(dashboard);
        String dashboardConfigApiPath = getDashboardConfigApiPath(dashboard, tenant, fullPath);
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

    @Data
    @AllArgsConstructor
    private static class DashboardConfig {
        private DashboardDto dashboardDto;
        private String oldHash;
    }

}
