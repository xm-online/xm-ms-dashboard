package com.icthh.xm.ms.dashboard.mapper;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;
import com.icthh.xm.ms.dashboard.service.dto.WidgetDto;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

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
        entity.setWidgets(toEntities(dto.getWidgets()));

        return entity;
    }

    public Dashboard toFullEntity(DashboardDto dto) {
        Dashboard entity = toEntity(dto);
        entity.setId(dto.getId());
        return entity;
    }

    public Set<Widget> toEntities(Set<WidgetDto> widgets) {
        return widgets.stream().map(this::toEntity).collect(toSet());
    }

    public Widget toEntity(WidgetDto dto) {
        Widget entity = new Widget();

        entity.setConfig(dto.getConfig());
        entity.setIsPublic(dto.getIsPublic());
        entity.setName(dto.getName());
        entity.setSelector(dto.getSelector());

        return entity;
    }

    public Dashboard merge(DashboardDto dto, Dashboard entity) {

        entity.setName(dto.getName());
        entity.setOwner(dto.getOwner());
        entity.setConfig(dto.getConfig());
        entity.setLayout(dto.getLayout());
        entity.setTypeKey(dto.getTypeKey());
        entity.setIsPublic(dto.getIsPublic());
        entity.setWidgets(toEntities(dto.getWidgets()));

        return entity;
    }

    public DashboardDto toDto(Dashboard entity) {
        DashboardDto dto = new DashboardDto();

        dto.setName(entity.getName());
        dto.setOwner(entity.getOwner());
        dto.setConfig(entity.getConfig());
        dto.setLayout(entity.getLayout());
        dto.setTypeKey(entity.getTypeKey());
        dto.setIsPublic(entity.isIsPublic());
        dto.setWidgets(toDtos(entity.getWidgets()));

        return dto;
    }

    public Set<WidgetDto> toDtos(Set<Widget> widgets) {
        return widgets.stream().map(this::toDto).collect(toSet());
    }

    public WidgetDto toDto(Widget entity) {
        WidgetDto dto = new WidgetDto();

        dto.setConfig(entity.getConfig());
        dto.setIsPublic(entity.isIsPublic());
        dto.setName(entity.getName());
        dto.setSelector(entity.getSelector());

        return dto;
    }
}
