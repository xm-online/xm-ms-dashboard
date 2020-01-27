package com.icthh.xm.ms.dashboard.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.ms.dashboard.service.bulk.AtomicBulkDashboardService;
import com.icthh.xm.ms.dashboard.service.bulk.BulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.BulkDashboard;
import com.icthh.xm.ms.dashboard.service.dto.BulkDashboardResult;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing bulk non atomic operations with Dashboard resource.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bulk")
public class BulkDashboardResource {

    private final BulkDashboardService bulkDashboardService;
    private final AtomicBulkDashboardService atomicBulkDashboardService;

    /**
     * Request for non atomic bulk save of dashboards
     *
     * @param bulkDashboard request body with list of dashboards objects
     * @param isAtomic makes bulk items been processed in all or nothing style
     * @return result of each processed dashboard item
     */
    @Timed
    @PostMapping("/dashboards")
    @PreAuthorize("hasPermission('dashboard','DASHBOARD.CREATE')")
    public BulkDashboardResult createDashboards(
        @Valid @RequestBody BulkDashboard bulkDashboard,
        @RequestParam(defaultValue = "false") boolean isAtomic
    ) {
        if (isAtomic)
            return atomicBulkDashboardService.create(bulkDashboard);
        else
            return bulkDashboardService.create(bulkDashboard);
    }

    /**
     * Request for non atomic bulk update of dashboards
     *
     * @param bulkDashboard request body with list of dashboards objects
     * @param isAtomic makes bulk items been processed in all or nothing style
     * @return result of each processed dashboard item
     */
    @Timed
    @PutMapping("/dashboards")
    @PreAuthorize("hasPermission('dashboard','DASHBOARD.UPDATE')")
    public BulkDashboardResult updateDashboards(
        @Valid @RequestBody BulkDashboard bulkDashboard,
        @RequestParam(defaultValue = "false") boolean isAtomic
    ) {
        if (isAtomic)
            return atomicBulkDashboardService.update(bulkDashboard);
        else
            return bulkDashboardService.update(bulkDashboard);
    }

    /**
     * Request for non atomic bulk delete of dashboards
     *
     * @param bulkDashboard request body with list of dashboards objects
     * @param isAtomic makes bulk items been processed in all or nothing style
     * @return result of each processed dashboard item
     */
    @Timed
    @DeleteMapping("/dashboards")
    @PreAuthorize("hasPermission('dashboard','DASHBOARD.DELETE')")
    public BulkDashboardResult deleteDashboards(
        @Valid @RequestBody BulkDashboard bulkDashboard,
        @RequestParam(defaultValue = "false") boolean isAtomic
    ) {
        if (isAtomic)
            return atomicBulkDashboardService.delete(bulkDashboard);
        else
            return bulkDashboardService.delete(bulkDashboard);
    }

}
