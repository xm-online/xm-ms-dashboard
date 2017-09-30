package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.ms.dashboard.domain.DefaultProfile;
import com.icthh.xm.ms.dashboard.repository.DefaultProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing DefaultProfile.
 */
@Service
@Transactional
public class DefaultProfileService {

    private final Logger log = LoggerFactory.getLogger(DefaultProfileService.class);

    private final DefaultProfileRepository defaultProfileRepository;

    public DefaultProfileService(DefaultProfileRepository defaultProfileRepository) {
        this.defaultProfileRepository = defaultProfileRepository;
    }

    /**
     * Save a defaultProfile.
     *
     * @param defaultProfile the entity to save
     * @return the persisted entity
     */
    public DefaultProfile save(DefaultProfile defaultProfile) {
        log.debug("Request to save DefaultProfile : {}", defaultProfile);
        return defaultProfileRepository.save(defaultProfile);
    }

    /**
     * Save and flush a defaultProfile.
     *
     * @param defaultProfile the entity to save
     * @return the persisted entity
     */
    public DefaultProfile saveAndFlush(DefaultProfile defaultProfile) {
        log.debug("Request to save DefaultProfile : {}", defaultProfile);
        return defaultProfileRepository.saveAndFlush(defaultProfile);
    }

    /**
     *  Get all the defaultProfiles.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<DefaultProfile> findAll() {
        log.debug("Request to get all DefaultProfiles");
        return defaultProfileRepository.findAll();
    }

    /**
     *  Get one defaultProfile by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public DefaultProfile findOne(Long id) {
        log.debug("Request to get DefaultProfile : {}", id);
        return defaultProfileRepository.findOne(id);
    }

    /**
     *  Delete the  defaultProfile by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete DefaultProfile : {}", id);
        defaultProfileRepository.delete(id);
    }

    /**
     *  Get one defaultProfile by roleKey.
     *
     *  @param roleKey the role key of the entity
     *  @return the list of default profiles
     */
    @Transactional(readOnly = true)
    public List<DefaultProfile> findAllByRoleKey(String roleKey) {
        log.debug("Request to get DefaultProfile by roleKey: {}", roleKey);
        return defaultProfileRepository.findByRoleKey(roleKey);
    }
}
