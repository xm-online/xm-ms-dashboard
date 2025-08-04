package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.commons.lep.LogicExtensionPoint;
import com.icthh.xm.commons.lep.spring.LepService;
import com.icthh.xm.commons.permission.annotation.FindWithPermission;
import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.repository.WidgetPermittedRepository;
import com.icthh.xm.ms.dashboard.repository.WidgetRepository;
import com.icthh.xm.ms.dashboard.service.dto.WidgetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Widget.
 */
@Service
@Transactional
@RequiredArgsConstructor
@LepService(group = "service.widget")
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
    @PrivilegeDescription("Privilege to get all the widgets")
    public List<WidgetDto> findAll(String privilegeKey) {
        return widgetPermittedRepository.findAll(privilegeKey)
            .stream().map(WidgetDto::new).collect(Collectors.toList());
    }

    /**
     *  Get one widget by id or returns null
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    @LogicExtensionPoint(value = "FindOne")
    public WidgetDto findOne(Long id) {
        Widget widget = widgetRepository.findById(id).orElse(null);
        return widget != null ? new WidgetDto(widget) : null;
    }

    /**
     *  Delete the  widget by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        widgetRepository.deleteById(id);
    }

    /**
     * Finds Widgets by Dashboard.
     * @param id the Dashboard id
     * @return a list of widgets
     */
    @Transactional(readOnly = true)
    @FindWithPermission("WIDGET.GET_LIST.ITEM")
    @PrivilegeDescription("Privilege to finds widgets by dashboard")
    @LogicExtensionPoint(value = "FindByDashboardId")
    public List<Widget> findByDashboardId(Long id, String privilegeKey) {
        return widgetPermittedRepository.findByDashboardId(id, privilegeKey);
    }

    @Transactional(readOnly = true)
    public Page<Map<String, Object>> findAuditsById(Long id, Pageable pageable) {
        return widgetRepository.findAuditsById(id, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Map<String, Object>> findAllAudits(Pageable pageable) {
        return widgetRepository.findAllAudits(pageable);
    }
}
