package com.icthh.xm.ms.dashboard.service.bulk;

import com.icthh.xm.ms.dashboard.service.dto.BulkDashboardItemStatus;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;

import java.util.Collection;

public interface BulkDashboardService {

    Collection<BulkDashboardItemStatus> create(Collection<DashboardDto> dashboardItems);

    Collection<BulkDashboardItemStatus> update(Collection<DashboardDto> dashboardItems);

    Collection<BulkDashboardItemStatus> delete(Collection<DashboardDto> dashboardItems);

}
