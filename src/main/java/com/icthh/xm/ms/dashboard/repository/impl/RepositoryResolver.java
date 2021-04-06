package com.icthh.xm.ms.dashboard.repository.impl;

import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.domain.DashboardSpec;
import com.icthh.xm.ms.dashboard.domain.DashboardSpec.DashboardStoreType;
import com.icthh.xm.ms.dashboard.service.DashboardSpecService;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;

public abstract class RepositoryResolver<T> {

    private final ApplicationProperties applicationProperties;

    @Getter
    private final Map<DashboardStoreType, T> repositories = new EnumMap<>(DashboardStoreType.class);
    private final DashboardSpecService dashboardSpecService;

    protected RepositoryResolver(ApplicationProperties applicationProperties,
                                 DashboardSpecService dashboardSpecService) {
        this.applicationProperties = applicationProperties;
        this.dashboardSpecService = dashboardSpecService;
    }

    public T retrieveRepository() {
        if (!applicationProperties.getStorage().isStoreConfigurationEnabled()) {
            return repositories.get(DashboardStoreType.RDBMS);
        }
        return dashboardSpecService.getDashboardSpec()
            .map(DashboardSpec::getDashboardStoreType)
            .map(repositories::get)
            .orElseThrow(() -> new IllegalStateException("Can't retrieve dashboard store specification"));
    }

}
