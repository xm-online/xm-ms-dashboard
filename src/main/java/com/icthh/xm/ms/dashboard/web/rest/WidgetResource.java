package com.icthh.xm.ms.dashboard.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.service.WidgetService;
import com.icthh.xm.ms.dashboard.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
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
 * REST controller for managing Widget.
 */
@RestController
@RequestMapping("/api")
public class WidgetResource {

    private final Logger log = LoggerFactory.getLogger(WidgetResource.class);

    private static final String ENTITY_NAME = "widget";

    private final WidgetService widgetService;

    public WidgetResource(WidgetService widgetService) {
        this.widgetService = widgetService;
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
    public ResponseEntity<Widget> createWidget(@Valid @RequestBody Widget widget) throws URISyntaxException {
        log.debug("REST request to save Widget : {}", widget);
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
    public ResponseEntity<Widget> updateWidget(@Valid @RequestBody Widget widget) throws URISyntaxException {
        log.debug("REST request to update Widget : {}", widget);
        if (widget.getId() == null) {
            return createWidget(widget);
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
    public List<Widget> getAllWidgets() {
        log.debug("REST request to get all Widgets");
        return widgetService.findAll();
    }

    /**
     * GET  /widgets/:id : get the "id" widget.
     *
     * @param id the id of the widget to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the widget, or with status 404 (Not Found)
     */
    @GetMapping("/widgets/{id}")
    @Timed
    public ResponseEntity<Widget> getWidget(@PathVariable Long id) {
        log.debug("REST request to get Widget : {}", id);
        Widget widget = widgetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(widget));
    }

    /**
     * DELETE  /widgets/:id : delete the "id" widget.
     *
     * @param id the id of the widget to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/widgets/{id}")
    @Timed
    public ResponseEntity<Void> deleteWidget(@PathVariable Long id) {
        log.debug("REST request to delete Widget : {}", id);
        widgetService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
