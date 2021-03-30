package com.icthh.xm.ms.dashboard.service.bulk.impl;

import com.icthh.xm.commons.logging.LoggingAspectConfig;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.bulk.AtomicBulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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

        dashboardRepository.findAllById(dtoById.keySet())
            .forEach(dashboard -> dashboardMapper.merge(dtoById.get(dashboard.getId()), dashboard));
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
        dashboardEntities.forEach(dashboardRepository::save);
    }

    void deleteAll(Collection<Dashboard> dashboardEntities) {
        dashboardRepository.deleteAll(dashboardEntities);
    }
}
