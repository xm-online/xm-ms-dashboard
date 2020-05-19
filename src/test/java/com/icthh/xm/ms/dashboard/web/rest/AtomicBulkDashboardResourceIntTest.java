package com.icthh.xm.ms.dashboard.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icthh.xm.commons.i18n.error.web.ExceptionTranslator;
import com.icthh.xm.ms.dashboard.DashboardApp;
import com.icthh.xm.ms.dashboard.config.SecurityBeanOverrideConfiguration;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Stream;

import static com.icthh.xm.ms.dashboard.util.FileUtils.readAsString;
import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@WithMockUser(authorities = "SUPER-ADMIN")
@SpringBootTest(classes = {DashboardApp.class, SecurityBeanOverrideConfiguration.class})
public class AtomicBulkDashboardResourceIntTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc httpMock;
    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private ExceptionTranslator exceptionTranslator;
    @Autowired
    private BulkDashboardResource dashboardResource;
    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;
    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Before
    public void setup() {
        initMocks(this);
        httpMock = standaloneSetup(dashboardResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter)
            .build();
    }

    @Test
    @SneakyThrows
    @Transactional
    public void shouldAtomicCreateDashboards() {
        httpMock.perform(post("/api/dashboards/bulk")
            .contentType(APPLICATION_JSON)
            .content(readAsString("bulkAtomicCreateDashboards.json")))
            .andExpect(status().isOk());

        assertTrue(dashboardRepository.findAll().stream()
            .anyMatch(dashboard -> "Bulk atomic crate first".equalsIgnoreCase(dashboard.getName()))
        );

        assertTrue(dashboardRepository.findAll().stream()
            .anyMatch(dashboard -> "Bulk atomic crate second".equalsIgnoreCase(dashboard.getName()))
        );

        dashboardRepository.findAll()
            .forEach(dashboard -> assertFalse(dashboard.getWidgets().isEmpty()));
    }

    @Test
    @SneakyThrows
    public void shouldAtomicUpdateDashboards() {

        Dashboard dashboardToUpdate = dashboardRepository
            .save(mapper.readValue(readAsString("dashboardEntity.json"), Dashboard.class));

        List<DashboardDto> dashboardsWithNewValues = mapper.readValue(
            readAsString("bulkAtomicUpdateDashboards.json"),
            new TypeReference<List<DashboardDto>>() {
            }
        ).stream()
            .peek(dashboardDto -> dashboardDto.setId(dashboardToUpdate.getId()))
            .collect(toList());

        httpMock.perform(put("/api/dashboards/bulk")
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(dashboardsWithNewValues)))
            .andExpect(status().isOk());

        assertTrue(dashboardRepository.findAll().stream()
            .anyMatch(dashboard -> "Updated name first".equalsIgnoreCase(dashboard.getName()))
        );
    }

    @Test
    @SneakyThrows
    public void shouldAtomicDeleteDashboards() {

        final Dashboard dashboardToDelete =
            dashboardRepository.save(mapper.readValue(readAsString("dashboardEntity.json"), Dashboard.class));

        List<DashboardDto> dashboardDtos = mapper.readValue(
            readAsString("bulkAtomicDeleteDashboards.json"), new TypeReference<List<DashboardDto>>() {
            }).stream()
            .peek(dashboardDto -> dashboardDto.setId(dashboardToDelete.getId()))
            .collect(toList());

        httpMock.perform(delete("/api/dashboards/bulk")
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(dashboardDtos)))
            .andExpect(status().isOk());

        assertNulls(dashboardToDelete.getId());
    }

    @Test
    @SneakyThrows
    public void shouldFailAtomicCreateDashboards() {
        httpMock.perform(post("/api/dashboards/bulk")
            .contentType(APPLICATION_JSON)
            .content(readAsString("failBulkAtomicCreateDashboards.json")))
            .andExpect(status().isInternalServerError());
    }

    void assertNulls(Long... ids) {
        Stream.of(ids)
            .forEach(id -> assertNull(dashboardRepository.findOneById(id)));
    }
}
