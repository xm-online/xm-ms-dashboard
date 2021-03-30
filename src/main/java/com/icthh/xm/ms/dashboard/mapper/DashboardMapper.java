package com.icthh.xm.ms.dashboard.mapper;

import static java.util.stream.Collectors.toSet;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import com.icthh.xm.ms.dashboard.service.dto.WidgetDto;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class DashboardMapper {

    public Dashboard toEntity(DashboardDto dto) {
        Dashboard entity = new Dashboard();

        entity.setName(dto.getName());
        entity.setOwner(dto.getOwner());
        entity.setConfig(dto.getConfig());
        entity.setLayout(dto.getLayout());
        entity.setTypeKey(dto.getTypeKey());
        entity.setIsPublic(dto.getIsPublic());
        entity.setWidgets(toEntities(dto.getWidgets(), entity));

        return entity;
    }

    public Dashboard toFullEntity(DashboardDto dto) {
        Dashboard entity = toEntity(dto);
        entity.setId(dto.getId());
        entity.setWidgets(toFullEntities(dto.getWidgets(), entity));
        return entity;
    }

    public Set<Widget> toEntities(Set<WidgetDto> widgets, Dashboard dashboard) {
        return widgets.stream()
            .map(widget -> toEntity(widget, dashboard))
            .collect(toSet());
    }

    public Set<Widget> toFullEntities(Set<WidgetDto> widgets, Dashboard dashboard) {
        return widgets.stream()
            .map(widget -> toFullEntity(widget, dashboard))
            .collect(toSet());
    }

    public Widget toEntity(WidgetDto dto, Dashboard dashboard) {
        Widget entity = new Widget();

        entity.setConfig(dto.getConfig());
        entity.setIsPublic(dto.getIsPublic());
        entity.setName(dto.getName());
        entity.setSelector(dto.getSelector());
        entity.setDashboard(dashboard);

        return entity;
    }

    public Widget toFullEntity(WidgetDto dto, Dashboard dashboard) {
        Widget entity = toEntity(dto, dashboard);
        entity.setId(dto.getId());
        return entity;
    }

    public Dashboard merge(DashboardDto dto, Dashboard entity) {

        entity.setName(dto.getName());
        entity.setOwner(dto.getOwner());
        entity.setConfig(dto.getConfig());
        entity.setLayout(dto.getLayout());
        entity.setTypeKey(dto.getTypeKey());
        entity.setIsPublic(dto.getIsPublic());
        entity.setWidgets(toEntities(dto.getWidgets(), entity));

        return entity;
    }
}
