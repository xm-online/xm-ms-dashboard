package com.icthh.xm.ms.dashboard.repository.impl;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.repository.IdRepository;
import com.icthh.xm.ms.dashboard.repository.WidgetRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ConfigWidgetRepository implements WidgetRepository {

    private final ConfigDashboardRefreshableRepository refreshableRepository;
    private final DashboardMapper dashboardMapper;
    private final DashboardRepository dashboardRepository;
    private final IdRepository idRepository;

    @Override
    public Widget save(Widget widget) {
        Dashboard dashboard = widget.getDashboard();
        if (dashboard == null) {
            throw new IllegalStateException("Cannot create widget without dashboard in Config store type");
        }

        if (dashboard.getId() == null) {
            dashboard.setId(idRepository.getNextId());
        } else {
            dashboard = dashboardRepository.findOneById(dashboard.getId());
        }

        if (widget.getId() == null) {
            widget.setId(idRepository.getNextId());
        }
        dashboard.getWidgets().remove(widget);
        dashboard.addWidgets(widget);
        dashboardRepository.save(dashboard);
        return widget;
    }

    @Override
    public Widget saveAndFlush(Widget widget) {
        return save(widget);
    }

    @Override
    public Optional<Widget> findById(Long id) {
        return refreshableRepository.getDashboards().stream()
            .map(dashboardMapper::toFullEntity)
            .flatMap(dashboard -> dashboard.getWidgets().stream())
            .filter(widget -> widget.getId().equals(id))
            .findAny();
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(widget -> {
            Dashboard dashboard = widget.getDashboard();
            dashboard.getWidgets().remove(widget);
            refreshableRepository.saveDashboard(dashboard);
        });
    }

    @Override
    public Widget findResourceById(Long id) {
        return findById(id).orElse(null);
    }

    @Override
    public Page<Map<String, Object>> findAllAudits(Pageable pageable) {
        throw new NotImplementedException();
    }

    @Override
    public Page<Map<String, Object>> findAuditsById(Long id, Pageable pageable) {
        throw new NotImplementedException();
    }
}
