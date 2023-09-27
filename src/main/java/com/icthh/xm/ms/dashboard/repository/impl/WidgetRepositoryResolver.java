package com.icthh.xm.ms.dashboard.repository.impl;

import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.domain.DashboardSpec.DashboardStoreType;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.repository.WidgetRepository;
import com.icthh.xm.ms.dashboard.service.DashboardSpecService;

import java.util.Map;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class WidgetRepositoryResolver extends RepositoryResolver<WidgetRepository> implements WidgetRepository {

    public WidgetRepositoryResolver(DefaultWidgetRepositoryWrapper defaultWidgetRepository,
        ConfigWidgetRepository configWidgetRepository,
        ApplicationProperties applicationProperties,
        DashboardSpecService dashboardSpecService) {

        super(applicationProperties, dashboardSpecService);
        getRepositories().put(DashboardStoreType.RDBMS, defaultWidgetRepository);
        getRepositories().put(DashboardStoreType.MSCONFG, configWidgetRepository);
    }

    @Override
    public Widget save(Widget widget) {
        return retrieveRepository().save(widget);
    }

    @Override
    public Widget saveAndFlush(Widget widget) {
        return retrieveRepository().saveAndFlush(widget);
    }

    @Override
    public Optional<Widget> findById(Long id) {
        return retrieveRepository().findById(id);
    }

    @Override
    public void deleteById(Long id) {
        retrieveRepository().deleteById(id);
    }

    @Override
    public Page<Map<String, Object>> findAllAudits(Pageable pageable) {
        return retrieveRepository().findAllAudits(pageable);
    }

    @Override
    public Page<Map<String, Object>> findAuditsById(Long id, Pageable pageable) {
        return retrieveRepository().findAuditsById(id, pageable);
    }

    @Override
    public Object findResourceById(Object id) {
        return retrieveRepository().findResourceById(id);
    }
}
