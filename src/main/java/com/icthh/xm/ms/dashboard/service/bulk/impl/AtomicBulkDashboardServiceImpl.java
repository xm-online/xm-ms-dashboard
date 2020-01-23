package com.icthh.xm.ms.dashboard.service.bulk.impl;

import static java.util.stream.Collectors.toList;

import com.icthh.xm.commons.exceptions.BusinessException;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
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
    public void create(BulkDashboard bulkDashboard) {
        try {
            Collection<Dashboard> dashboardEntities = bulkDashboard.getDashboardItems().stream()
                .map(dashboardMapper::toEntity)
                .collect(toList());

            save(dashboardEntities);
        } catch (Exception ex) {
            throw new BusinessException("Could not perform bulk create : " + ex.getMessage());
        }
    }

    @Override
    public void update(BulkDashboard bulkDashboard) {
        try {
            Collection<Dashboard> dashboardEntities = bulkDashboard.getDashboardItems().stream()
                .map(this::update)
                .collect(toList());

            save(dashboardEntities);
        } catch (Exception ex) {
            throw new BusinessException("Could not perform bulk update : " + ex.getMessage());
        }
    }

    @Override
    public void delete(BulkDashboard bulkDashboard) {
        try {

            Collection<Dashboard> dashboardEntities = bulkDashboard.getDashboardItems().stream()
                .map(dashboardMapper::toEntity)
                .collect(toList());

            delete(dashboardEntities);
        } catch (Exception ex) {
            throw new BusinessException("Could not perform bulk delete dashboards : " + ex.getMessage());
        }
    }

    @Transactional
    void save(Collection<Dashboard> dashboardEntities) {
        dashboardRepository.saveAll(dashboardEntities);
    }

    @Transactional
    void delete(Collection<Dashboard> dashboardEntities) {
        dashboardRepository.deleteAll(dashboardEntities);
    }

    private Dashboard update(DashboardDto dashboardDto) {
        Dashboard dashboard = dashboardRepository.findById(dashboardDto.getId())
            .orElseThrow(() -> new BusinessException(
                "Could not find dashboard with id = " + dashboardDto.getId()));

        return dashboardMapper.merge(dashboardDto, dashboard);
    }
}
