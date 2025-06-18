package com.icthh.xm.ms.dashboard.util;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.DefaultProfile;
import com.icthh.xm.ms.dashboard.domain.Profile;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the ServiceUtil.
 *
 * @see ServiceUtil
 */
public class ServiceUtilUnitTest {

    @Test
    public void retrieveDashboardsFromProfilesTest() {
        int dashboardsAmount = 5;

        Set<Dashboard> generatedDashboards = new HashSet<>();
        for (int i = 1; i <= dashboardsAmount; i++) {
            generatedDashboards.add(buildDashboard());
        }
        Profile profile = new Profile().dashboards(generatedDashboards);

        List<Dashboard> dashboards = ServiceUtil.retrieveDashboardsFromProfiles(Collections.singletonList(profile));
        assertTrue(dashboardsAmount == dashboards.size());
    }

    @Test
    public void retrieveDashboardsFromDefProfilesTest() {
        int dashboardsAmount = 5;

        List<DefaultProfile> defaultProfiles = new ArrayList<>();
        for (int i = 1; i <= dashboardsAmount; i++) {
            defaultProfiles.add(new DefaultProfile().dashboard(buildDashboard()));
        }

        List<Dashboard> dashboards = ServiceUtil.retrieveDashboardsFromDefProfiles(defaultProfiles);
        assertTrue(dashboardsAmount == dashboards.size());
    }

    private Dashboard buildDashboard() {
        return new Dashboard().name(RandomStringUtils.randomAlphabetic(5));
    }
}
