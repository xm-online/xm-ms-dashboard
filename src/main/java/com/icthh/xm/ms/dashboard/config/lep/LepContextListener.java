package com.icthh.xm.ms.dashboard.config.lep;

import com.icthh.xm.commons.lep.api.BaseLepContext;
import com.icthh.xm.commons.lep.api.LepContextFactory;
import com.icthh.xm.lep.api.LepMethod;
import com.icthh.xm.ms.dashboard.service.DashboardService;
import com.icthh.xm.ms.dashboard.service.ProfileService;
import com.icthh.xm.ms.dashboard.service.UiDataService;
import com.icthh.xm.ms.dashboard.service.WidgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LepContextListener implements LepContextFactory {

    private final DashboardService dashboardService;
    private final WidgetService widgetService;
    private final UiDataService uiDataService;
    private final ProfileService profileService;

    @Override
    public BaseLepContext buildLepContext(LepMethod lepMethod) {
        LepContext lepContext = new LepContext();

        lepContext.services = buildLepContextServices();

        return lepContext;
    }

    private LepContext.LepServices buildLepContextServices() {
        LepContext.LepServices lepServices = new LepContext.LepServices();

        lepServices.dashboardService = dashboardService;
        lepServices.widgetService = widgetService;
        lepServices.uiDataService = uiDataService;
        lepServices.profileService = profileService;

        return lepServices;
    }
}
