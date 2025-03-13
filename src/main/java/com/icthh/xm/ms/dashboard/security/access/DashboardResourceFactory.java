package com.icthh.xm.ms.dashboard.security.access;

import com.icthh.xm.commons.permission.access.ResourceFactory;
import com.icthh.xm.commons.permission.access.repository.ResourceRepository;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.repository.DefaultProfileRepository;
import com.icthh.xm.ms.dashboard.repository.ProfileRepository;
import com.icthh.xm.ms.dashboard.repository.WidgetRepository;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class DashboardResourceFactory implements ResourceFactory {

    private Map<String, ResourceRepository> repositories = new HashMap<>();

    private final DashboardRepository dashboardRepository;
    private final DefaultProfileRepository defaultProfileRepository;
    private final ProfileRepository profileRepository;
    private final WidgetRepository widgetRepository;

    public DashboardResourceFactory(
                    DashboardRepository dashboardRepository,
                    DefaultProfileRepository defaultProfileRepository,
                    ProfileRepository profileRepository,
                    WidgetRepository widgetRepository) {
        this.dashboardRepository = dashboardRepository;
        this.defaultProfileRepository = defaultProfileRepository;
        this.profileRepository = profileRepository;
        this.widgetRepository = widgetRepository;
    }

    @PostConstruct
    public void init() {
        repositories.put("dashboard", dashboardRepository);
        repositories.put("defaultProfile", defaultProfileRepository);
        repositories.put("profile", profileRepository);
        repositories.put("widget", widgetRepository);
    }

    @Override
    public Object getResource(Object resourceId, String objectType) {
        Object result = null;
        ResourceRepository resourceRepository = repositories.get(objectType);
        if (resourceRepository != null) {
            result = resourceRepository.findResourceById(resourceId);
        }
        return result;
    }
}
