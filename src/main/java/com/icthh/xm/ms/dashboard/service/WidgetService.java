package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.commons.permission.annotation.FindWithPermission;
import com.icthh.xm.commons.permission.repository.PermittedRepository;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.repository.WidgetPermittedRepository;
import com.icthh.xm.ms.dashboard.repository.WidgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing Widget.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class WidgetService {

    private final WidgetRepository widgetRepository;
    private final WidgetPermittedRepository widgetPermittedRepository;

    /**
     * Save a widget.
     *
     * @param widget the entity to save
     * @return the persisted entity
     */
    public Widget save(Widget widget) {
        return widgetRepository.save(widget);
    }

    /**
     * Save and flush a widget.
     *
     * @param widget the entity to save
     * @return the persisted entity
     */
    public Widget saveAndFlush(Widget widget) {
        return widgetRepository.saveAndFlush(widget);
    }

    /**
     *  Get all the widgets.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    @FindWithPermission("WIDGET.GET_LIST")
    public List<Widget> findAll(String privilegeKey) {
        return widgetPermittedRepository.findAll(Widget.class, privilegeKey);
    }

    /**
     *  Get one widget by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Widget findOne(Long id) {
        return widgetRepository.findOne(id);
    }

    /**
     *  Delete the  widget by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        widgetRepository.delete(id);
    }

    /**
     * Finds Widgets by Dashboard.
     * @param id the Dashboard id
     * @return a list of widgets
     */
    @Transactional(readOnly = true)
    @FindWithPermission("WIDGET.GET_LIST.ITEM")
    public List<Widget> findByDashboardId(Long id, String privilegeKey) {
        return widgetPermittedRepository.findByDashboardId(id, privilegeKey);
    }
}
