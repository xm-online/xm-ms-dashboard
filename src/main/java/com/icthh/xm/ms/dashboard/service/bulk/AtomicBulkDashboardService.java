package com.icthh.xm.ms.dashboard.service.bulk;

import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;

import java.util.Collection;

public interface AtomicBulkDashboardService {

    void create(Collection<DashboardDto> dashboardItems);

    void update(Collection<DashboardDto> dashboardItems);

    void delete(Collection<DashboardDto> dashboardItems);

}
