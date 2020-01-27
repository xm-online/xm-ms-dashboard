package com.icthh.xm.ms.dashboard.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.ms.dashboard.service.bulk.AtomicBulkDashboardService;
import com.icthh.xm.ms.dashboard.service.bulk.BulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.BulkDashboardResult;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import java.util.Collection;
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
     * @param dashboardItems     request body with list of dashboards objects
     * @param isAtomicProcessing makes bulk items been processed in all or nothing style
     * @return result of each processed dashboard item
     */
    @Timed
    @PostMapping("/dashboards")
    @PreAuthorize("hasPermission('dashboard','DASHBOARD.CREATE')")
    public BulkDashboardResult createDashboards(
        @Valid @RequestBody Collection<DashboardDto> dashboardItems,
        @RequestParam(defaultValue = "false") boolean isAtomicProcessing
    ) {
        if (isAtomicProcessing)
            return atomicBulkDashboardService.create(dashboardItems);
        else
            return bulkDashboardService.create(dashboardItems);
    }

    /**
     * Request for non atomic bulk update of dashboards
     *
     * @param dashboardItems     request body with list of dashboards objects
     * @param isAtomicProcessing makes bulk items been processed in all or nothing style
     * @return result of each processed dashboard item
     */
    @Timed
    @PutMapping("/dashboards")
    @PreAuthorize("hasPermission('dashboard','DASHBOARD.UPDATE')")
    public BulkDashboardResult updateDashboards(
        @Valid @RequestBody Collection<DashboardDto> dashboardItems,
        @RequestParam(defaultValue = "false") boolean isAtomicProcessing
    ) {
        if (isAtomicProcessing)
            return atomicBulkDashboardService.update(dashboardItems);
        else
            return bulkDashboardService.update(dashboardItems);
    }

    /**
     * Request for non atomic bulk delete of dashboards
     *
     * @param dashboardItems     request body with list of dashboards objects
     * @param isAtomicProcessing makes bulk items been processed in all or nothing style
     * @return result of each processed dashboard item
     */
    @Timed
    @DeleteMapping("/dashboards")
    @PreAuthorize("hasPermission('dashboard','DASHBOARD.DELETE')")
    public BulkDashboardResult deleteDashboards(
        @Valid @RequestBody Collection<DashboardDto> dashboardItems,
        @RequestParam(defaultValue = "false") boolean isAtomicProcessing
    ) {
        if (isAtomicProcessing)
            return atomicBulkDashboardService.delete(dashboardItems);
        else
            return bulkDashboardService.delete(dashboardItems);
    }

}
