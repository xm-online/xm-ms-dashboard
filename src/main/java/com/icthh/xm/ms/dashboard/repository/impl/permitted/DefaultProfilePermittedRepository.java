package com.icthh.xm.ms.dashboard.repository.impl.permitted;

import com.icthh.xm.commons.permission.repository.PermittedRepository;
import com.icthh.xm.commons.permission.service.PermissionCheckService;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultProfilePermittedRepository extends PermittedRepository {
    public DefaultProfilePermittedRepository(PermissionCheckService permissionCheckService) {
        super(permissionCheckService);
    }
}
