package com.icthh.xm.ms.dashboard.web.rest.vm;

import com.icthh.xm.ms.dashboard.domain.Widget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WidgetVM {

    private Long id;

    private String selector;

    private String name;
    
    private Boolean isPublic;

    private Map<String, Object> config = new HashMap<>();

    public WidgetVM (Widget widget) {
        super();
        this.id = widget.getId();
        this.selector = widget.getSelector();
        this.name = widget.getName();
        this.isPublic = widget.isIsPublic();
        this.config = widget.getConfig();
    }

}
