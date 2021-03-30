package com.icthh.xm.ms.dashboard.repository.impl.permitted;

import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.DashboardSpec.DashboardStoreType;
import com.icthh.xm.ms.dashboard.repository.DashboardPermittedRepository;
import com.icthh.xm.ms.dashboard.repository.impl.RepositoryResolver;
import com.icthh.xm.ms.dashboard.service.DashboardSpecService;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class DashboardPermittedRepositoryResolver extends RepositoryResolver<DashboardPermittedRepository> implements
    DashboardPermittedRepository {

    public DashboardPermittedRepositoryResolver(DefaultDashboardPermittedRepository defaultDashboardPermittedRepository,
        ConfigDashboardPermittedRepository configDashboardPermittedRepository,
        ApplicationProperties applicationProperties,
        DashboardSpecService dashboardSpecService) {

        super(applicationProperties, dashboardSpecService);
        getRepositories().put(DashboardStoreType.DB, defaultDashboardPermittedRepository);
        getRepositories().put(DashboardStoreType.CONFIG, configDashboardPermittedRepository);
    }

    @Override
    public List<Dashboard> findAll(String privilegeKey) {
        return retrieveRepository().findAll(privilegeKey);
    }
}
