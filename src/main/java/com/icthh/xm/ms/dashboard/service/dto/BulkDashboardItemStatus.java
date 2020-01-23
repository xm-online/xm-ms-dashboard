package com.icthh.xm.ms.dashboard.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkDashboardItemStatus {

    private BulkOperationStatus status;
    private DashboardDto dashboardItem;

}
