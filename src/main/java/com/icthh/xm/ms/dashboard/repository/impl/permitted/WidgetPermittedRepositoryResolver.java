package com.icthh.xm.ms.dashboard.repository.impl.permitted;

import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import com.icthh.xm.ms.dashboard.domain.DashboardSpec.DashboardStoreType;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.repository.WidgetPermittedRepository;
import com.icthh.xm.ms.dashboard.repository.impl.RepositoryResolver;
import com.icthh.xm.ms.dashboard.service.DashboardSpecService;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class WidgetPermittedRepositoryResolver extends RepositoryResolver<WidgetPermittedRepository> implements
    WidgetPermittedRepository {

    public WidgetPermittedRepositoryResolver(DefaultWidgetPermittedRepository defaultWidgetPermittedRepository,
        ConfigWidgetPermittedRepository configWidgetPermittedRepository,
        ApplicationProperties applicationProperties,
        DashboardSpecService dashboardSpecService) {
        super(applicationProperties, dashboardSpecService);

        getRepositories().put(DashboardStoreType.RDBMS, defaultWidgetPermittedRepository);
        getRepositories().put(DashboardStoreType.MSCONFG, configWidgetPermittedRepository);
    }

    @Override
    public List<Widget> findAll(String privilegeKey) {
        return retrieveRepository().findAll(privilegeKey);
    }

    @Override
    public List<Widget> findByDashboardId(Long id, String privilegeKey) {
        return retrieveRepository().findByDashboardId(id, privilegeKey);
    }
}
