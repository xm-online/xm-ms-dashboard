package com.icthh.xm.ms.dashboard.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.service.DashboardService;
import com.icthh.xm.ms.dashboard.service.WidgetService;
import com.icthh.xm.ms.dashboard.web.rest.util.HeaderUtil;
import com.icthh.xm.ms.dashboard.web.rest.util.RespContentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Dashboard.
 */
@RestController
@RequestMapping("/api")
public class DashboardResource {

    private final Logger log = LoggerFactory.getLogger(DashboardResource.class);

    private static final String ENTITY_NAME = "dashboard";

    private final DashboardService dashboardService;

    private final WidgetService widgetService;

    public DashboardResource(DashboardService dashboardService, WidgetService widgetService) {
        this.dashboardService = dashboardService;
        this.widgetService = widgetService;
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
    public ResponseEntity<Dashboard> createDashboard(@Valid @RequestBody Dashboard dashboard) throws URISyntaxException {
        log.debug("REST request to save Dashboard : {}", dashboard);
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
    public ResponseEntity<Dashboard> updateDashboard(@Valid @RequestBody Dashboard dashboard) throws URISyntaxException {
        log.debug("REST request to update Dashboard : {}", dashboard);
        if (dashboard.getId() == null) {
            return createDashboard(dashboard);
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
        log.debug("REST request to get all Dashboards");
        return dashboardService.findAll();
    }

    /**
     * GET  /dashboards/:id : get the "id" dashboard.
     *
     * @param id the id of the dashboard to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dashboard, or with status 404 (Not Found)
     */
    @GetMapping("/dashboards/{id}")
    @Timed
    public ResponseEntity<Dashboard> getDashboard(@PathVariable Long id) {
        log.debug("REST request to get Dashboard : {}", id);
        Dashboard dashboard = dashboardService.findOne(id);
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
        log.debug("REST request to get Widget by Dashboard : {}", id);
        return widgetService.findByDashboardId(id);
    }

    /**
     * DELETE  /dashboards/:id : delete the "id" dashboard.
     *
     * @param id the id of the dashboard to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/dashboards/{id}")
    @Timed
    public ResponseEntity<Void> deleteDashboard(@PathVariable Long id) {
        log.debug("REST request to delete Dashboard : {}", id);
        dashboardService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
