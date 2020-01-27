package com.icthh.xm.ms.dashboard.service.bulk.impl;

import com.icthh.xm.commons.exceptions.BusinessException;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.bulk.BulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.BulkDashboardResult;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import java.util.Collection;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkDashboardServiceImpl implements BulkDashboardService {

    private final DashboardMapper dashboardMapper;
    private final DashboardRepository dashboardRepository;

    @Override
    public BulkDashboardResult create(Collection<DashboardDto> dashboardItems) {

        BulkDashboardResult saveResult = new BulkDashboardResult();

        dashboardItems
            .forEach(dashboardEntity -> create(dashboardEntity, saveResult));

        return saveResult;
    }

    @Override
    public BulkDashboardResult update(Collection<DashboardDto> dashboardItems) {
        BulkDashboardResult bulkDashboardResult = new BulkDashboardResult();

        dashboardItems
            .forEach(dashboardDto -> update(dashboardDto, bulkDashboardResult));

        return bulkDashboardResult;
    }

    @Override
    public BulkDashboardResult delete(Collection<DashboardDto> dashboardItems) {

        BulkDashboardResult deleteResult = new BulkDashboardResult();

        dashboardItems
            .forEach(dashboardEntity -> delete(dashboardEntity, deleteResult));

        return deleteResult;
    }

    void update(DashboardDto dashboardDto, BulkDashboardResult saveResult) {
        try {
            Dashboard dashboardEntity = dashboardRepository.findById(dashboardDto.getId())
                .orElseThrow(() -> new BusinessException(
                    "Could not find dashboard with id = " + dashboardDto.getId()));

            persist(dashboardMapper.merge(dashboardDto, dashboardEntity));

            saveResult.updated(dashboardDto);
        } catch (Exception ex) {
            saveResult.failed(dashboardDto);
            log.error("Could not save dashboard entity ", ex);
        }
    }

    private void create(DashboardDto dashboardDto, BulkDashboardResult saveResult) {
        try {
            persist(dashboardMapper.toEntity(dashboardDto));
            saveResult.created(dashboardDto);
        } catch (Exception ex) {
            saveResult.failed(dashboardDto);
            log.error("Could not save dashboard entity ", ex);
        }
    }

    private void delete(DashboardDto dashboardDto, BulkDashboardResult saveResult) {
        try {
            delete(dashboardDto.getId());
            saveResult.deleted(dashboardDto);
        } catch (Exception ex) {
            saveResult.failed(dashboardDto);
            log.error("Could not delete dashboard entity ", ex);
        }
    }

    @Transactional
    void persist(Dashboard dashboardEntity) {
        dashboardRepository.save(dashboardEntity);
    }

    @Transactional
    void delete(Long id) {
        dashboardRepository.findById(id)
            .ifPresent(dashboardRepository::delete);
    }
}
