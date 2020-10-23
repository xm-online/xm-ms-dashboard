package com.icthh.xm.ms.dashboard.service.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportDashboardDto {

    List<DashboardDto> dashboards = new ArrayList<>();
    List<WidgetDto> widgets = new ArrayList<>();

    @Override
    public String toString() {
        return "ImportDashboardDto{" +
               "dashboards.size=" + dashboards.size() +
               ", widgets.size=" + widgets.size() +
               '}';
    }
}
