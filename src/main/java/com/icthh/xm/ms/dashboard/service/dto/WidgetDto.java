package com.icthh.xm.ms.dashboard.service.dto;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.Widget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WidgetDto {

    private Long id;

    private String selector;

    private String name;

    private Boolean isPublic;

    private Map<String, Object> config = new HashMap<>();

    private Long dashboard;

    public WidgetDto(Widget widget) {
        super();
        this.id = widget.getId();
        this.selector = widget.getSelector();
        this.name = widget.getName();
        this.isPublic = widget.isIsPublic();
        this.config = widget.getConfig();
        this.dashboard = Optional.ofNullable(widget.getDashboard()).map(Dashboard::getId).orElse(null);
    }

}
