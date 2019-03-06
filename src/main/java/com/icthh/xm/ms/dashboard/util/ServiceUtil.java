package com.icthh.xm.ms.dashboard.util;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.DefaultProfile;
import com.icthh.xm.ms.dashboard.domain.Profile;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility for service operations.
 */
public final class ServiceUtil {

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
}
