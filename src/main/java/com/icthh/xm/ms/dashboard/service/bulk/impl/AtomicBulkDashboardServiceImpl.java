package com.icthh.xm.ms.dashboard.service.bulk.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.icthh.xm.commons.logging.LoggingAspectConfig;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.bulk.AtomicBulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtomicBulkDashboardServiceImpl implements AtomicBulkDashboardService {

    private final DashboardMapper dashboardMapper;
    private final DashboardRepository dashboardRepository;

    @Override
    @LoggingAspectConfig(inputCollectionAware = true)
    public void create(Collection<DashboardDto> dashboardItems) {
        Collection<Dashboard> dashboardEntities = dashboardItems.stream()
            .map(dashboardMapper::toEntity)
            .collect(toList());

        save(dashboardEntities);
    }

    @Override
    @Transactional
    @LoggingAspectConfig(inputCollectionAware = true)
    public void update(Collection<DashboardDto> dashboardItems) {

        Map<Long, DashboardDto> dtoById = dashboardItems.stream()
            .collect(toMap(DashboardDto::getId, Function.identity()));

        List<Dashboard> dashboards = dashboardRepository.findAllById(dtoById.keySet());
        dashboards.forEach(dashboard -> dashboardMapper.merge(dtoById.get(dashboard.getId()), dashboard));
        save(dashboards);

    }

    @Override
    @LoggingAspectConfig(inputCollectionAware = true)
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
}
