package com.icthh.xm.ms.dashboard.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.ms.dashboard.domain.Profile;
import com.icthh.xm.ms.dashboard.service.ProfileService;
import com.icthh.xm.ms.dashboard.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

/**
 * REST controller for managing Profile.
 *
 * TODO add get profile by user_key from token, to retrieve dashboards for current user.
 */
@RestController
@RequestMapping("/api")
public class ProfileResource {

    private static final String ENTITY_NAME = "profile";

    private final ProfileService profileService;

    private final ProfileResource profileResource;

    public ProfileResource(
                    ProfileService profileService,
                    @Lazy ProfileResource profileResource) {
        this.profileService = profileService;
        this.profileResource = profileResource;
    }

    /**
     * POST  /profiles : Create a new profile.
     *
     * @param profile the profile to create
     * @return the ResponseEntity with status 201 (Created) and with body the new profile, or with status 400 (Bad Request) if the profile has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/profiles")
    @Timed
    @PreAuthorize("hasPermission({'profile': #profile}, 'PROFILE.CREATE')")
    @PrivilegeDescription("Privilege to create a new profile")
    public ResponseEntity<Profile> createProfile(@Valid @RequestBody Profile profile) throws URISyntaxException {
        if (profile.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new profile cannot already have an ID")).body(null);
        }
        Profile result = profileService.save(profile);
        return ResponseEntity.created(new URI("/api/profiles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /profiles : Updates an existing profile.
     *
     * @param profile the profile to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated profile,
     * or with status 400 (Bad Request) if the profile is not valid,
     * or with status 500 (Internal Server Error) if the profile couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/profiles")
    @Timed
    @PreAuthorize("hasPermission({'id': #profile.id, 'newProfile': #profile}, 'profile', 'PROFILE.UPDATE')")
    @PrivilegeDescription("Privilege to updates an existing profile")
    public ResponseEntity<Profile> updateProfile(@Valid @RequestBody Profile profile) throws URISyntaxException {
        if (profile.getId() == null) {
            //in order to call method with permissions check
            return this.profileResource.createProfile(profile);
        }
        Profile result = profileService.save(profile);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, profile.getId().toString()))
            .body(result);
    }

    /**
     * GET  /profiles : get all the profiles.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of profiles in body
     */
    @GetMapping("/profiles")
    @Timed
    public List<Profile> getAllProfiles() {
        return profileService.findAll(null);
    }

    /**
     * GET  /profiles/:id : get the "id" profile.
     *
     * @param id the id of the profile to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the profile, or with status 404 (Not Found)
     */
    @GetMapping("/profiles/{id}")
    @Timed
    @PostAuthorize("hasPermission({'returnObject': returnObject.body}, 'PROFILE.GET_LIST.ITEM')")
    @PrivilegeDescription("Privilege to get the profile by id")
    public ResponseEntity<Profile> getProfile(@PathVariable Long id) {
        Profile profile = profileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(profile));
    }

    /**
     * DELETE  /profiles/:id : delete the "id" profile.
     *
     * @param id the id of the profile to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/profiles/{id}")
    @Timed
    @PreAuthorize("hasPermission({'id': #id}, 'profile', 'PROFILE.DELETE')")
    @PrivilegeDescription("Privilege to delete the profile by id")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        profileService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
