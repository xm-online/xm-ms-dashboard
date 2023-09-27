package com.icthh.xm.ms.dashboard.repository.impl;

import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.DashboardSpec.DashboardStoreType;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.DashboardSpecService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class DashboardRepositoryResolver extends RepositoryResolver<DashboardRepository> implements
    DashboardRepository {

    protected DashboardRepositoryResolver(ApplicationProperties applicationProperties,
        DashboardSpecService dashboardSpecService,
        DefaultDashboardRepositoryWrapper defaultDashboardRepository,
        ConfigDashboardRepository configDashboardRepository) {
        super(applicationProperties, dashboardSpecService);
        getRepositories().put(DashboardStoreType.RDBMS, defaultDashboardRepository);
        getRepositories().put(DashboardStoreType.MSCONFG, configDashboardRepository);
    }

    @Override
    public Dashboard findOneById(Long id) {
        return super.retrieveRepository().findOneById(id);
    }

    @Override
    public <S extends Dashboard> S save(S dashboard) {
        return retrieveRepository().save(dashboard);
    }

    @Override
    public Dashboard saveAndFlush(Dashboard dashboard) {
        return retrieveRepository().saveAndFlush(dashboard);
    }

    @Override
    public void deleteById(Long id) {
        retrieveRepository().deleteById(id);
    }

    @Override
    public List<Dashboard> findAll() {
        return retrieveRepository().findAll();
    }

    @Override
    public void deleteAll(Iterable<? extends Dashboard> toDelete) {
        retrieveRepository().deleteAll(toDelete);
    }

    @Override
    public List<Dashboard> findAllById(Set<Long> ids) {
        return retrieveRepository().findAllById(ids);
    }

    @Override
    public <S extends Dashboard> List<S> saveAll(Iterable<S> dashboardEntities) {
        return retrieveRepository().saveAll(dashboardEntities);
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
        return findOneById((Long) id);
    }
}
