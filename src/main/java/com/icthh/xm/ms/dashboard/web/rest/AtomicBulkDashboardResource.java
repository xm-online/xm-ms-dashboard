package com.icthh.xm.ms.dashboard.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.ms.dashboard.service.bulk.AtomicBulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.BulkDashboard;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AtomicBulkDashboardResource {

    AtomicBulkDashboardService getAtomicBulkDashboardService();

    /**
     * Request for atomic bulk save of dashboards
     *
     * @param bulkDashboard request body with list of dashboards objects
     * @return result of each processed dashboard item
     */
    @Timed
    @PostMapping("/dashboards/atomic")
    @PreAuthorize("hasPermission('DASHBOARD.CREATE')")
    default void atomicCreateDashboards(@Valid @RequestBody BulkDashboard bulkDashboard) {
        getAtomicBulkDashboardService().create(bulkDashboard);
    }

    /**
     * Request for atomic bulk update of dashboards
     *
     * @param bulkDashboard request body with list of dashboards objects
     * @return result of each processed dashboard item
     */
    @Timed
    @PutMapping("/dashboards/atomic")
    @PreAuthorize("hasPermission('DASHBOARD.UPDATE')")
    default void atomicUpdateDashboards(@Valid @RequestBody BulkDashboard bulkDashboard) {
        getAtomicBulkDashboardService().update(bulkDashboard);
    }

    /**
     * Request for atomic bulk delete of dashboards
     *
     * @param bulkDashboard request body with list of dashboards objects
     * @return result of each processed dashboard item
     */
    @Timed
    @DeleteMapping("/dashboards/atomic")
    @PreAuthorize("hasPermission('DASHBOARD.DELETE')")
    default void atomicDeleteDashboards(@Valid @RequestBody BulkDashboard bulkDashboard) {
        getAtomicBulkDashboardService().delete(bulkDashboard);
    }
}
