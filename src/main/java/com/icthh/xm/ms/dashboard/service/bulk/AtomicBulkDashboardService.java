package com.icthh.xm.ms.dashboard.service.bulk;

import com.icthh.xm.ms.dashboard.service.dto.BulkDashboard;
import com.icthh.xm.ms.dashboard.service.dto.BulkDashboardResult;

public interface AtomicBulkDashboardService {

    BulkDashboardResult create(BulkDashboard bulkDashboard);

    BulkDashboardResult update(BulkDashboard bulkDashboard);

    BulkDashboardResult delete(BulkDashboard bulkDashboard);

}
