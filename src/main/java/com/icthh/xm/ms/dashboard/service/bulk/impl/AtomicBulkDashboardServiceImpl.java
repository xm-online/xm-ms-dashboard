package com.icthh.xm.ms.dashboard.service.bulk.impl;

import static java.util.stream.Collectors.toList;

import com.icthh.xm.commons.exceptions.BusinessException;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.bulk.AtomicBulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.BulkDashboardResult;
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
    public BulkDashboardResult create(Collection<DashboardDto> dashboardItems) {
        try {
            Collection<Dashboard> dashboardEntities = dashboardItems.stream()
                .map(dashboardMapper::toEntity)
                .collect(toList());

            save(dashboardEntities);
            return new BulkDashboardResult();
        } catch (Exception ex) {
            throw new BusinessException("Could not perform bulk create : " + ex.getMessage());
        }
    }

    @Override
    public BulkDashboardResult update(Collection<DashboardDto> dashboardItems) {
        try {
            Collection<Dashboard> dashboardEntities = dashboardItems.stream()
                .map(this::update)
                .collect(toList());

            save(dashboardEntities);
            return new BulkDashboardResult();
        } catch (Exception ex) {
            throw new BusinessException("Could not perform bulk update : " + ex.getMessage());
        }
    }

    @Override
    public BulkDashboardResult delete(Collection<DashboardDto> dashboardItems) {
        try {

            Collection<Dashboard> dashboardEntities = dashboardItems.stream()
                .map(dashboardMapper::toEntity)
                .collect(toList());

            deleteAll(dashboardEntities);
            return new BulkDashboardResult();
        } catch (Exception ex) {
            throw new BusinessException("Could not perform bulk delete dashboards : " + ex.getMessage());
        }
    }

    @Transactional
    void save(Collection<Dashboard> dashboardEntities) {
        dashboardRepository.saveAll(dashboardEntities);
    }

    @Transactional
    void deleteAll(Collection<Dashboard> dashboardEntities) {
        dashboardRepository.deleteAll(dashboardEntities);
    }

    private Dashboard update(DashboardDto dashboardDto) {
        Dashboard dashboard = dashboardRepository.findById(dashboardDto.getId())
            .orElseThrow(() -> new BusinessException(
                "Could not find dashboard with id = " + dashboardDto.getId()));

        return dashboardMapper.merge(dashboardDto, dashboard);
    }
}
