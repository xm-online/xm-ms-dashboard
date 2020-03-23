package com.icthh.xm.ms.dashboard.service.bulk.impl;

import com.icthh.xm.commons.exceptions.BusinessException;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.bulk.BulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.BulkDashboardItemStatus;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.LinkedList;

import static com.icthh.xm.ms.dashboard.service.dto.BulkOperationStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkDashboardServiceImpl implements BulkDashboardService {

    private final DashboardMapper dashboardMapper;
    private final DashboardRepository dashboardRepository;

    @Override
    public Collection<BulkDashboardItemStatus> create(Collection<DashboardDto> dashboardItems) {

        Collection<BulkDashboardItemStatus> itemStatuses = new LinkedList<>();

        dashboardItems
            .forEach(dashboardEntity -> create(dashboardEntity, itemStatuses));

        return itemStatuses;
    }

    @Override
    public Collection<BulkDashboardItemStatus> update(Collection<DashboardDto> dashboardItems) {
        Collection<BulkDashboardItemStatus> itemStatuses = new LinkedList<>();

        dashboardItems
            .forEach(dashboardDto -> update(dashboardDto, itemStatuses));

        return itemStatuses;
    }

    @Override
    public Collection<BulkDashboardItemStatus> delete(Collection<DashboardDto> dashboardItems) {

        Collection<BulkDashboardItemStatus> itemStatuses = new LinkedList<>();

        dashboardItems
            .forEach(dashboardEntity -> delete(dashboardEntity, itemStatuses));

        return itemStatuses;
    }

    void update(DashboardDto dashboardDto, Collection<BulkDashboardItemStatus> itemStatuses) {
        try {
            Dashboard dashboardEntity = dashboardRepository.findById(dashboardDto.getId())
                .orElseThrow(() -> new BusinessException(
                    "Could not find dashboard with id = " + dashboardDto.getId()));

            persist(dashboardMapper.merge(dashboardDto, dashboardEntity));
            itemStatuses.add(UPDATED.toItemStatus(dashboardDto));
        } catch (Exception ex) {
            itemStatuses.add(FAILED.toItemStatus(dashboardDto));
            log.error("Could not update dashboard entity ", ex);
        }
    }

    private void create(DashboardDto dashboardDto, Collection<BulkDashboardItemStatus> itemStatuses) {
        try {
            persist(dashboardMapper.toEntity(dashboardDto));
            itemStatuses.add(CREATED.toItemStatus(dashboardDto));
        } catch (Exception ex) {
            itemStatuses.add(FAILED.toItemStatus(dashboardDto));
            log.error("Could not create dashboard entity ", ex);
        }
    }

    private void delete(DashboardDto dashboardDto, Collection<BulkDashboardItemStatus> itemStatuses) {
        try {
            delete(dashboardDto.getId());
            itemStatuses.add(DELETED.toItemStatus(dashboardDto));
        } catch (Exception ex) {
            itemStatuses.add(FAILED.toItemStatus(dashboardDto));
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
