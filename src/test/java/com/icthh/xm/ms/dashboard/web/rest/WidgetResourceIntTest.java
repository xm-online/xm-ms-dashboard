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

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.icthh.xm.commons.exceptions.spring.web.ExceptionTranslator;
import com.icthh.xm.ms.dashboard.service.dto.WidgetDto;
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
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.service.WidgetService;

/**
 * Test class for the WidgetResource REST controller.
 *
 * @see WidgetResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DashboardApp.class, SecurityBeanOverrideConfiguration.class})
@WithMockUser(authorities = "SUPER-ADMIN")
public class WidgetResourceIntTest {

    private static final String DEFAULT_SELECTOR = "AAAAAAAAAA";
    private static final String UPDATED_SELECTOR = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Map<String, Object> DEFAULT_CONFIG = ImmutableMap.<String, Object>builder()
        .put("AAAAAAAAAA", "BBBBBBBBBB").build();
    private static final Map<String, Object> UPDATED_CONFIG = ImmutableMap.<String, Object>builder()
        .put("AAAAAAAAAA", "CCCCCCCCCC").build();

    private static final Boolean DEFAULT_IS_PUBLIC = false;
    private static final Boolean UPDATED_IS_PUBLIC = true;

    @Autowired
    private WidgetResource widgetResource;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restWidgetMockMvc;

    private Widget widget;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WidgetResource widgetResourceMock = new WidgetResource(widgetService, widgetResource);
        this.restWidgetMockMvc = MockMvcBuilders.standaloneSetup(widgetResourceMock)
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
    public static Widget createEntity(EntityManager em) {
        Widget widget = new Widget()
            .selector(DEFAULT_SELECTOR)
            .name(DEFAULT_NAME)
            .config(DEFAULT_CONFIG)
            .isPublic(DEFAULT_IS_PUBLIC);
        return widget;
    }

    @Before
    public void initTest() {
        widget = createEntity(em);
    }

    @Test
    @Transactional
    public void createWidget() throws Exception {
        int databaseSizeBeforeCreate = widgetService.findAll(null).size();

        // Create the Widget
        restWidgetMockMvc.perform(post("/api/widgets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(widget)))
            .andExpect(status().isCreated());

        // Validate the Widget in the database
        List<WidgetDto> widgetList = widgetService.findAll(null);
        assertThat(widgetList).hasSize(databaseSizeBeforeCreate + 1);
        WidgetDto testWidget = widgetList.get(widgetList.size() - 1);
        assertThat(testWidget.getSelector()).isEqualTo(DEFAULT_SELECTOR);
        assertThat(testWidget.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWidget.getConfig()).isEqualTo(DEFAULT_CONFIG);
        assertThat(testWidget.getIsPublic()).isEqualTo(DEFAULT_IS_PUBLIC);
    }

    @Test
    @Transactional
    public void createWidgetWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = widgetService.findAll(null).size();

        // Create the Widget with an existing ID
        widget.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWidgetMockMvc.perform(post("/api/widgets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(widget)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<WidgetDto> widgetList = widgetService.findAll(null);
        assertThat(widgetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkSelectorIsRequired() throws Exception {
        int databaseSizeBeforeTest = widgetService.findAll(null).size();
        // set the field null
        widget.setSelector(null);

        // Create the Widget, which fails.

        restWidgetMockMvc.perform(post("/api/widgets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(widget)))
            .andExpect(status().isBadRequest());

        List<WidgetDto> widgetList = widgetService.findAll(null);
        assertThat(widgetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = widgetService.findAll(null).size();
        // set the field null
        widget.setName(null);

        // Create the Widget, which fails.

        restWidgetMockMvc.perform(post("/api/widgets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(widget)))
            .andExpect(status().isBadRequest());

        List<WidgetDto> widgetList = widgetService.findAll(null);
        assertThat(widgetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWidgets() throws Exception {
        // Initialize the database
        widgetService.saveAndFlush(widget);

        // Get all the widgetList
        restWidgetMockMvc.perform(get("/api/widgets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(widget.getId().intValue())))
            .andExpect(jsonPath("$.[*].selector").value(hasItem(DEFAULT_SELECTOR.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG)))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC.booleanValue())));
    }

    @Test
    @Transactional
    public void getWidget() throws Exception {
        // Initialize the database
        widgetService.saveAndFlush(widget);

        // Get the widget
        restWidgetMockMvc.perform(get("/api/widgets/{id}", widget.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(widget.getId().intValue()))
            .andExpect(jsonPath("$.selector").value(DEFAULT_SELECTOR.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.config.AAAAAAAAAA").value("BBBBBBBBBB"))
            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingWidget() throws Exception {
        // Get the widget
        restWidgetMockMvc.perform(get("/api/widgets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWidget() throws Exception {
        // Initialize the database
        widgetService.save(widget);

        int databaseSizeBeforeUpdate = widgetService.findAll(null).size();

        // Update the widget
        WidgetDto updatedWidget = widgetService.findOne(widget.getId());
        updatedWidget.setSelector(UPDATED_SELECTOR);
        updatedWidget.setName(UPDATED_NAME);
        updatedWidget.setConfig(UPDATED_CONFIG);
        updatedWidget.setIsPublic(UPDATED_IS_PUBLIC);

        restWidgetMockMvc.perform(put("/api/widgets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWidget)))
            .andExpect(status().isOk());

        // Validate the Widget in the database
        List<WidgetDto> widgetList = widgetService.findAll(null);
        assertThat(widgetList).hasSize(databaseSizeBeforeUpdate);
        WidgetDto testWidget = widgetList.get(widgetList.size() - 1);
        assertThat(testWidget.getSelector()).isEqualTo(UPDATED_SELECTOR);
        assertThat(testWidget.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWidget.getConfig()).isEqualTo(UPDATED_CONFIG);
        assertThat(testWidget.getIsPublic()).isEqualTo(UPDATED_IS_PUBLIC);
    }

    @Test
    @Transactional
    public void updateNonExistingWidget() throws Exception {
        int databaseSizeBeforeUpdate = widgetService.findAll(null).size();

        // Create the Widget

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restWidgetMockMvc.perform(put("/api/widgets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(widget)))
            .andExpect(status().isCreated());

        // Validate the Widget in the database
        List<WidgetDto> widgetList = widgetService.findAll(null);
        assertThat(widgetList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteWidget() throws Exception {
        // Initialize the database
        widgetService.save(widget);

        int databaseSizeBeforeDelete = widgetService.findAll(null).size();

        // Get the widget
        restWidgetMockMvc.perform(delete("/api/widgets/{id}", widget.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<WidgetDto> widgetList = widgetService.findAll(null);
        assertThat(widgetList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Widget.class);
        Widget widget1 = new Widget();
        widget1.setId(1L);
        Widget widget2 = new Widget();
        widget2.setId(widget1.getId());
        assertThat(widget1).isEqualTo(widget2);
        widget2.setId(2L);
        assertThat(widget1).isNotEqualTo(widget2);
        widget1.setId(null);
        assertThat(widget1).isNotEqualTo(widget2);
    }
}
