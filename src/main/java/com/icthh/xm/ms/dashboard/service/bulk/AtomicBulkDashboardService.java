package com.icthh.xm.ms.dashboard.service.bulk;

import com.icthh.xm.ms.dashboard.service.dto.BulkDashboard;

public interface AtomicBulkDashboardService {

    void create(BulkDashboard bulkDashboard);

    void update(BulkDashboard bulkDashboard);

    void delete(BulkDashboard bulkDashboard);

}
