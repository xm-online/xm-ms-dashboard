package com.icthh.xm.ms.dashboard.service.bulk.impl;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.exception.ResourceNotFoundException;
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

        bulkDashboard.getDashboardItems().stream()
            .map(dashboardMapper::toEntity)
            .forEach(dashboardEntity -> saveEntity(dashboardEntity, saveResult));

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

        bulkDashboard.getDashboardItems().stream()
            .map(dashboardMapper::toEntity)
            .forEach(dashboardEntity -> deleteEntity(dashboardEntity, deleteResult));

        return deleteResult;
    }

    private void update(DashboardDto dashboardDto, BulkDashboardResult bulkDashboardResult) {
        Dashboard dashboard = dashboardRepository.findById(dashboardDto.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Could not find dashboard with id = " + dashboardDto.getId()));

        saveEntity(dashboardMapper.merge(dashboardDto, dashboard), bulkDashboardResult);
    }

    private void saveEntity(Dashboard dashboardEntity, BulkDashboardResult saveResult) {
        try {
            persist(dashboardEntity);
            saveResult.created(dashboardMapper.toDto(dashboardEntity));
        } catch (Exception ex) {
            saveResult.failed(dashboardMapper.toDto(dashboardEntity));
            log.error("Could not save dashboard entity ", ex);
        }
    }

    private void deleteEntity(Dashboard dashboardEntity, BulkDashboardResult saveResult) {
        try {
            delete(dashboardEntity);
            saveResult.deleted(dashboardMapper.toDto(dashboardEntity));
        } catch (Exception ex) {
            saveResult.failed(dashboardMapper.toDto(dashboardEntity));
            log.error("Could not delete dashboard entity ", ex);
        }
    }

    @Transactional
    void persist(Dashboard dashboardEntity) {
        dashboardRepository.save(dashboardEntity);
    }

    @Transactional
    void delete(Dashboard dashboardEntity) {
        dashboardRepository.delete(dashboardEntity);
    }
}
