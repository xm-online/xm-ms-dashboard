package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.commons.permission.annotation.FindWithPermission;
import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.ms.dashboard.domain.Profile;
import com.icthh.xm.ms.dashboard.repository.ProfilePermittedRepository;
import com.icthh.xm.ms.dashboard.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing Profile.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfilePermittedRepository profilePermittedRepository;

    /**
     * Save a profile.
     *
     * @param profile the entity to save
     * @return the persisted entity
     */
    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    /**
     * Save and flush a profile.
     *
     * @param profile the entity to save
     * @return the persisted entity
     */
    public Profile saveAndFlush(Profile profile) {
        return profileRepository.saveAndFlush(profile);
    }

    /**
     *  Get all the profiles.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    @FindWithPermission("PROFILE.GET_LIST")
    @PrivilegeDescription("Privilege to get all the profiles")
    public List<Profile> findAll(String privilegeKey) {
        return profilePermittedRepository.findAllWithEagerRelationships(privilegeKey);
    }

    /**
     *  Get one profile by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Profile findOne(Long id) {
        return profileRepository.findOneWithEagerRelationships(id);
    }

    /**
     *  Delete the  profile by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        profileRepository.deleteById(id);
    }

    /**
     *  Get all profiles by user key.
     *
     *  @param userKey users key
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Profile> findAllByUserKey(String userKey) {
        return profileRepository.findByUserKey(userKey);
    }
}
