package com.icthh.xm.ms.dashboard.service;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icthh.xm.commons.config.client.api.RefreshableConfiguration;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.domain.DashboardSpec;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardSpecService implements RefreshableConfiguration {

    private final Map<String, DashboardSpec> dashboardSpec = new ConcurrentHashMap<>();

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
                                                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final ApplicationProperties applicationProperties;
    private final TenantContextHolder tenantContextHolder;

    @Override
    public void onRefresh(String updatedKey, String config) {

        String specificationPathPattern = applicationProperties.getSpecificationPathPattern();
        try {
            String tenant = matcher.extractUriTemplateVariables(specificationPathPattern, updatedKey).get("tenantName");
            if (isBlank(config)) {
                dashboardSpec.remove(tenant);
                log.info("Specification for tenant {} was removed", tenant);
                return;
            }
            DashboardSpec spec = mapper.readValue(config, DashboardSpec.class);
            dashboardSpec.put(tenant, spec);
            log.info("Specification was for tenant {} updated", tenant);
        } catch (Exception e) {
            log.error("Error read specification from path " + updatedKey, e);
        }
    }

    @Override
    public boolean isListeningConfiguration(String updatedKey) {
        String specificationPathPattern = applicationProperties.getSpecificationPathPattern();
        return matcher.match(specificationPathPattern, updatedKey);
    }

    @Override
    public void onInit(String configKey, String configValue) {
        onRefresh(configKey, configValue);
    }

    public Optional<DashboardSpec> getDashboardSpec() {
        String tenant = getTenantKeyValue();
        return Optional.ofNullable(dashboardSpec.get(tenant));
    }

    private String getTenantKeyValue() {
        return TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder).toUpperCase();
    }
}
