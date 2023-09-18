package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.commons.permission.annotation.FindWithPermission;
import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.ms.dashboard.domain.RevInfo;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.repository.WidgetPermittedRepository;
import com.icthh.xm.ms.dashboard.repository.WidgetRepository;
import com.icthh.xm.ms.dashboard.service.dto.WidgetDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Widget.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class WidgetService {

    private final WidgetRepository widgetRepository;
    private final WidgetPermittedRepository widgetPermittedRepository;
    private final AuditReader auditReader;

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
    public List<Widget> findByDashboardId(Long id, String privilegeKey) {
        return widgetPermittedRepository.findByDashboardId(id, privilegeKey);
    }

    public List<Map<String, Object>> findAuditsById(Long id) {
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Widget.class,false, true)
            .add(AuditEntity.property("id").eq(id));
        return getResult(auditQuery);
    }

    public List<Map<String, Object>> findAllAudits() {
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Widget.class,false, true);
        return getResult(auditQuery);
    }

    public List<Map<String, Object>> getResult(AuditQuery auditQuery) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object entry : auditQuery.getResultList()) {
            Object[] row = (Object[]) entry;
            Widget widget = (Widget) row[0];
            RevInfo revInfo = (RevInfo) row[1];
            Map<String, Object> resultEntry = new HashMap<>();
            resultEntry.put("audit", widget);
            resultEntry.put("revInfo", revInfo);
            result.add(resultEntry);
        }
        return result;
    }
}
