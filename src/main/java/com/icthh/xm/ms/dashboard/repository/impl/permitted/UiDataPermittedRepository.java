package com.icthh.xm.ms.dashboard.repository.impl.permitted;

import com.icthh.xm.commons.permission.repository.PermittedRepository;
import com.icthh.xm.commons.permission.service.PermissionCheckService;
import com.icthh.xm.ms.dashboard.domain.UiData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UiDataPermittedRepository extends PermittedRepository {

    public static final String OWNER_CONDITION = "owner = :owner";
    public static final String TYPE_KEY_CONDITION = "typeKey = :typeKey";
    public static final String KEY_CONDITION = "key = :key";

    public UiDataPermittedRepository(PermissionCheckService permissionCheckService) {
        super(permissionCheckService);
    }

    public Page<UiData> findAllByTypeKeyAndOwner(String typeKey, String key, String owner, Pageable pageable, String privilegeKey) {

        Map<String, Object> conditionParams = new HashMap<>();
        List<String> conditions = new ArrayList<>();
        if (StringUtils.isNotBlank(typeKey)) {
            conditions.add(TYPE_KEY_CONDITION);
            conditionParams.put("typeKey", typeKey);
        }
        if (StringUtils.isNotBlank(owner)) {
            conditions.add(OWNER_CONDITION);
            conditionParams.put("owner", owner);
        }
        if (StringUtils.isNotBlank(key)) {
            conditions.add(KEY_CONDITION);
            conditionParams.put("key", key);
        }
        String whereCondition = CollectionUtils.isNotEmpty(conditions) ? String.join(" AND ", conditions) : " id is not null ";
        return findByCondition(whereCondition, conditionParams, pageable, UiData.class, privilegeKey);
    }
}
