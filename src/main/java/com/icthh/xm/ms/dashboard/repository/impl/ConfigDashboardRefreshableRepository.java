package com.icthh.xm.ms.dashboard.repository.impl;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icthh.xm.commons.config.client.api.RefreshableConfiguration;
import com.icthh.xm.commons.config.client.repository.TenantConfigRepository;
import com.icthh.xm.commons.config.domain.Configuration;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.repository.IdRepository;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    private final Map<String, Map<String, DashboardDto>> dashboardsByTenantByFile = new ConcurrentHashMap<>();

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final ApplicationProperties applicationProperties;
    private final TenantContextHolder tenantContextHolder;
    private final IdRepository idRepository;
    private final TenantConfigRepository tenantConfigRepository;

    @Override
    public void onRefresh(String updatedKey, String config) {
        try {
            String pathPattern = getPathPattern(updatedKey);
            String tenant = matcher.extractUriTemplateVariables(pathPattern, updatedKey).get(TENANT_NAME);
            updateByFileState(updatedKey, config, tenant);
            log.info("Specification was for tenant {} updated from file {}", tenant, updatedKey);
        } catch (Exception e) {
            log.error("Error read xm specification from path " + updatedKey, e);
        }
    }


    @SneakyThrows
    private void updateByFileState(String updatedKey, String config, String tenant) {
        Map<String, DashboardDto> dashboards = dashboardsByTenantByFile
            .computeIfAbsent(tenant, key -> new LinkedHashMap<>());

        if (StringUtils.isBlank(config)) {
            dashboards.remove(updatedKey);
            return;
        }

        DashboardDto dashboard = mapper.readValue(config, DashboardDto.class);
        dashboards.put(updatedKey, dashboard);
    }

    @Override
    public boolean isListeningConfiguration(String updatedKey) {
        String tenantDashboardsFolderPathPattern = applicationProperties.getTenantDashboardsFolderPathPattern();
        return matcher.match(tenantDashboardsFolderPathPattern, updatedKey);
    }

    @Override
    public void onInit(String configKey, String configValue) {
        onRefresh(configKey, configValue);
    }

    private String getPathPattern(String updatedKey) {
        String tenantDashboardsFolderPathPattern = applicationProperties.getTenantDashboardsFolderPathPattern();
        if (matcher.match(tenantDashboardsFolderPathPattern, updatedKey)) {
            return tenantDashboardsFolderPathPattern;
        }
        throw new IllegalStateException("Dashboards path does not match defined patterns");
    }

    public List<DashboardDto> getDashboards() {
        String tenant = getTenantKeyValue();
        return new ArrayList<>(dashboardsByTenantByFile.getOrDefault(tenant, Map.of()).values());
    }

    @SneakyThrows
    public <S extends Dashboard> S saveDashboard(S dashboard) {
        String tenant = getTenantKeyValue();
        String fullPath = getFullPath(dashboard);
        tenantConfigRepository.updateConfigFullPath(tenant, fullPath, mapper.writeValueAsString(dashboard));
        return dashboard;
    }

    private String getTenantKeyValue() {
        return TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder).toUpperCase();
    }

    public void deleteDashboard(Dashboard dashboard) {
        String tenant = getTenantKeyValue();
        String fullPath = getFullPath(dashboard);
        tenantConfigRepository.deleteConfigFullPath(tenant, fullPath);

    }

    public  <S extends Dashboard> String getFullPath(S dashboard) {
        String tenantDashboardsFolderPath = applicationProperties.getTenantDashboardsFolderPath();
        String fullPath = "/api"
            + tenantDashboardsFolderPath
            + dashboard.getTypeKey()
            + "-"
            + dashboard.getId()
            + ".yml";
        return fullPath;
    }

    private String sha1Hex(Configuration configuration) {
        return ofNullable(configuration).map(Configuration::getContent).map(DigestUtils::sha1Hex).orElse(null);
    }

}
