package com.icthh.xm.ms.dashboard.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.bulk.AtomicBulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.ImportDashboardDto;
import com.icthh.xm.ms.dashboard.service.dto.WidgetDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ImportDashboardService {

    private final AtomicBulkDashboardService atomicBulkDashboardService;
    private final DashboardRepository dashboardRepository;

    public void importDashboards(ImportDashboardDto imports) {

        List<Dashboard> toDelete = dashboardRepository.findAll();
        toDelete.forEach(dashboard -> log.info("delete dashboard before import: {}. widgets: {}",
                                               dashboard, dashboard.getWidgets()));
        dashboardRepository.deleteAll(toDelete);

        log.info("All existing dashboards was deleted before importing: {}", toDelete.size());

        Map<Long, Set<WidgetDto>> widgetByDashboard = imports.getWidgets()
                                                             .stream()
                                                             .collect(groupingBy(WidgetDto::getDashboard,
                                                                                 mapping(this::clearIds, toSet())));

        var dashboards = imports.getDashboards()
                                .stream()
                                .peek(dashboard -> {
                                    dashboard.setWidgets(widgetByDashboard.get(dashboard.getId()));
                                    dashboard.setId(null);
                                    log.info("dashboard with key: {}, name: {}, widgets.size: {} will be loaded",
                                             dashboard.getTypeKey(),
                                             dashboard.getName(),
                                             dashboard.getWidgets().size());
                                })
                                .collect(Collectors.toList());

        atomicBulkDashboardService.create(dashboards);

    }

    private WidgetDto clearIds(WidgetDto widgetDto) {
        widgetDto.setId(null);
        widgetDto.setDashboard(null);
        return widgetDto;
    }

}
