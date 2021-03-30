package com.icthh.xm.ms.dashboard.repository.impl;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icthh.xm.commons.config.client.api.RefreshableConfiguration;
import com.icthh.xm.commons.config.client.repository.TenantConfigRepository;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.repository.IdRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdRefreshableRepository implements RefreshableConfiguration, IdRepository {

    private final Map<String, DashboardId> dashboards = new ConcurrentHashMap<>();

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final ApplicationProperties applicationProperties;
    private final TenantContextHolder tenantContextHolder;
    private final TenantConfigRepository tenantConfigRepository;

    @Override
    public void onRefresh(String updatedKey, String config) {

        String specificationPathPattern = applicationProperties.getTenantDashboardPropertiesIdPathPattern();
        try {
            String tenant = matcher.extractUriTemplateVariables(specificationPathPattern, updatedKey).get("tenantName");
            if (isBlank(config)) {
                dashboards.remove(tenant);
                log.info("Specification for tenant {} was removed", tenant);
                return;
            }
            DashboardId spec = mapper.readValue(config, DashboardId.class);
            dashboards.put(tenant, spec);
            log.info("Specification was for tenant {} updated", tenant);
        } catch (Exception e) {
            log.error("Error read specification from path " + updatedKey, e);
        }
    }

    @Override
    public boolean isListeningConfiguration(String updatedKey) {
        String specificationPathPattern = applicationProperties.getTenantDashboardPropertiesIdPathPattern();
        return matcher.match(specificationPathPattern, updatedKey);
    }

    @Override
    public void onInit(String configKey, String configValue) {
        onRefresh(configKey, configValue);
    }

    @Override
    @SneakyThrows
    public Long getNextId() {
        String tenant = getTenantKeyValue();
        DashboardId dashboardId = dashboards.getOrDefault(tenant, new DashboardId());

        if (dashboardId.getId() == null) {
            dashboardId.setId(0L);
        }
        dashboardId.setId(dashboardId.getId() + 1);
        dashboards.put(tenant, dashboardId);
        tenantConfigRepository.updateConfigFullPath(tenant,
                                            "/api"
                                                    + applicationProperties.getTenantDashboardPropertiesIdPathPattern(),
                                                    mapper.writeValueAsString(dashboardId));

        return dashboardId.getId();
    }

    private String getTenantKeyValue() {
        return TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder);
    }

    @Data
    public static class DashboardId {
        Long id;
    }

}
