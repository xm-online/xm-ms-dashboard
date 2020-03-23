package com.icthh.xm.ms.dashboard.service.dto;

public enum BulkOperationStatus {
    CREATED, UPDATED, DELETED, FAILED;

    public final BulkDashboardItemStatus toItemStatus(DashboardDto dashboardDto) {
        return BulkDashboardItemStatus.builder()
            .dashboardItem(dashboardDto)
            .status(this)
            .build();
    }
}
