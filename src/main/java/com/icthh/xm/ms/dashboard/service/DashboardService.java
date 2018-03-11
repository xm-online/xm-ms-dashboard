package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.commons.permission.annotation.FindWithPermission;
import com.icthh.xm.commons.permission.repository.PermittedRepository;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing Dashboard.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final PermittedRepository permittedRepository;

    /**
     * Save a dashboard.
     *
     * @param dashboard the entity to save
     * @return the persisted entity
     */
    public Dashboard save(Dashboard dashboard) {
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
     *  Get one dashboard by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Dashboard findOne(Long id) {
        return dashboardRepository.findOne(id);
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
