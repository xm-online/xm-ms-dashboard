package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import java.util.List;

public interface DashboardPermittedRepository {

     List<Dashboard> findAll(String privilegeKey);
}
