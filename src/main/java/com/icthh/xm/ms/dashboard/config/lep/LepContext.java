package com.icthh.xm.ms.dashboard.config.lep;

import com.icthh.xm.commons.config.client.service.TenantConfigService;
import com.icthh.xm.commons.lep.api.BaseLepContext;
import com.icthh.xm.commons.permission.service.PermissionCheckService;
import com.icthh.xm.ms.dashboard.service.DashboardService;
import com.icthh.xm.ms.dashboard.service.ProfileService;
import com.icthh.xm.ms.dashboard.service.UiDataService;
import com.icthh.xm.ms.dashboard.service.WidgetService;
import org.springframework.web.client.RestTemplate;

public class LepContext extends BaseLepContext {

    public LepServices services;
    public LepTemplates templates;

    public static class LepServices {
        public DashboardService dashboardService;
        public WidgetService widgetService;
        public UiDataService uiDataService;
        public ProfileService profileService;
        public PermissionCheckService permissionService;
        public TenantConfigService tenantConfigService;
    }

    public static class LepTemplates {
        public RestTemplate rest;
        public RestTemplate plainRest;
    }

}

