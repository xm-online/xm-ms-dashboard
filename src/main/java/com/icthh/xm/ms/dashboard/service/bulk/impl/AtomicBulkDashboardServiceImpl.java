package com.icthh.xm.ms.dashboard.service.bulk.impl;

import static java.util.stream.Collectors.toList;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.exception.ResourceNotFoundException;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.bulk.AtomicBulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.BulkDashboard;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import java.util.Collection;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtomicBulkDashboardServiceImpl implements AtomicBulkDashboardService {

    private final DashboardMapper dashboardMapper;
    private final DashboardRepository dashboardRepository;

    @Override
    @Transactional
    public void create(BulkDashboard bulkDashboard) {
        Collection<Dashboard> dashboardEntities = bulkDashboard.getDashboardItems().stream()
            .map(dashboardMapper::toEntity)
            .collect(toList());

        dashboardRepository.saveAll(dashboardEntities);
    }

    @Override
    @Transactional
    public void update(BulkDashboard bulkDashboard) {
        Collection<Dashboard> dashboardEntities = bulkDashboard.getDashboardItems().stream()
            .map(this::update)
            .collect(toList());

        dashboardRepository.saveAll(dashboardEntities);
    }

    private Dashboard update(DashboardDto dashboardDto) {
        Dashboard dashboard = dashboardRepository.findById(dashboardDto.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Could not find dashboard with id = " + dashboardDto.getId()));

        return dashboardMapper.merge(dashboardDto, dashboard);
    }

    @Override
    @Transactional
    public void delete(BulkDashboard bulkDashboard) {
        Collection<Dashboard> dashboardEntities = bulkDashboard.getDashboardItems().stream()
            .map(dashboardMapper::toEntity)
            .collect(toList());

        dashboardRepository.deleteAll(dashboardEntities);
    }

}
