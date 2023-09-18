package com.icthh.xm.ms.dashboard.service;

import static com.icthh.xm.ms.dashboard.service.dto.DashboardDto.toWidgetsDto;

import com.icthh.xm.commons.permission.annotation.FindWithPermission;
import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.RevInfo;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.repository.DashboardPermittedRepository;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Dashboard.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final DashboardPermittedRepository permittedRepository;
    private final WidgetService widgetService;
    private final AuditReader auditReader;

    /**
     * Save a dashboard.
     *
     * @param dashboard the entity to save
     * @return the persisted entity
     */
    public Dashboard save(Dashboard dashboard) {
        dashboard.updateDashboardReference(dashboard.getWidgets(), Widget::setDashboard);
        return dashboardRepository.save(dashboard);
    }

    /**
     * Save and flush a dashboard.
     *
     * @param dashboard the entity to save
     * @return the persisted entity
     */
    public Dashboard saveAndFlush(Dashboard dashboard) {
        return dashboardRepository.saveAndFlush(dashboard);
    }

    /**
     *  Get all the dashboards.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    @FindWithPermission("DASHBOARD.GET_LIST")
    @PrivilegeDescription("Privilege to get all the dashboards")
    public List<Dashboard> findAll(String privilegeKey) {
        return permittedRepository.findAll(privilegeKey);
    }

    /**
     *  Get one dashboard by id. Returned widgets filtered by addition permission.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public DashboardDto findOne(Long id) {
        Dashboard dashboard = dashboardRepository.findOneById(id);

        return Optional.ofNullable(dashboard)
                       .map(DashboardDto::new)
                       .map(this::addWidgetsToDashboard)
                       .orElse(null);
    }

    private DashboardDto addWidgetsToDashboard(final DashboardDto dashboardDto) {
        Optional.ofNullable(widgetService.findByDashboardId(dashboardDto.getId(), null))
                .ifPresent(widgets -> dashboardDto.setWidgets(toWidgetsDto(widgets)));
        return dashboardDto;
    }

    /**
     *  Delete the  dashboard by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        dashboardRepository.deleteById(id);
    }


    public Page<Map<String, Object>> findAuditsById(Long id, int page, int size) {
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Dashboard.class,false, true)
            .setFirstResult(page * size)
            .setMaxResults(size)
            .add(AuditEntity.property("id").eq(id));
        return widgetService.getResult(auditQuery);
    }

    public Page<Map<String, Object>> findAllAudits(int page, int size) {
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Dashboard.class,false, true)
            .setFirstResult(page * size)
            .setMaxResults(size);
        return widgetService.getResult(auditQuery);
    }
}
