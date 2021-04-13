package com.icthh.xm.ms.dashboard.repository.impl.permitted;

import static java.util.stream.Collectors.toList;

import com.icthh.xm.commons.permission.service.PermissionCheckService;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardPermittedRepository;
import com.icthh.xm.ms.dashboard.repository.impl.ConfigDashboardRefreshableRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ConfigDashboardPermittedRepository extends ConfigPermittedRepository implements
    DashboardPermittedRepository {

    private final ConfigDashboardRefreshableRepository refreshableRepository;
    private final DashboardMapper dashboardMapper;

    protected ConfigDashboardPermittedRepository(PermissionCheckService permissionService,
        ConfigDashboardRefreshableRepository refreshableRepository,
        DashboardMapper dashboardMapper) {
        super(permissionService);
        this.refreshableRepository = refreshableRepository;
        this.dashboardMapper = dashboardMapper;
    }

    @Override
    public List<Dashboard> findAll(String privilegeKey) {
        return refreshableRepository.getDashboards().stream()
            .map(dashboardMapper::toFullEntity)
            .filter(dashboard -> matchSpelExpression(privilegeKey, dashboard))
            .collect(toList());
    }

}
