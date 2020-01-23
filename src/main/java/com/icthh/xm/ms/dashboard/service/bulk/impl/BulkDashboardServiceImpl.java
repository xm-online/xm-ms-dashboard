package com.icthh.xm.ms.dashboard.service.bulk.impl;

import com.icthh.xm.commons.exceptions.BusinessException;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.bulk.BulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.BulkDashboard;
import com.icthh.xm.ms.dashboard.service.dto.BulkDashboardResult;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
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
    public BulkDashboardResult create(BulkDashboard bulkDashboard) {

        BulkDashboardResult saveResult = new BulkDashboardResult();

        bulkDashboard.getDashboardItems()
            .forEach(dashboardEntity -> create(dashboardEntity, saveResult));

        return saveResult;
    }

    @Override
    public BulkDashboardResult update(BulkDashboard bulkDashboard) {
        BulkDashboardResult bulkDashboardResult = new BulkDashboardResult();

        bulkDashboard.getDashboardItems()
            .forEach(dashboardDto -> update(dashboardDto, bulkDashboardResult));

        return bulkDashboardResult;
    }

    @Override
    public BulkDashboardResult delete(BulkDashboard bulkDashboard) {

        BulkDashboardResult deleteResult = new BulkDashboardResult();

        bulkDashboard.getDashboardItems()
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

    void delete(Long id) {
        dashboardRepository.deleteById(id);
    }
}
