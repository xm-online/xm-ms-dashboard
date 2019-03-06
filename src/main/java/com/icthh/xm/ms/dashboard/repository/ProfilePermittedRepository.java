package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.commons.permission.repository.PermittedRepository;
import com.icthh.xm.commons.permission.service.PermissionCheckService;
import com.icthh.xm.ms.dashboard.domain.Profile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfilePermittedRepository extends PermittedRepository {

    private static final String SELECT_SQL = "select distinct returnObject from Profile returnObject left join fetch returnObject.dashboards";
    private static final String COUNT_SQL = "select distinct count(returnObject) from Profile returnObject";

    public ProfilePermittedRepository(PermissionCheckService permissionCheckService) {
        super(permissionCheckService);
    }

    /**
     * Find permitted profiles with dashboards.
     * @param privilegeKey the privilege key
     * @return permitted profiles
     */
    public List<Profile> findAllWithEagerRelationships(String privilegeKey) {
        String selectSql = SELECT_SQL;
        String countSql = COUNT_SQL;

        String permittedCondition = createPermissionCondition(privilegeKey);
        if (StringUtils.isNotBlank(permittedCondition)) {
            selectSql += WHERE_SQL + permittedCondition;
            countSql += WHERE_SQL + permittedCondition;
        }

        return execute(createCountQuery(countSql), null, createSelectQuery(selectSql, null, getType())).getContent();
    }

    private Class<Profile> getType() {
        return Profile.class;
    }
}
