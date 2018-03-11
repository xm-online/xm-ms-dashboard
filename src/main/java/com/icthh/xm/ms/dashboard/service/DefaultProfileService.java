package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.commons.permission.annotation.FindWithPermission;
import com.icthh.xm.commons.permission.repository.PermittedRepository;
import com.icthh.xm.ms.dashboard.domain.DefaultProfile;
import com.icthh.xm.ms.dashboard.repository.DefaultProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing DefaultProfile.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DefaultProfileService {

    private final DefaultProfileRepository defaultProfileRepository;
    private final PermittedRepository permittedRepository;

    /**
     * Save a defaultProfile.
     *
     * @param defaultProfile the entity to save
     * @return the persisted entity
     */
    public DefaultProfile save(DefaultProfile defaultProfile) {
        return defaultProfileRepository.save(defaultProfile);
    }

    /**
     * Save and flush a defaultProfile.
     *
     * @param defaultProfile the entity to save
     * @return the persisted entity
     */
    public DefaultProfile saveAndFlush(DefaultProfile defaultProfile) {
        return defaultProfileRepository.saveAndFlush(defaultProfile);
    }

    /**
     *  Get all the defaultProfiles.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    @FindWithPermission("DEFAULT_PROFILE.GET_LIST")
    public List<DefaultProfile> findAll(String privilegeKey) {
        return permittedRepository.findAll(DefaultProfile.class, privilegeKey);
    }

    /**
     *  Get one defaultProfile by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public DefaultProfile findOne(Long id) {
        return defaultProfileRepository.findOne(id);
    }

    /**
     *  Delete the  defaultProfile by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
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
        return defaultProfileRepository.findByRoleKey(roleKey);
    }
}
