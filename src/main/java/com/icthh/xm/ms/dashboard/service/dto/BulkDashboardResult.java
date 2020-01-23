package com.icthh.xm.ms.dashboard.service.dto;

import static com.icthh.xm.ms.dashboard.service.dto.BulkOperationStatus.CREATED;
import static com.icthh.xm.ms.dashboard.service.dto.BulkOperationStatus.DELETED;
import static com.icthh.xm.ms.dashboard.service.dto.BulkOperationStatus.FAILED;
import static com.icthh.xm.ms.dashboard.service.dto.BulkOperationStatus.UPDATED;

import java.util.ArrayList;
import java.util.Collection;
import lombok.Data;

@Data
public class BulkDashboardResult {

    private final Collection<BulkDashboardItemStatus> itemStatuses = new ArrayList<>();

    public void created(DashboardDto dashboardDto) {
        itemStatuses.add(BulkDashboardItemStatus.builder()
            .dashboardItem(dashboardDto)
            .status(CREATED).build()
        );
    }

    public void updated(DashboardDto dashboardDto) {
        itemStatuses.add(BulkDashboardItemStatus.builder()
            .dashboardItem(dashboardDto)
            .status(UPDATED).build()
        );
    }

    public void deleted(DashboardDto dashboardDto) {
        itemStatuses.add(BulkDashboardItemStatus.builder()
            .dashboardItem(dashboardDto)
            .status(DELETED).build()
        );
    }

    public void failed(DashboardDto dashboardDto) {
        itemStatuses.add(BulkDashboardItemStatus.builder()
            .dashboardItem(dashboardDto)
            .status(FAILED).build()
        );
    }

}
