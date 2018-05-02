package com.icthh.xm.ms.dashboard.web.rest.vm;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.Widget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardVM {

    private Long id;

    private String name;

    private String owner;

    private String typeKey;

    private Boolean isPublic;

    private Map<String, Object> layout = new HashMap<>();

    private Map<String, Object> config = new HashMap<>();

    private Set<Widget> widgets = new HashSet<>();

    public DashboardVM (Dashboard dashboard) {
        super();
        this.id = dashboard.getId();
        this.name = dashboard.getName();
        this.owner = dashboard.getOwner();
        this.typeKey = dashboard.getTypeKey();
        this.isPublic = dashboard.isIsPublic();
        this.layout = dashboard.getLayout() == null ? this.layout : dashboard.getLayout();
        this.config = dashboard.getConfig() == null ? this.config : dashboard.getConfig();
        this.widgets = dashboard.getWidgets() == null ? this.widgets : dashboard.getWidgets();
    }

}
