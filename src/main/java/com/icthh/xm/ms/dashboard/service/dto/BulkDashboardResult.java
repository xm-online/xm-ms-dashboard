package com.icthh.xm.ms.dashboard.service.dto;

import lombok.experimental.UtilityClass;

import static com.icthh.xm.ms.dashboard.service.dto.BulkOperationStatus.CREATED;
import static com.icthh.xm.ms.dashboard.service.dto.BulkOperationStatus.DELETED;
import static com.icthh.xm.ms.dashboard.service.dto.BulkOperationStatus.FAILED;
import static com.icthh.xm.ms.dashboard.service.dto.BulkOperationStatus.UPDATED;

@UtilityClass
public class BulkDashboardResult {

    public static BulkDashboardItemStatus created(DashboardDto dashboardDto) {
        return BulkDashboardItemStatus.builder()
            .dashboardItem(dashboardDto)
            .status(CREATED).build();
    }

    public static BulkDashboardItemStatus updated(DashboardDto dashboardDto) {
        return BulkDashboardItemStatus.builder()
            .dashboardItem(dashboardDto)
            .status(UPDATED).build();
    }

    public static BulkDashboardItemStatus deleted(DashboardDto dashboardDto) {
        return BulkDashboardItemStatus.builder()
            .dashboardItem(dashboardDto)
            .status(DELETED).build();
    }

    public static BulkDashboardItemStatus failed(DashboardDto dashboardDto) {
        return BulkDashboardItemStatus.builder()
            .dashboardItem(dashboardDto)
            .status(FAILED).build();
    }
}
