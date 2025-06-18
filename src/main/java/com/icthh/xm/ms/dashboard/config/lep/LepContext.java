package com.icthh.xm.ms.dashboard.config.lep;

import com.icthh.xm.commons.lep.api.BaseLepContext;
import com.icthh.xm.ms.dashboard.service.DashboardService;
import com.icthh.xm.ms.dashboard.service.ProfileService;
import com.icthh.xm.ms.dashboard.service.UiDataService;
import com.icthh.xm.ms.dashboard.service.WidgetService;

public class LepContext extends BaseLepContext {

    public LepServices services;

    public static class LepServices {
        public DashboardService dashboardService;
        public WidgetService widgetService;
        public UiDataService uiDataService;
        public ProfileService profileService;
    }

}

