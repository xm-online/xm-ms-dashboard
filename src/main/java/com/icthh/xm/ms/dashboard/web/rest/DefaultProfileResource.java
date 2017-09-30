package com.icthh.xm.ms.dashboard.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.ms.dashboard.domain.DefaultProfile;
import com.icthh.xm.ms.dashboard.service.DefaultProfileService;
import com.icthh.xm.ms.dashboard.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing DefaultProfile.
 */
@RestController
@RequestMapping("/api")
public class DefaultProfileResource {

    private final Logger log = LoggerFactory.getLogger(DefaultProfileResource.class);

    private static final String ENTITY_NAME = "defaultProfile";

    private final DefaultProfileService defaultProfileService;

    public DefaultProfileResource(DefaultProfileService defaultProfileService) {
        this.defaultProfileService = defaultProfileService;
    }

    /**
     * POST  /default-profiles : Create a new defaultProfile.
     *
     * @param defaultProfile the defaultProfile to create
     * @return the ResponseEntity with status 201 (Created) and with body the new defaultProfile, or with status 400 (Bad Request) if the defaultProfile has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/default-profiles")
    @Timed
    public ResponseEntity<DefaultProfile> createDefaultProfile(@Valid @RequestBody DefaultProfile defaultProfile) throws URISyntaxException {
        log.debug("REST request to save DefaultProfile : {}", defaultProfile);
        if (defaultProfile.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new defaultProfile cannot already have an ID")).body(null);
        }
        DefaultProfile result = defaultProfileService.save(defaultProfile);
        return ResponseEntity.created(new URI("/api/default-profiles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /default-profiles : Updates an existing defaultProfile.
     *
     * @param defaultProfile the defaultProfile to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated defaultProfile,
     * or with status 400 (Bad Request) if the defaultProfile is not valid,
     * or with status 500 (Internal Server Error) if the defaultProfile couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/default-profiles")
    @Timed
    public ResponseEntity<DefaultProfile> updateDefaultProfile(@Valid @RequestBody DefaultProfile defaultProfile) throws URISyntaxException {
        log.debug("REST request to update DefaultProfile : {}", defaultProfile);
        if (defaultProfile.getId() == null) {
            return createDefaultProfile(defaultProfile);
        }
        DefaultProfile result = defaultProfileService.save(defaultProfile);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, defaultProfile.getId().toString()))
            .body(result);
    }

    /**
     * GET  /default-profiles : get all the defaultProfiles.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of defaultProfiles in body
     */
    @GetMapping("/default-profiles")
    @Timed
    public List<DefaultProfile> getAllDefaultProfiles() {
        log.debug("REST request to get all DefaultProfiles");
        return defaultProfileService.findAll();
    }

    /**
     * GET  /default-profiles/:id : get the "id" defaultProfile.
     *
     * @param id the id of the defaultProfile to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the defaultProfile, or with status 404 (Not Found)
     */
    @GetMapping("/default-profiles/{id}")
    @Timed
    public ResponseEntity<DefaultProfile> getDefaultProfile(@PathVariable Long id) {
        log.debug("REST request to get DefaultProfile : {}", id);
        DefaultProfile defaultProfile = defaultProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(defaultProfile));
    }

    /**
     * DELETE  /default-profiles/:id : delete the "id" defaultProfile.
     *
     * @param id the id of the defaultProfile to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/default-profiles/{id}")
    @Timed
    public ResponseEntity<Void> deleteDefaultProfile(@PathVariable Long id) {
        log.debug("REST request to delete DefaultProfile : {}", id);
        defaultProfileService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
