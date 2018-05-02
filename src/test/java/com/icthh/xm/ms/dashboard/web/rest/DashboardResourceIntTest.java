package com.icthh.xm.ms.dashboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.icthh.xm.commons.exceptions.spring.web.ExceptionTranslator;
import com.icthh.xm.ms.dashboard.web.rest.vm.WidgetVM;
import org.assertj.core.api.Condition;
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

import com.google.common.collect.ImmutableMap;
import com.icthh.xm.ms.dashboard.DashboardApp;
import com.icthh.xm.ms.dashboard.config.SecurityBeanOverrideConfiguration;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.service.DashboardService;
import com.icthh.xm.ms.dashboard.service.WidgetService;

/**
 * Test class for the DashboardResource REST controller.
 *
 * @see DashboardResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DashboardApp.class, SecurityBeanOverrideConfiguration.class})
@WithMockUser(authorities = "SUPER-ADMIN")
public class DashboardResourceIntTest {

    private static final String DEFAULT_SELECTOR = "AAAAAAAAAA";
    private static final String UPDATED_SELECTOR = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER = "AAAAAAAAAA";
    private static final String UPDATED_OWNER = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE_KEY = "AAAAAAAAAA";
    private static final String UPDATED_TYPE_KEY = "BBBBBBBBBB";

    private static final Map<String, Object> DEFAULT_LAYOUT = ImmutableMap.<String, Object>builder()
        .put("AAAAAAAAAA", "BBBBBBBBBB").build();
    private static final Map<String, Object> UPDATED_LAYOUT = ImmutableMap.<String, Object>builder()
        .put("AAAAAAAAAA", "CCCCCCCCCC").build();

    private static final Map<String, Object> DEFAULT_CONFIG = ImmutableMap.<String, Object>builder()
        .put("AAAAAAAAAA", "BBBBBBBBBB").build();
    private static final Map<String, Object> UPDATED_CONFIG = ImmutableMap.<String, Object>builder()
        .put("AAAAAAAAAA", "CCCCCCCCCC").build();

    private static final Boolean DEFAULT_IS_PUBLIC = false;
    private static final Boolean UPDATED_IS_PUBLIC = true;

    @Autowired
    private DashboardResource dashboardResource;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restDashboardMockMvc;

    private Dashboard dashboard;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DashboardResource dashboardResourceMock = new DashboardResource(dashboardService,
                        widgetService, dashboardResource);
        this.restDashboardMockMvc = MockMvcBuilders.standaloneSetup(dashboardResourceMock)
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
    public static Dashboard createDashboard() {
        return new Dashboard()
            .name(DEFAULT_NAME)
            .owner(DEFAULT_OWNER)
            .layout(DEFAULT_LAYOUT)
            .config(DEFAULT_CONFIG)
            .isPublic(DEFAULT_IS_PUBLIC)
            .typeKey(DEFAULT_TYPE_KEY);
    }

    public static Widget createWidget() {
        return new Widget()
            .name(DEFAULT_NAME)
            .selector(DEFAULT_SELECTOR)
            .config(DEFAULT_CONFIG)
            .isPublic(DEFAULT_IS_PUBLIC);
    }

    @Before
    public void initTest() {
        dashboard = createDashboard();
    }

    @Test
    @Transactional
    public void testDashboardCreated() throws Exception {
        int dashboardListInitial = dashboardService.findAll(null).size();

        // Create the Dashboard
        restDashboardMockMvc.perform(post("/api/dashboards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dashboard)))
            .andExpect(status().isCreated());

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardService.findAll(null);
        assertThat(dashboardList).hasSize(dashboardListInitial + 1);

        Dashboard testDashboard = dashboardList.get(dashboardList.size() - 1);
        assertThat(testDashboard.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDashboard.getOwner()).isEqualTo(DEFAULT_OWNER);
        assertThat(testDashboard.getLayout()).isEqualTo(DEFAULT_LAYOUT);
        assertThat(testDashboard.getConfig()).isEqualTo(DEFAULT_CONFIG);
        assertThat(testDashboard.isIsPublic()).isEqualTo(DEFAULT_IS_PUBLIC);
        assertThat(testDashboard.getTypeKey()).isEqualTo(DEFAULT_TYPE_KEY);
    }

    @Test
    @Transactional
    public void testDashboardCreatedWithWidgets() throws Exception {
        int dashboardListInitial = dashboardService.findAll(null).size();
        int widgetListInitial = widgetService.findAll(null).size();

        Widget widget = createWidget();
        dashboard.widgets(Collections.singleton(widget));
        // Create the Dashboard
        restDashboardMockMvc.perform(post("/api/dashboards")
                                         .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                         .content(TestUtil.convertObjectToJsonBytes(dashboard)))
                            .andExpect(status().isCreated());

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardService.findAll(null);
        assertThat(dashboardList).hasSize(dashboardListInitial + 1);
        List<WidgetVM> widgetList = widgetService.findAll(null);
        assertThat(widgetList).hasSize(widgetListInitial + 1);

        Dashboard testDashboard = dashboardList.get(dashboardList.size() - 1);
        Widget testWidget = testDashboard.getWidgets().iterator().next();
        assertThat(testWidget.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWidget.getSelector()).isEqualTo(DEFAULT_SELECTOR);
        assertThat(testWidget.getConfig()).isEqualTo(DEFAULT_CONFIG);
        assertThat(testWidget.isIsPublic()).isEqualTo(DEFAULT_IS_PUBLIC);

        // Get the dashboard with widgets
        restDashboardMockMvc.perform(get("/api/dashboards/{id}", testDashboard.getId()))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                            .andDo(print())
                            .andExpect(jsonPath("$.id").value(testDashboard.getId()))
                            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
                            .andExpect(jsonPath("$.owner").value(DEFAULT_OWNER))
                            .andExpect(jsonPath("$.typeKey").value(DEFAULT_TYPE_KEY))
                            .andExpect(jsonPath("$.layout.AAAAAAAAAA").value("BBBBBBBBBB"))
                            .andExpect(jsonPath("$.config.AAAAAAAAAA").value("BBBBBBBBBB"))
                            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC))
                            .andExpect(jsonPath("$.widgets.[*].id").value(hasItem(testWidget.getId().intValue())))
                            .andExpect(jsonPath("$.widgets.[*].selector").value(hasItem(DEFAULT_SELECTOR)))
                            .andExpect(jsonPath("$.widgets.[*].name").value(hasItem(DEFAULT_NAME)))
                            .andExpect(jsonPath("$.widgets.[*].config").value(hasItem(DEFAULT_CONFIG)))
                            .andExpect(jsonPath("$.widgets.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC)))
                            .andExpect(jsonPath("$.widgets.[*].dashboard").value(hasItem(testDashboard.getId().intValue())));
    }

    @Test
    @Transactional
    public void createDashboardWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = dashboardService.findAll(null).size();

        // Create the Dashboard with an existing ID
        dashboard.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDashboardMockMvc.perform(post("/api/dashboards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dashboard)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Dashboard> dashboardList = dashboardService.findAll(null);
        assertThat(dashboardList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dashboardService.findAll(null).size();
        // set the field null
        dashboard.setName(null);

        // Create the Dashboard, which fails.

        restDashboardMockMvc.perform(post("/api/dashboards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dashboard)))
            .andExpect(status().isBadRequest());

        List<Dashboard> dashboardList = dashboardService.findAll(null);
        assertThat(dashboardList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOwnerIsRequired() throws Exception {
        int databaseSizeBeforeTest = dashboardService.findAll(null).size();
        // set the field null
        dashboard.setOwner(null);

        // Create the Dashboard, which fails.

        restDashboardMockMvc.perform(post("/api/dashboards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dashboard)))
            .andExpect(status().isBadRequest());

        List<Dashboard> dashboardList = dashboardService.findAll(null);
        assertThat(dashboardList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = dashboardService.findAll(null).size();
        // set the field null
        dashboard.setTypeKey(null);

        // Create the Dashboard, which fails.

        restDashboardMockMvc.perform(post("/api/dashboards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dashboard)))
            .andExpect(status().isBadRequest());

        List<Dashboard> dashboardList = dashboardService.findAll(null);
        assertThat(dashboardList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDashboards() throws Exception {
        // Initialize the database
        dashboardService.saveAndFlush(dashboard);


        // Get all the dashboardList
        restDashboardMockMvc.perform(get("/api/dashboards?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dashboard.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].owner").value(hasItem(DEFAULT_OWNER)))
            .andExpect(jsonPath("$.[*].layout").value(hasItem(DEFAULT_LAYOUT)))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG)))
            .andExpect(jsonPath("$.[*].typeKey").value(hasItem(DEFAULT_TYPE_KEY)))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC)));
    }

    @Test
    @Transactional
    public void getWidgetsByDashboard() throws Exception {
        // Initialize the database
        dashboardService.saveAndFlush(dashboard);

        Widget widget = new Widget()
            .selector(DEFAULT_SELECTOR)
            .name(DEFAULT_NAME)
            .config(DEFAULT_CONFIG)
            .isPublic(DEFAULT_IS_PUBLIC)
            .dashboard(dashboard);
        widgetService.saveAndFlush(widget);


        // Get the widgetList
        restDashboardMockMvc.perform(get("/api/dashboards/{id}/widgets", dashboard.getId()))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(widget.getId().intValue())))
            .andExpect(jsonPath("$.[*].selector").value(hasItem(DEFAULT_SELECTOR)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG)))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC)))
            .andExpect(jsonPath("$.[*].dashboard").value(hasItem(dashboard.getId().intValue())));
    }

    @Test
    @Transactional
    public void getDashboard() throws Exception {
        // Initialize the database
        dashboardService.saveAndFlush(dashboard);

        // Get the dashboard
        restDashboardMockMvc.perform(get("/api/dashboards/{id}", dashboard.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(dashboard.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.owner").value(DEFAULT_OWNER))
            .andExpect(jsonPath("$.typeKey").value(DEFAULT_TYPE_KEY))
            .andExpect(jsonPath("$.layout.AAAAAAAAAA").value("BBBBBBBBBB"))
            .andExpect(jsonPath("$.config.AAAAAAAAAA").value("BBBBBBBBBB"))
            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC));
    }

    @Test
    @Transactional
    public void getNonExistingDashboard() throws Exception {
        // Get the dashboard
        restDashboardMockMvc.perform(get("/api/dashboards/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDashboard() throws Exception {
        // Initialize the database
        dashboardService.save(dashboard);

        int databaseSizeBeforeUpdate = dashboardService.findAll(null).size();

        // Update the dashboard
        Dashboard updatedDashboard = dashboardService.findOne(dashboard.getId());
        updatedDashboard
            .name(UPDATED_NAME)
            .owner(UPDATED_OWNER)
            .layout(UPDATED_LAYOUT)
            .config(UPDATED_CONFIG)
            .isPublic(UPDATED_IS_PUBLIC)
            .typeKey(UPDATED_TYPE_KEY);

        restDashboardMockMvc.perform(put("/api/dashboards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDashboard)))
            .andExpect(status().isOk());

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardService.findAll(null);
        assertThat(dashboardList).hasSize(databaseSizeBeforeUpdate);
        Dashboard testDashboard = dashboardList.get(dashboardList.size() - 1);
        assertThat(testDashboard.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDashboard.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testDashboard.getLayout()).isEqualTo(UPDATED_LAYOUT);
        assertThat(testDashboard.getConfig()).isEqualTo(UPDATED_CONFIG);
        assertThat(testDashboard.isIsPublic()).isEqualTo(UPDATED_IS_PUBLIC);
        assertThat(testDashboard.getTypeKey()).isEqualTo(UPDATED_TYPE_KEY);
    }

    @Test
    @Transactional
    public void updateNonExistingDashboard() throws Exception {
        int databaseSizeBeforeUpdate = dashboardService.findAll(null).size();

        // Create the Dashboard

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDashboardMockMvc.perform(put("/api/dashboards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dashboard)))
            .andExpect(status().isCreated());

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardService.findAll(null);
        assertThat(dashboardList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteDashboard() throws Exception {
        // Initialize the database
        dashboardService.save(dashboard);

        int databaseSizeBeforeDelete = dashboardService.findAll(null).size();

        // Get the dashboard
        restDashboardMockMvc.perform(delete("/api/dashboards/{id}", dashboard.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Dashboard> dashboardList = dashboardService.findAll(null);
        assertThat(dashboardList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dashboard.class);
        Dashboard dashboard1 = new Dashboard();
        dashboard1.setId(1L);
        Dashboard dashboard2 = new Dashboard();
        dashboard2.setId(dashboard1.getId());
        assertThat(dashboard1).isEqualTo(dashboard2);
        dashboard2.setId(2L);
        assertThat(dashboard1).isNotEqualTo(dashboard2);
        dashboard1.setId(null);
        assertThat(dashboard1).isNotEqualTo(dashboard2);
    }
}
