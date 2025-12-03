package com.icthh.xm.ms.dashboard.config.lep;

import com.icthh.xm.commons.config.client.service.TenantConfigService;
import com.icthh.xm.commons.lep.api.BaseLepContext;
import com.icthh.xm.commons.lep.api.LepContextFactory;
import com.icthh.xm.commons.permission.service.PermissionCheckService;
import com.icthh.xm.lep.api.LepMethod;
import com.icthh.xm.ms.dashboard.service.DashboardService;
import com.icthh.xm.ms.dashboard.service.ProfileService;
import com.icthh.xm.ms.dashboard.service.UiDataService;
import com.icthh.xm.ms.dashboard.service.WidgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class LepContextListener implements LepContextFactory {

    private final DashboardService dashboardService;
    private final WidgetService widgetService;
    private final UiDataService uiDataService;
    private final ProfileService profileService;
    private final PermissionCheckService permissionCheckService;
    private final RestTemplate loadBalancedRestTemplate;
    private final RestTemplate plainRestTemplate;
    private final TenantConfigService tenantConfigService;

    @Override
    public BaseLepContext buildLepContext(LepMethod lepMethod) {
        LepContext lepContext = new LepContext();

        lepContext.services = buildLepContextServices();
        lepContext.templates = buildLepContextTemplates();

        return lepContext;
    }

    private LepContext.LepServices buildLepContextServices() {
        LepContext.LepServices lepServices = new LepContext.LepServices();

        lepServices.dashboardService = dashboardService;
        lepServices.widgetService = widgetService;
        lepServices.uiDataService = uiDataService;
        lepServices.profileService = profileService;
        lepServices.permissionService = permissionCheckService;
        lepServices.tenantConfigService = tenantConfigService;

        return lepServices;
    }

    private LepContext.LepTemplates buildLepContextTemplates() {
        LepContext.LepTemplates lepTemplates = new LepContext.LepTemplates();

        lepTemplates.rest = loadBalancedRestTemplate;
        lepTemplates.plainRest = plainRestTemplate;

        return lepTemplates;
    }
}
