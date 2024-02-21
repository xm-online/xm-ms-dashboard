package com.icthh.xm.ms.dashboard.web.rest;

import com.google.common.collect.ImmutableMap;
import com.icthh.xm.commons.i18n.error.web.ExceptionTranslator;
import com.icthh.xm.ms.dashboard.DashboardApp;
import com.icthh.xm.ms.dashboard.config.SecurityBeanOverrideConfiguration;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.service.DashboardService;
import com.icthh.xm.ms.dashboard.service.ImportDashboardService;
import com.icthh.xm.ms.dashboard.service.WidgetService;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import com.icthh.xm.ms.dashboard.service.dto.WidgetDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DashboardApp.class, SecurityBeanOverrideConfiguration.class})
@WithMockUser(authorities = "SUPER-ADMIN")
@TestPropertySource(properties = {
    "application.storage.audit-support=true",
    "application.storage.store-configuration-enabled=false"
})
public class AuditIntTest {

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
    private ImportDashboardService importDashboardService;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private DashboardMapper dashboardMapper;

    @Autowired
    private WidgetResource widgetResource;

    private MockMvc restDashboardMockMvc;
    private MockMvc restWidgetMockMvc;

    private Dashboard dashboard;
    private Widget widget;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DashboardResource dashboardResourceMock = new DashboardResource(dashboardService,
            widgetService, dashboardMapper, dashboardResource, importDashboardService);
        this.restDashboardMockMvc = MockMvcBuilders.standaloneSetup(dashboardResourceMock)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
        WidgetResource widgetResourceMock = new WidgetResource(widgetService, widgetResource);
        this.restWidgetMockMvc = MockMvcBuilders.standaloneSetup(widgetResourceMock)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }
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
        widget = createWidget();
    }
    @Test
    public void testAuditDashboard() throws Exception {

        // Check audit table is empty
        restDashboardMockMvc.perform(get("/api/dashboards-audit")
                .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value(emptyIterable()));

        // Initialize the database
        dashboardService.save(dashboard);

        // Checking audit table: create dashboard
        restDashboardMockMvc.perform(get("/api/dashboards-audit")
                .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value(not(emptyIterable())))
            .andExpect(jsonPath("$.content").value(iterableWithSize(1)))
            .andExpect(jsonPath("$.content[0].audit.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.content[0].audit.owner").value(DEFAULT_OWNER))
            .andExpect(jsonPath("$.content[0].audit.typeKey").value(DEFAULT_TYPE_KEY))
            .andExpect(jsonPath("$.content[0].audit.layout.AAAAAAAAAA").value(DEFAULT_LAYOUT.get("AAAAAAAAAA")))
            .andExpect(jsonPath("$.content[0].audit.config.AAAAAAAAAA").value(DEFAULT_CONFIG.get("AAAAAAAAAA")))
            .andExpect(jsonPath("$.content[0].audit.isPublic").value(DEFAULT_IS_PUBLIC))
            .andExpect(jsonPath("$.content[0].operation").value("ADD"));

        // Update the dashboard
        DashboardDto updatedDashboard = dashboardService.findOne(dashboard.getId());

        updatedDashboard.setName(UPDATED_NAME);
        updatedDashboard.setOwner(UPDATED_OWNER);
        updatedDashboard.setLayout(UPDATED_LAYOUT);
        updatedDashboard.setConfig(UPDATED_CONFIG);
        updatedDashboard.setIsPublic(UPDATED_IS_PUBLIC);
        updatedDashboard.setTypeKey(UPDATED_TYPE_KEY);
        updatedDashboard.setWidgets(null);

        restDashboardMockMvc.perform(put("/api/dashboards")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDashboard)))
            .andExpect(status().isOk());

        //Checking audit table: update dashboard
        restDashboardMockMvc.perform(get("/api/dashboards-audit?sort=rev,asc")
                .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value(not(emptyIterable())))
            .andExpect(jsonPath("$.content").value(iterableWithSize(2)))
            .andExpect(jsonPath("$.content[1].audit.name").value(UPDATED_NAME))
            .andExpect(jsonPath("$.content[1].audit.owner").value(UPDATED_OWNER))
            .andExpect(jsonPath("$.content[1].audit.typeKey").value(UPDATED_TYPE_KEY))
            .andExpect(jsonPath("$.content[1].audit.layout.AAAAAAAAAA").value(UPDATED_LAYOUT.get("AAAAAAAAAA")))
            .andExpect(jsonPath("$.content[1].audit.config.AAAAAAAAAA").value(UPDATED_CONFIG.get("AAAAAAAAAA")))
            .andExpect(jsonPath("$.content[1].audit.isPublic").value(UPDATED_IS_PUBLIC))
            .andExpect(jsonPath("$.content[1].operation").value("MOD"));

        restDashboardMockMvc.perform(delete("/api/dashboards/{id}", dashboard.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        //Checking audit table: delete dashboard
        restDashboardMockMvc.perform(get("/api/dashboards-audit?sort=revtstmp,asc")
                .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andDo(h -> System.out.println(h.getResponse().getContentAsString()))
            .andExpect(jsonPath("$.content").value(not(emptyIterable())))
            .andExpect(jsonPath("$.content").value(iterableWithSize(3)))
            .andExpect(jsonPath("$.content[2].audit.name").value(nullValue()))
            .andExpect(jsonPath("$.content[2].audit.owner").value(nullValue()))
            .andExpect(jsonPath("$.content[2].audit.typeKey").value(nullValue()))
            .andExpect(jsonPath("$.content[2].audit.layout.size()").value(is(0)))
            .andExpect(jsonPath("$.content[2].audit.config.size()").value(is(0)))
            .andExpect(jsonPath("$.content[2].audit.isPublic").value(nullValue()))
            .andExpect(jsonPath("$.content[2].operation").value("DEL"));
    }

    @Test
    public void testAuditWidget() throws Exception {

        // Check audit table is empty
        restWidgetMockMvc.perform(get("/api/widgets-audit")
                .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value(emptyIterable()));

        // Initialize the database
        widgetService.save(widget);

        //Checking audit table: create widget
        restWidgetMockMvc.perform(get("/api/widgets-audit")
                .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value(iterableWithSize(1)))
            .andExpect(jsonPath("$.content[0].audit.selector").value(DEFAULT_SELECTOR))
            .andExpect(jsonPath("$.content[0].audit.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.content[0].audit.config.AAAAAAAAAA").value(DEFAULT_CONFIG.get("AAAAAAAAAA")))
            .andExpect(jsonPath("$.content[0].audit.isPublic").value(DEFAULT_IS_PUBLIC))
            .andExpect(jsonPath("$.content[0].operation").value("ADD"));

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
        System.out.println(widgetService.findOne(widget.getId()));

        //Checking audit table: update widget
        restWidgetMockMvc.perform(get("/api/widgets-audit")
                .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value(not(emptyIterable())))
            .andExpect(jsonPath("$.content").value(iterableWithSize(2)))
            .andExpect(jsonPath("$.content[1].audit.selector").value(UPDATED_SELECTOR))
            .andExpect(jsonPath("$.content[1].audit.name").value(UPDATED_NAME))
            .andExpect(jsonPath("$.content[1].audit.config.AAAAAAAAAA").value(UPDATED_CONFIG.get("AAAAAAAAAA")))
            .andExpect(jsonPath("$.content[1].audit.isPublic").value(UPDATED_IS_PUBLIC))
            .andExpect(jsonPath("$.content[1].operation").value("MOD"));

        restWidgetMockMvc.perform(delete("/api/widgets/{id}", widget.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        //Checking audit table: delete widget
        restWidgetMockMvc.perform(get("/api/widgets-audit")
                .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value(not(emptyIterable())))
            .andExpect(jsonPath("$.content").value(iterableWithSize(3)))
            .andExpect(jsonPath("$.content[2].audit.selector").value(nullValue()))
            .andExpect(jsonPath("$.content[2].audit.name").value(nullValue()))
            .andExpect(jsonPath("$.content[2].audit.config.size()").value(is(0)))
            .andExpect(jsonPath("$.content[2].audit.isPublic").value(nullValue()))
            .andExpect(jsonPath("$.content[2].operation").value("DEL"));
    }
}
