package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.commons.permission.annotation.FindWithPermission;
import com.icthh.xm.commons.permission.repository.PermittedRepository;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing Dashboard.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final PermittedRepository permittedRepository;
    private final WidgetService widgetService;

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
    public List<Dashboard> findAll(String privilegeKey) {
        return permittedRepository.findAll(Dashboard.class, privilegeKey);
    }

    /**
     *  Get one dashboard by id. Returned widgets filtered by addition permission.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Dashboard findOne(Long id) {
        Dashboard dashboard = dashboardRepository.findOneById(id);

        Optional.ofNullable(dashboard)
                .map(d -> widgetService.findByDashboardId(d.getId(), null))
                .ifPresent(w -> w.forEach(dashboard::addWidgets));

        return dashboard;
    }

    /**
     *  Delete the  dashboard by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        dashboardRepository.delete(id);
    }
}
