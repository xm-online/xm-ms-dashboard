package com.icthh.xm.ms.dashboard.repository.impl;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icthh.xm.commons.config.client.api.RefreshableConfiguration;
import com.icthh.xm.commons.config.client.repository.TenantConfigRepository;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.repository.IdRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdRefreshableRepository implements RefreshableConfiguration, IdRepository {

    private final Map<String, DashboardCounterState> dashboards = new ConcurrentHashMap<>();

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
            String oldHash = DigestUtils.sha1Hex(config);
            DashboardIdDto dashboardIdDto = mapper.readValue(config, DashboardIdDto.class);
            DashboardCounterState state = getDashboardCounterState(tenant);
            state.globalCounterState.set(new DashboardGlobalCounterState(dashboardIdDto.getId(), oldHash));
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
        DashboardCounterState dashboardCounterState = getDashboardCounterState(tenant);
        DashboardLocalCounter localCounter = dashboardCounterState.getLocalCounter();
        Optional<Long> counterValue = localCounter.getNextCounter();
        if (counterValue.isPresent()) {
            return counterValue.get();
        } else {
            // for avoid conflict exception on bulk import
            return getNextIdSynchronized(tenant, dashboardCounterState);
        }
    }

    private synchronized Long getNextIdSynchronized(String tenant, DashboardCounterState dashboardCounterState) throws JsonProcessingException {
        DashboardLocalCounter localCounter = dashboardCounterState.getLocalCounter();
        Optional<Long> counterValue = localCounter.getNextCounter();
        if (counterValue.isPresent()) {
            return counterValue.get();
        } else {
            DashboardLocalCounter dashboardLocalCounter = acquireCounterResource(tenant, 1000);
            dashboardCounterState.localCounter.set(dashboardLocalCounter);
            return dashboardLocalCounter.getNextCounter().orElseThrow(IllegalStateException::new);
        }
    }

    private DashboardLocalCounter acquireCounterResource(String tenant, long count) throws JsonProcessingException {
        String path = "/api" + applicationProperties.getTenantDashboardPropertiesIdPathPattern();
        DashboardGlobalCounterState dashboardCounterState = getDashboardCounterState(tenant).globalCounterState.get();
        DashboardIdDto value = new DashboardIdDto();
        Long currentValue = dashboardCounterState.currentValue;
        value.setId(currentValue + count);
        String body = mapper.writeValueAsString(value);
        tenantConfigRepository.updateConfigFullPath(tenant, path, body, dashboardCounterState.oldHash);
        return new DashboardLocalCounter(currentValue, count);
    }

    private DashboardCounterState getDashboardCounterState(String tenant) {
        return dashboards.computeIfAbsent(tenant, key -> new DashboardCounterState());
    }

    private String getTenantKeyValue() {
        return TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder);
    }

    public static class DashboardCounterState {
        final AtomicReference<DashboardGlobalCounterState> globalCounterState = new AtomicReference<>(
                new DashboardGlobalCounterState(0L, null)
        );
        final AtomicReference<DashboardLocalCounter> localCounter = new AtomicReference<>(
                new DashboardLocalCounter(0L, 0L)
        );

        public DashboardLocalCounter getLocalCounter() {
            return localCounter.get();
        }
    }

    public static class DashboardLocalCounter {
        final AtomicLong availableCounterResources;
        final Long startCounterValue;
        final Long counterResource;

        public DashboardLocalCounter(Long startCounterValue, Long counterResource) {
            this.availableCounterResources = new AtomicLong(counterResource);
            this.startCounterValue = startCounterValue;
            this.counterResource = counterResource;
        }

        public Optional<Long> getNextCounter() {
            long currentValue = availableCounterResources.decrementAndGet();
            if (currentValue <= 0) {
                return Optional.empty();
            }
            return Optional.of(startCounterValue + (counterResource - currentValue));
        }
    }

    @RequiredArgsConstructor
    public static class DashboardGlobalCounterState {
        private final Long currentValue;
        private final String oldHash;
    }

    @Data
    public static class DashboardIdDto {
        Long id;
    }

}
