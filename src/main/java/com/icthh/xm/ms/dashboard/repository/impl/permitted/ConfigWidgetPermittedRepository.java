package com.icthh.xm.ms.dashboard.repository.impl.permitted;

import static java.util.stream.Collectors.toList;

import com.icthh.xm.commons.permission.service.PermissionCheckService;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.repository.WidgetPermittedRepository;
import com.icthh.xm.ms.dashboard.repository.impl.ConfigDashboardRefreshableRepository;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ConfigWidgetPermittedRepository extends ConfigPermittedRepository implements WidgetPermittedRepository {

    private final ConfigDashboardRefreshableRepository refreshableRepository;
    private final DashboardMapper dashboardMapper;
    private final DashboardRepository dashboardRepository;

    protected ConfigWidgetPermittedRepository(PermissionCheckService permissionService,
        ConfigDashboardRefreshableRepository refreshableRepository,
        DashboardMapper dashboardMapper, DashboardRepository dashboardRepository) {
        super(permissionService);
        this.refreshableRepository = refreshableRepository;
        this.dashboardMapper = dashboardMapper;
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public List<Widget> findAll(String privilegeKey) {
        return refreshableRepository.getDashboards().stream()
            .map(dashboardMapper::toFullEntity)
            .filter(dashboardDto -> matchSpelExpression(privilegeKey, dashboardDto))
            .map(Dashboard::getWidgets)
            .flatMap(Collection::stream)
            .collect(toList());
    }

    @Override
    public List<Widget> findByDashboardId(Long id, String privilegeKey) {
        Dashboard dashboard = dashboardRepository.findOneById(id);

        return dashboard.getWidgets().stream()
            .filter(widget -> matchSpelExpression(privilegeKey, widget))
            .collect(toList());
    }

}
