package com.icthh.xm.ms.dashboard.repository.impl.permitted;

import com.icthh.xm.commons.permission.repository.PermittedRepository;
import com.icthh.xm.commons.permission.service.PermissionCheckService;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.repository.DashboardPermittedRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultDashboardPermittedRepository extends PermittedRepository implements DashboardPermittedRepository {

    public DefaultDashboardPermittedRepository(PermissionCheckService permissionCheckService) {
        super(permissionCheckService);
    }

    @Override
    public List<Dashboard> findAll(String privilegeKey) {
        return super.findAll(Dashboard.class, privilegeKey);
    }
}
