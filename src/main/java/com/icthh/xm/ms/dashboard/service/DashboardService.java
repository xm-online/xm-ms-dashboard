package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.ms.dashboard.config.tenant.TenantContext;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.DefaultProfile;
import com.icthh.xm.ms.dashboard.domain.Profile;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.repository.DefaultProfileRepository;
import com.icthh.xm.ms.dashboard.repository.ProfileRepository;
import com.icthh.xm.ms.dashboard.util.ServiceUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing Dashboard.
 */
@Service
@Transactional
public class DashboardService {

    private final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final DashboardRepository dashboardRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private DefaultProfileRepository defaultProfileRepository;

    public DashboardService(
                    DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    /**
     * Save a dashboard.
     *
     * @param dashboard the entity to save
     * @return the persisted entity
     */
    public Dashboard save(Dashboard dashboard) {
        log.debug("Request to save Dashboard : {}", dashboard);
        return dashboardRepository.save(dashboard);
    }

    /**
     * Save and flush a dashboard.
     *
     * @param dashboard the entity to save
     * @return the persisted entity
     */
    public Dashboard saveAndFlush(Dashboard dashboard) {
        log.debug("Request to save and flush Dashboard : {}", dashboard);
        return dashboardRepository.saveAndFlush(dashboard);
    }

    /**
     *  Get all the dashboards.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Dashboard> findAll() {
        log.debug("Request to get all Dashboards");

        String userKey = TenantContext.getCurrent().getUserKey();
        List<Profile> profiles = profileRepository.findByUserKey(userKey);
        List<Dashboard> dashboards = ServiceUtil.retrieveDashboardsFromProfiles(profiles);

        if (CollectionUtils.isEmpty(dashboards)) {
            //todo in future releases: use findAllByDefaultProfilesRoleKey instead DefaultProfileRepository.findAll
            List<DefaultProfile> defaultProfiles = defaultProfileRepository.findAll();
            dashboards = ServiceUtil.retrieveDashboardsFromDefProfiles(defaultProfiles);
        }

        return CollectionUtils.isEmpty(dashboards) ? dashboardRepository.findAll() : dashboards;
    }

    /**
     *  Get one dashboard by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Dashboard findOne(Long id) {
        log.debug("Request to get Dashboard : {}", id);
        return dashboardRepository.findOne(id);
    }

    /**
     *  Delete the  dashboard by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Dashboard : {}", id);
        dashboardRepository.delete(id);
    }
}
