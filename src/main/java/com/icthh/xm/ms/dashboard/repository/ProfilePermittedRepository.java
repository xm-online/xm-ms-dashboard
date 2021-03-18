package com.icthh.xm.ms.dashboard.repository;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import com.icthh.xm.commons.permission.repository.PermittedRepository;
import com.icthh.xm.commons.permission.service.PermissionCheckService;
import com.icthh.xm.ms.dashboard.domain.Profile;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
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

        Collection<String> permittedCondition = createPermissionCondition(privilegeKey);
        if (isNotEmpty(permittedCondition)) {
            String orChainedPermittedCondition = String.join(" OR ", permittedCondition);
            selectSql += WHERE_SQL + orChainedPermittedCondition;
            countSql += WHERE_SQL + orChainedPermittedCondition;
        }
        log.debug("Executing SQL '{}'", selectSql);
        return execute(createCountQuery(countSql), null, createSelectQuery(selectSql, null, getType())).getContent();
    }

    private Class<Profile> getType() {
        return Profile.class;
    }
}
