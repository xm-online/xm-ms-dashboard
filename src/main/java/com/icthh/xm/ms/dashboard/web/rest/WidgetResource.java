package com.icthh.xm.ms.dashboard.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.service.WidgetService;
import com.icthh.xm.ms.dashboard.web.rest.util.HeaderUtil;
import com.icthh.xm.ms.dashboard.service.dto.WidgetDto;
import io.github.jhipster.web.util.ResponseUtil;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.validation.Valid;

/**
 * REST controller for managing Widget.
 */
@RestController
@RequestMapping("/api")
public class WidgetResource {

    private static final String ENTITY_NAME = "widget";

    private final WidgetService widgetService;

    private final WidgetResource widgetResource;

    private final AuditReader auditReader;

    public WidgetResource(
                    WidgetService widgetService,
                    @Lazy WidgetResource widgetResource,
                    AuditReader auditReader) {
        this.widgetService = widgetService;
        this.widgetResource = widgetResource;
        this.auditReader = auditReader;
    }

    /**
     * POST  /widgets : Create a new widget.
     *
     * @param widget the widget to create
     * @return the ResponseEntity with status 201 (Created) and with body the new widget, or with status 400 (Bad Request) if the widget has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/widgets")
    @Timed
    @PreAuthorize("hasPermission({'widget': #widget}, 'WIDGET.CREATE')")
    @PrivilegeDescription("Privilege to create a new widget")
    public ResponseEntity<Widget> createWidget(@Valid @RequestBody Widget widget) throws URISyntaxException {
        if (widget.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new widget cannot already have an ID")).body(null);
        }
        Widget result = widgetService.save(widget);
        return ResponseEntity.created(new URI("/api/widgets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /widgets : Updates an existing widget.
     *
     * @param widget the widget to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated widget,
     * or with status 400 (Bad Request) if the widget is not valid,
     * or with status 500 (Internal Server Error) if the widget couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/widgets")
    @Timed
    @PreAuthorize("hasPermission({'id': #widget.id, 'newWidget': #widget}, 'widget', 'WIDGET.UPDATE')")
    @PrivilegeDescription("Privilege to updates an existing widget")
    public ResponseEntity<Widget> updateWidget(@Valid @RequestBody Widget widget) throws URISyntaxException {
        if (widget.getId() == null) {
            //in order to call method with permissions check
            return this.widgetResource.createWidget(widget);
        }
        Widget result = widgetService.save(widget);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, widget.getId().toString()))
            .body(result);
    }

    /**
     * GET  /widgets : get all the widgets.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of widgets in body
     */
    @GetMapping("/widgets")
    @Timed
    public List<WidgetDto> getAllWidgets() {
        return widgetService.findAll(null);
    }

    /**
     * GET  /widgets/:id : get the "id" widget.
     *
     * @param id the id of the widget to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the widget, or with status 404 (Not Found)
     */
    @GetMapping("/widgets/{id}")
    @Timed
    @PostAuthorize("hasPermission({'returnObject': returnObject.body}, 'WIDGET.GET_LIST.ITEM')")
    @PrivilegeDescription("Privilege to get the widget by id")
    public ResponseEntity<WidgetDto> getWidget(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(widgetService.findOne(id)));
    }

    /**
     * DELETE  /widgets/:id : delete the "id" widget.
     *
     * @param id the id of the widget to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/widgets/{id}")
    @Timed
    @PreAuthorize("hasPermission({'id': #id}, 'widget', 'WIDGET.DELETE')")
    @PrivilegeDescription("Privilege to delete the widget by id")
    public ResponseEntity<Void> deleteWidget(@PathVariable Long id) {
        widgetService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    @GetMapping("/widgets-audit/{id}")
    public ResponseEntity<Page<Map<String, Object>>> getWidgetAuditById(@PathVariable Long id,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "3") int size) {
        return ResponseEntity.ok(widgetService.findAuditsById(id, page, size));
    }

    @GetMapping("/widgets-audit/")
    public ResponseEntity<Page<Map<String, Object>>> getAllWidgetAudits(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "3") int size) {
        return ResponseEntity.ok(widgetService.findAllAudits(page, size));
    }
}
