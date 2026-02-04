package com.icthh.xm.ms.dashboard.domain;

import lombok.Data;

@Data
public class DashboardSpec {

    private DashboardStoreType dashboardStoreType;
    private Boolean overrideId = true;

    public enum DashboardStoreType {
        RDBMS, MSCONFG
    }

}
