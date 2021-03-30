package com.icthh.xm.ms.dashboard.repository.impl.permitted;

import com.icthh.xm.commons.permission.repository.PermittedRepository;
import com.icthh.xm.commons.permission.service.PermissionCheckService;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.repository.WidgetPermittedRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class DefaultWidgetPermittedRepository extends PermittedRepository implements WidgetPermittedRepository {

    public DefaultWidgetPermittedRepository(PermissionCheckService permissionCheckService) {
        super(permissionCheckService);
    }

    @Override
    public List<Widget> findAll(String privilegeKey) {
        return super.findAll(Widget.class, privilegeKey);
    }

    /**
     * Find permitted widgets by dashboard id.
     * @param id the dashboard id
     * @param privilegeKey the privilege key
     * @return permitted profiles
     */
    public List<Widget> findByDashboardId(Long id, String privilegeKey) {
        String whereCondition = "dashboard.id = :id";

        Map<String, Object> conditionParams = Collections.singletonMap("id", id);

        return findByCondition(whereCondition, conditionParams, getType(), privilegeKey);
    }

    private Class<Widget> getType() {
        return Widget.class;
    }

}
