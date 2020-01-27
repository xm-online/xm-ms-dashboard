package com.icthh.xm.ms.dashboard.service.bulk;

import com.icthh.xm.ms.dashboard.service.dto.BulkDashboardResult;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import java.util.Collection;

public interface BulkDashboardService {

    BulkDashboardResult create(Collection<DashboardDto> dashboardItems);

    BulkDashboardResult update(Collection<DashboardDto> dashboardItems);

    BulkDashboardResult delete(Collection<DashboardDto> dashboardItems);

}
