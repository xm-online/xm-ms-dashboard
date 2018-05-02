package com.icthh.xm.ms.dashboard.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.service.DashboardService;
import com.icthh.xm.ms.dashboard.service.WidgetService;
import com.icthh.xm.ms.dashboard.web.rest.util.HeaderUtil;
import com.icthh.xm.ms.dashboard.web.rest.util.RespContentUtil;
import com.icthh.xm.ms.dashboard.web.rest.vm.DashboardVM;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

/**
 * REST controller for managing Dashboard.
 */
@RestController
@RequestMapping("/api")
public class DashboardResource {

    private static final String ENTITY_NAME = "dashboard";

    private final DashboardService dashboardService;
    private final WidgetService widgetService;

    private final DashboardResource dashboardResource;

    public DashboardResource(
                    DashboardService dashboardService,
                    WidgetService widgetService,
                    @Lazy DashboardResource dashboardResource) {
        this.dashboardService = dashboardService;
        this.widgetService = widgetService;
        this.dashboardResource = dashboardResource;
    }

    /**
     * POST  /dashboards : Create a new dashboard.
     *
     * @param dashboard the dashboard to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dashboard, or with status 400 (Bad Request) if the dashboard has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/dashboards")
    @Timed
    @PreAuthorize("hasPermission({'dashboard': #dashboard}, 'DASHBOARD.CREATE')")
    public ResponseEntity<Dashboard> createDashboard(@Valid @RequestBody Dashboard dashboard) throws URISyntaxException {
        if (dashboard.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new dashboard cannot already have an ID")).body(null);
        }
        Dashboard result = dashboardService.save(dashboard);
        return ResponseEntity.created(new URI("/api/dashboards/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /dashboards : Updates an existing dashboard.
     *
     * @param dashboard the dashboard to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dashboard,
     * or with status 400 (Bad Request) if the dashboard is not valid,
     * or with status 500 (Internal Server Error) if the dashboard couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/dashboards")
    @Timed
    @PreAuthorize("hasPermission({'id': #dashboard.id, 'newDashboard': #dashboard}, 'dashboard', 'DASHBOARD.UPDATE')")
    public ResponseEntity<Dashboard> updateDashboard(@Valid @RequestBody Dashboard dashboard) throws URISyntaxException {
        if (dashboard.getId() == null) {
            //in order to call method with permissions check
            return this.dashboardResource.createDashboard(dashboard);
        }
        Dashboard result = dashboardService.save(dashboard);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dashboard.getId().toString()))
            .body(result);
    }

    /**
     * GET  /dashboards : get all the dashboards.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of dashboards in body
     */
    @GetMapping("/dashboards")
    @Timed
    public List<Dashboard> getAllDashboards() {
        return dashboardService.findAll(null);
    }

    /**
     * GET  /dashboards/:id : get the "id" dashboard.
     *
     * @param id the id of the dashboard to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dashboard, or with status 404 (Not Found)
     */
    @GetMapping("/dashboards/{id}")
    @Timed
    @PostAuthorize("hasPermission({'returnObject': returnObject.body}, 'DASHBOARD.GET_LIST.ITEM')")
    public ResponseEntity<DashboardVM> getDashboard(@PathVariable Long id) {
        DashboardVM dashboard = dashboardService.findOne(id);
        return RespContentUtil.wrapOrNotFound(Optional.ofNullable(dashboard));
    }

    /**
     * GET  /dashboards/:id/widgets : get the widgets by dashboard "id".
     *
     * @param id the id of the dashboard to retrieve widgets
     * @return the ResponseEntity with status 200 (OK) and with widgets in body
     */
    @GetMapping("/dashboards/{id}/widgets")
    @Timed
    public List<Widget> getWidgets(@PathVariable Long id) {
        return widgetService.findByDashboardId(id, null);
    }

    /**
     * DELETE  /dashboards/:id : delete the "id" dashboard.
     *
     * @param id the id of the dashboard to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/dashboards/{id}")
    @Timed
    @PreAuthorize("hasPermission({'id': #id}, 'dashboard', 'DASHBOARD.DELETE')")
    public ResponseEntity<Void> deleteDashboard(@PathVariable Long id) {
        dashboardService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
