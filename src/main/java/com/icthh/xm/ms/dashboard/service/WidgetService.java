package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.repository.WidgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing Widget.
 */
@Service
@Transactional
public class WidgetService {

    private final Logger log = LoggerFactory.getLogger(WidgetService.class);

    private final WidgetRepository widgetRepository;

    public WidgetService(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    /**
     * Save a widget.
     *
     * @param widget the entity to save
     * @return the persisted entity
     */
    public Widget save(Widget widget) {
        log.debug("Request to save Widget : {}", widget);
        return widgetRepository.save(widget);
    }

    /**
     * Save and flush a widget.
     *
     * @param widget the entity to save
     * @return the persisted entity
     */
    public Widget saveAndFlush(Widget widget) {
        log.debug("Request to save Widget : {}", widget);
        return widgetRepository.saveAndFlush(widget);
    }

    /**
     *  Get all the widgets.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Widget> findAll() {
        log.debug("Request to get all Widgets");
        return widgetRepository.findAll();
    }

    /**
     *  Get one widget by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Widget findOne(Long id) {
        log.debug("Request to get Widget : {}", id);
        return widgetRepository.findOne(id);
    }

    /**
     *  Delete the  widget by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Widget : {}", id);
        widgetRepository.delete(id);
    }

    /**
     * Finds Widgets by Dashboard.
     * @param id the Dashboard id
     * @return a list of widgets
     */
    @Transactional(readOnly = true)
    public List<Widget> findByDashboardId(Long id) {
        log.debug("Request to find Widget by Dashboard id : {}", id);
        return widgetRepository.findByDashboardId(id);
    }
}
