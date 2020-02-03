package com.icthh.xm.ms.dashboard.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.ms.dashboard.service.bulk.AtomicBulkDashboardService;
import com.icthh.xm.ms.dashboard.service.bulk.BulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.BulkDashboardItemStatus;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;

/**
 * REST controller for managing bulk atomic and non atomic operations with Dashboard resource.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bulk")
public class BulkDashboardResource {

    private final BulkDashboardService bulkDashboardService;
    private final AtomicBulkDashboardService atomicBulkDashboardService;

    /**
     * Request for atomic and non atomic bulk save of dashboards
     *
     * @param dashboardItems     request body with list of dashboards objects
     * @param isAtomicProcessing makes bulk items been processed in all or nothing style
     * @return result of each processed dashboard item
     */
    @Timed
    @PostMapping("/dashboards")
    @PreAuthorize("hasPermission('dashboard','DASHBOARD.CREATE')")
    public Collection<BulkDashboardItemStatus> createDashboards(
        @Valid @RequestBody Collection<DashboardDto> dashboardItems,
        @RequestParam(defaultValue = "false") boolean isAtomicProcessing
    ) {
        Collection<BulkDashboardItemStatus> result = new ArrayList<>();

        if (isAtomicProcessing)
            atomicBulkDashboardService.create(dashboardItems);
        else
            result = bulkDashboardService.create(dashboardItems);

        return result;
    }

    /**
     * Request for atomic and non atomic bulk update of dashboards
     *
     * @param dashboardItems     request body with list of dashboards objects
     * @param isAtomicProcessing makes bulk items been processed in all or nothing style
     * @return result of each processed dashboard item
     */
    @Timed
    @PutMapping("/dashboards")
    @PreAuthorize("hasPermission('dashboard','DASHBOARD.UPDATE')")
    public Collection<BulkDashboardItemStatus> updateDashboards(
        @Valid @RequestBody Collection<DashboardDto> dashboardItems,
        @RequestParam(defaultValue = "false") boolean isAtomicProcessing
    ) {
        Collection<BulkDashboardItemStatus> result = new ArrayList<>();

        if (isAtomicProcessing)
            atomicBulkDashboardService.update(dashboardItems);
        else
            result = bulkDashboardService.update(dashboardItems);

        return result;
    }

    /**
     * Request for atomic and non atomic bulk delete of dashboards
     *
     * @param dashboardItems     request body with list of dashboards objects
     * @param isAtomicProcessing makes bulk items been processed in all or nothing style
     * @return result of each processed dashboard item
     */
    @Timed
    @DeleteMapping("/dashboards")
    @PreAuthorize("hasPermission('dashboard','DASHBOARD.DELETE')")
    public Collection<BulkDashboardItemStatus> deleteDashboards(
        @Valid @RequestBody Collection<DashboardDto> dashboardItems,
        @RequestParam(defaultValue = "false") boolean isAtomicProcessing
    ) {
        Collection<BulkDashboardItemStatus> result = new ArrayList<>();

        if (isAtomicProcessing)
            atomicBulkDashboardService.delete(dashboardItems);
        else
            result = bulkDashboardService.delete(dashboardItems);

        return result;
    }

}
