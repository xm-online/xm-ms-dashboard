package com.icthh.xm.ms.dashboard.service.bulk.impl;

import com.icthh.xm.commons.exceptions.BusinessException;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.bulk.AtomicBulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class AtomicBulkDashboardServiceImpl implements AtomicBulkDashboardService {

    private final DashboardMapper dashboardMapper;
    private final DashboardRepository dashboardRepository;

    @Override
    public void create(Collection<DashboardDto> dashboardItems) {
        Collection<Dashboard> dashboardEntities = dashboardItems.stream()
            .map(dashboardMapper::toEntity)
            .collect(toList());

        save(dashboardEntities);
    }

    @Override
    @Transactional
    public void update(Collection<DashboardDto> dashboardItems) {
        dashboardItems.forEach(this::update);
    }

    @Override
    public void delete(Collection<DashboardDto> dashboardItems) {
        Collection<Dashboard> dashboardEntities = dashboardItems.stream()
            .map(dashboardMapper::toFullEntity)
            .collect(toList());

        deleteAll(dashboardEntities);
    }

    @Transactional
    void save(Collection<Dashboard> dashboardEntities) {
        dashboardRepository.saveAll(dashboardEntities);
    }

    void deleteAll(Collection<Dashboard> dashboardEntities) {
        dashboardRepository.deleteAll(dashboardEntities);
    }

    private Dashboard update(DashboardDto dashboardDto) {
        Dashboard dashboard = dashboardRepository.findById(dashboardDto.getId())
            .orElseThrow(() -> new BusinessException("Could not find dashboard with id = " + dashboardDto.getId()));

        return dashboardMapper.merge(dashboardDto, dashboard);
    }
}
