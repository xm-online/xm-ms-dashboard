package com.icthh.xm.ms.dashboard.service;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;

import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.service.bulk.AtomicBulkDashboardService;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import com.icthh.xm.ms.dashboard.service.dto.ImportDashboardDto;
import com.icthh.xm.ms.dashboard.service.dto.WidgetDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.List;

public class ImportDashboardServiceUnitTest {

    @Mock
    AtomicBulkDashboardService atomicBulkDashboardService;
    @Mock
    DashboardRepository dashboardRepository;
    ImportDashboardService importDashboardService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        importDashboardService = new ImportDashboardService(atomicBulkDashboardService, dashboardRepository);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void importDashboards() {

        ImportDashboardDto imports = buildImportDashboards();

        importDashboardService.importDashboards(imports);

        verify(dashboardRepository).findAll();
        verify(dashboardRepository).deleteAll(anyCollection());

        ArgumentCaptor<Collection<DashboardDto>> captor = ArgumentCaptor.forClass(Collection.class);

        verify(atomicBulkDashboardService).create(captor.capture());

        Collection<DashboardDto> toCreate = captor.getValue();
        assertThat(toCreate, hasSize(2));
        assertThat(toCreate, containsInAnyOrder(
            allOf(
                hasProperty("id", nullValue()),
                hasProperty("name", is("Dashboard 2")),
                hasProperty("widgets", hasSize(1)),
                hasProperty("widgets",
                            contains(allOf(
                                hasProperty("id", nullValue()),
                                hasProperty("name", is("in dashboard 2")))
                            )
                )
            ),
            allOf(
                hasProperty("id", nullValue()),
                hasProperty("name", is("Dashboard 1")),
                hasProperty("widgets", hasSize(1)),
                hasProperty("widgets",
                            contains(allOf(
                                hasProperty("id", nullValue()),
                                hasProperty("name", is("in dashboard 1")))
                            )
                )
            )
        ));

    }

    private ImportDashboardDto buildImportDashboards() {
        ImportDashboardDto imports = new ImportDashboardDto();

        DashboardDto dashboardDto = new DashboardDto();
        dashboardDto.setId(1L);
        dashboardDto.setName("Dashboard 1");

        DashboardDto dashboardDto2 = new DashboardDto();
        dashboardDto2.setId(2L);
        dashboardDto2.setName("Dashboard 2");

        WidgetDto widgetDto = new WidgetDto();
        widgetDto.setName("in dashboard 1");
        widgetDto.setId(10L);
        widgetDto.setDashboard(1L);

        WidgetDto widgetDto2 = new WidgetDto();
        widgetDto2.setName("in dashboard 2");
        widgetDto2.setId(20L);
        widgetDto2.setDashboard(2L);

        WidgetDto widgetDto3 = new WidgetDto();
        widgetDto3.setName("not in any dashboard");
        widgetDto3.setId(30L);
        widgetDto3.setDashboard(3L);

        imports.getDashboards().addAll(List.of(dashboardDto, dashboardDto2));
        imports.getWidgets().addAll(List.of(widgetDto, widgetDto2, widgetDto3));
        return imports;
    }

}
