package com.icthh.xm.ms.dashboard.util;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.DefaultProfile;
import com.icthh.xm.ms.dashboard.domain.Profile;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility for service operations.
 */
public final class ServiceUtil {

    public static final String REVISION_PREFIX = "originalId.revision.";
    public static final String REVISION_TIMESTAMP = "revtstmp";
    public static final String REVISION_ID = "rev";

    private ServiceUtil() {
        throw new AssertionError();
    }

    public static List<Dashboard> retrieveDashboardsFromProfiles(List<Profile> profiles) {
        return new ArrayList<>(profiles.stream()
                        .filter(profile -> CollectionUtils.isNotEmpty(profile.getDashboards()))
                        .map(Profile::getDashboards).reduce(new HashSet<>(), (list, item) -> {
                            list.addAll(item);
                            return list;
                        }));
    }

    public static List<Dashboard> retrieveDashboardsFromDefProfiles(
                    List<DefaultProfile> defaultProfiles) {
        return defaultProfiles.stream().filter(profile -> profile.getDashboard() != null)
                        .map(DefaultProfile::getDashboard).collect(Collectors.toList());
    }

    public static Sort.Order getPageOrder(Pageable pageable) {
        Sort.Order order = pageable.getSortOr(Sort.by(REVISION_PREFIX + REVISION_TIMESTAMP).ascending()).stream().findFirst().get();
        String orderProperty = order.getProperty();
        if (REVISION_TIMESTAMP.equals(orderProperty) || REVISION_ID.equals(orderProperty)) {
            order = new Sort.Order(order.getDirection(), REVISION_PREFIX + orderProperty, order.getNullHandling());
        }
        return order;
    }

    public static Page<Map<String, Object>> getResult(List resultList, Pageable pageable, Long totalCount) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object entry : resultList) {
            Object[] row = (Object[]) entry;
            Map<String, Object> resultEntry = new HashMap<>();
            resultEntry.put("audit", row[0]);
            resultEntry.put("revInfo", row[1]);
            resultEntry.put("operation", row[2]);
            result.add(resultEntry);
        }
        return new PageImpl<>(result, pageable, totalCount);
    }

    public static Long getNotExistValueCompareSet(Set<Long> allValues, Long value) {
        if (CollectionUtils.isEmpty(allValues)) {
            return value;
        }

        Long candidate = value;

        while (allValues.contains(candidate)) {
            candidate++;
        }

        return candidate;
    }
}
