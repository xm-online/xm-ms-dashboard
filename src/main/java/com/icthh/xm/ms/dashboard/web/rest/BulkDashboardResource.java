package com.icthh.xm.ms.dashboard.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.ms.dashboard.service.bulk.AtomicBulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

/**
 * REST controller for managing bulk atomic and non atomic operations with Dashboard resource.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboards/bulk")
public class BulkDashboardResource {

    private final AtomicBulkDashboardService atomicBulkDashboardService;

    /**
     * Request for atomic and non atomic bulk save of dashboards
     *
     * @param dashboardItems request body with list of dashboards objects
     */
    @Timed
    @PostMapping
    @PreAuthorize("hasPermission('dashboard','DASHBOARD.CREATE.BULK')")
    @PrivilegeDescription("Privilege to create dashboard bulk")
    public void createDashboards(
        @Valid @RequestBody Collection<DashboardDto> dashboardItems
    ) {
        atomicBulkDashboardService.create(dashboardItems);
    }

    /**
     * Request for atomic and non atomic bulk update of dashboards
     *
     * @param dashboardItems request body with list of dashboards objects
     */
    @Timed
    @PutMapping
    @PreAuthorize("hasPermission('dashboard','DASHBOARD.UPDATE.BULK')")
    @PrivilegeDescription("Privilege to update dashboard bulk")
    public void updateDashboards(
        @Valid @RequestBody Collection<DashboardDto> dashboardItems
    ) {
        atomicBulkDashboardService.update(dashboardItems);
    }

    /**
     * Request for atomic and non atomic bulk delete of dashboards
     *
     * @param dashboardItems request body with list of dashboards objects
     */
    @Timed
    @DeleteMapping
    @PreAuthorize("hasPermission('dashboard','DASHBOARD.DELETE.BULK')")
    @PrivilegeDescription("Privilege to delete dashboard bulk")
    public void deleteDashboards(
        @Valid @RequestBody Collection<DashboardDto> dashboardItems
    ) {
        atomicBulkDashboardService.delete(dashboardItems);
    }
}
