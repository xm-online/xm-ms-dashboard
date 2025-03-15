package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.commons.config.DataSpecificationService;
import com.icthh.xm.commons.listener.JsonListenerService;
import com.icthh.xm.commons.service.DefaultSpecProcessingService;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.ms.dashboard.domain.spec.UiDataSpec;
import com.icthh.xm.ms.dashboard.domain.spec.UiDataSpecs;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Component
public class UiDataSpecService extends DataSpecificationService<UiDataSpecs> {

    private final TenantContextHolder tenantContextHolder;

    public UiDataSpecService(JsonListenerService jsonListenerService,
                             DefaultSpecProcessingService<UiDataSpecs> specProcessingService,
                             TenantContextHolder tenantContextHolder) {
        super(UiDataSpecs.class, jsonListenerService, specProcessingService);
        this.tenantContextHolder = tenantContextHolder;
    }

    public Optional<UiDataSpec> getSpecByKeyAndTenant(String typeKey) {
        String tenantKey = tenantContextHolder.getTenantKey().toUpperCase();
        return getTenantSpecifications(tenantKey).values().stream()
            .map(UiDataSpecs::getItems)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .filter(f -> typeKey.equals(f.getKey()))
            .findFirst();
    }

    @Override
    public String specKey() { // define the base specification key
        return "ui-data-spec";
    }

    @Override
    public String folder() { // define the folder there your base specification files located
        return "dashboard/ui-data-spec";
    }
}
