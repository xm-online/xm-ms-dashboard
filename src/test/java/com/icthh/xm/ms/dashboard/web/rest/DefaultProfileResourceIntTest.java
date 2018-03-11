package com.icthh.xm.ms.dashboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.icthh.xm.commons.exceptions.spring.web.ExceptionTranslator;
import com.icthh.xm.ms.dashboard.DashboardApp;
import com.icthh.xm.ms.dashboard.config.SecurityBeanOverrideConfiguration;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.DefaultProfile;
import com.icthh.xm.ms.dashboard.service.DefaultProfileService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.persistence.EntityManager;

/**
 * Test class for the DefaultProfileResource REST controller.
 *
 * @see DefaultProfileResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DashboardApp.class, SecurityBeanOverrideConfiguration.class})
@WithMockUser(authorities = "SUPER-ADMIN")
public class DefaultProfileResourceIntTest {

    private static final String DEFAULT_ROLE_KEY = "AAAAAAAAAA";
    private static final String UPDATED_ROLE_KEY = "BBBBBBBBBB";

    @Autowired
    private DefaultProfileResource defaultProfileResource;

    @Autowired
    private DefaultProfileService defaultProfileService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDefaultProfileMockMvc;

    private DefaultProfile defaultProfile;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DefaultProfileResource resource = new DefaultProfileResource(defaultProfileService, defaultProfileResource);
        this.restDefaultProfileMockMvc = MockMvcBuilders.standaloneSetup(resource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DefaultProfile createEntity(EntityManager em) {
        DefaultProfile defaultProfile = new DefaultProfile()
            .roleKey(DEFAULT_ROLE_KEY);
        // Add required entity
        Dashboard dashboard = DashboardResourceIntTest.createEntity(em);
        em.persist(dashboard);
        em.flush();
        defaultProfile.setDashboard(dashboard);
        return defaultProfile;
    }

    @Before
    public void initTest() {
        defaultProfile = createEntity(em);
    }

    @Test
    @Transactional
    public void createDefaultProfile() throws Exception {
        int databaseSizeBeforeCreate = defaultProfileService.findAll(null).size();

        // Create the DefaultProfile
        restDefaultProfileMockMvc.perform(post("/api/default-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(defaultProfile)))
            .andExpect(status().isCreated());

        // Validate the DefaultProfile in the database
        List<DefaultProfile> defaultProfileList = defaultProfileService.findAll(null);
        assertThat(defaultProfileList).hasSize(databaseSizeBeforeCreate + 1);
        DefaultProfile testDefaultProfile = defaultProfileList.get(defaultProfileList.size() - 1);
        assertThat(testDefaultProfile.getRoleKey()).isEqualTo(DEFAULT_ROLE_KEY);
    }

    @Test
    @Transactional
    public void createDefaultProfileWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = defaultProfileService.findAll(null).size();

        // Create the DefaultProfile with an existing ID
        defaultProfile.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDefaultProfileMockMvc.perform(post("/api/default-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(defaultProfile)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<DefaultProfile> defaultProfileList = defaultProfileService.findAll(null);
        assertThat(defaultProfileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkRoleKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = defaultProfileService.findAll(null).size();
        // set the field null
        defaultProfile.setRoleKey(null);

        // Create the DefaultProfile, which fails.

        restDefaultProfileMockMvc.perform(post("/api/default-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(defaultProfile)))
            .andExpect(status().isBadRequest());

        List<DefaultProfile> defaultProfileList = defaultProfileService.findAll(null);
        assertThat(defaultProfileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDefaultProfiles() throws Exception {
        // Initialize the database
        defaultProfileService.saveAndFlush(defaultProfile);

        // Get all the defaultProfileList
        restDefaultProfileMockMvc.perform(get("/api/default-profiles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(defaultProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].roleKey").value(hasItem(DEFAULT_ROLE_KEY.toString())));
    }

    @Test
    @Transactional
    public void getDefaultProfile() throws Exception {
        // Initialize the database
        defaultProfileService.saveAndFlush(defaultProfile);

        // Get the defaultProfile
        restDefaultProfileMockMvc.perform(get("/api/default-profiles/{id}", defaultProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(defaultProfile.getId().intValue()))
            .andExpect(jsonPath("$.roleKey").value(DEFAULT_ROLE_KEY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDefaultProfile() throws Exception {
        // Get the defaultProfile
        restDefaultProfileMockMvc.perform(get("/api/default-profiles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getDefaultProfileByRoleKey() throws Exception {
        String randRoleKey = RandomStringUtils.randomAlphabetic(5);
        int databaseSizeBeforeCreate = defaultProfileService.findAllByRoleKey(randRoleKey).size();

        defaultProfile.setRoleKey(randRoleKey);
        defaultProfileService.saveAndFlush(defaultProfile);

        // Get the defaultProfile by roleKey
        List<DefaultProfile> defaultProfiles = defaultProfileService.findAllByRoleKey(randRoleKey);
        assertThat(defaultProfiles).hasSize(databaseSizeBeforeCreate + 1);
        DefaultProfile testDefaultProfile = defaultProfiles.get(defaultProfiles.size() - 1);
        assertThat(testDefaultProfile.getRoleKey()).isEqualTo(randRoleKey);
    }

    @Test
    @Transactional
    public void updateDefaultProfile() throws Exception {
        // Initialize the database
        defaultProfileService.save(defaultProfile);

        int databaseSizeBeforeUpdate = defaultProfileService.findAll(null).size();

        // Update the defaultProfile
        DefaultProfile updatedDefaultProfile = defaultProfileService.findOne(defaultProfile.getId());
        updatedDefaultProfile
            .roleKey(UPDATED_ROLE_KEY);

        restDefaultProfileMockMvc.perform(put("/api/default-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDefaultProfile)))
            .andExpect(status().isOk());

        // Validate the DefaultProfile in the database
        List<DefaultProfile> defaultProfileList = defaultProfileService.findAll(null);
        assertThat(defaultProfileList).hasSize(databaseSizeBeforeUpdate);
        DefaultProfile testDefaultProfile = defaultProfileList.get(defaultProfileList.size() - 1);
        assertThat(testDefaultProfile.getRoleKey()).isEqualTo(UPDATED_ROLE_KEY);
    }

    @Test
    @Transactional
    public void updateNonExistingDefaultProfile() throws Exception {
        int databaseSizeBeforeUpdate = defaultProfileService.findAll(null).size();

        // Create the DefaultProfile

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDefaultProfileMockMvc.perform(put("/api/default-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(defaultProfile)))
            .andExpect(status().isCreated());

        // Validate the DefaultProfile in the database
        List<DefaultProfile> defaultProfileList = defaultProfileService.findAll(null);
        assertThat(defaultProfileList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteDefaultProfile() throws Exception {
        // Initialize the database
        defaultProfileService.save(defaultProfile);

        int databaseSizeBeforeDelete = defaultProfileService.findAll(null).size();

        // Get the defaultProfile
        restDefaultProfileMockMvc.perform(delete("/api/default-profiles/{id}", defaultProfile.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<DefaultProfile> defaultProfileList = defaultProfileService.findAll(null);
        assertThat(defaultProfileList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DefaultProfile.class);
        DefaultProfile defaultProfile1 = new DefaultProfile();
        defaultProfile1.setId(1L);
        DefaultProfile defaultProfile2 = new DefaultProfile();
        defaultProfile2.setId(defaultProfile1.getId());
        assertThat(defaultProfile1).isEqualTo(defaultProfile2);
        defaultProfile2.setId(2L);
        assertThat(defaultProfile1).isNotEqualTo(defaultProfile2);
        defaultProfile1.setId(null);
        assertThat(defaultProfile1).isNotEqualTo(defaultProfile2);
    }
}
