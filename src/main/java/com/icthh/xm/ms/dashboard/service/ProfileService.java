package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.ms.dashboard.domain.Profile;
import com.icthh.xm.ms.dashboard.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing Profile.
 */
@Service
@Transactional
public class ProfileService {

    private final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Save a profile.
     *
     * @param profile the entity to save
     * @return the persisted entity
     */
    public Profile save(Profile profile) {
        log.debug("Request to save Profile : {}", profile);
        return profileRepository.save(profile);
    }

    /**
     * Save and flush a profile.
     *
     * @param profile the entity to save
     * @return the persisted entity
     */
    public Profile saveAndFlush(Profile profile) {
        log.debug("Request to save Profile : {}", profile);
        return profileRepository.saveAndFlush(profile);
    }

    /**
     *  Get all the profiles.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Profile> findAll() {
        log.debug("Request to get all Profiles");
        return profileRepository.findAllWithEagerRelationships();
    }

    /**
     *  Get one profile by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Profile findOne(Long id) {
        log.debug("Request to get Profile : {}", id);
        return profileRepository.findOneWithEagerRelationships(id);
    }

    /**
     *  Delete the  profile by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Profile : {}", id);
        profileRepository.delete(id);
    }

    /**
     *  Get all profiles by user key.
     *
     *  @param userKey users key
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Profile> findAllByUserKey(String userKey) {
        log.debug("Request to get all Profiles by userKey : {}", userKey);
        return profileRepository.findByUserKey(userKey);
    }
}
