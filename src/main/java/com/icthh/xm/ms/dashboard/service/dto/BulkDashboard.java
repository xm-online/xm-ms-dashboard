package com.icthh.xm.ms.dashboard.service.dto;

import java.util.Collection;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkDashboard {

    @NotEmpty
    private Collection<DashboardDto> dashboardItems;

}
