package com.icthh.xm.ms.dashboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.icthh.xm.commons.lep.api.LepManagementService;
import com.icthh.xm.commons.permission.service.PermissionService;
import com.icthh.xm.commons.permission.service.RoleService;
import com.icthh.xm.commons.security.XmAuthenticationConstants;
import com.icthh.xm.commons.security.internal.XmAuthenticationDetails;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.dashboard.AbstractSpringBootTest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

/**
 * Verifies that the audit history endpoints of {@link DashboardResource} and {@link WidgetResource}
 * require the {@code *.AUDIT.GET_LIST} / {@code *.AUDIT.GET_LIST.ITEM} privileges.
 */
@TestPropertySource(properties = {
    "application.storage.audit-support=true",
    "application.storage.store-configuration-enabled=false"
})
public class AuditResourcePermissionIntTest extends AbstractSpringBootTest {

    private static final String TENANT = "XM";
    private static final String TEST_ROLE = "AUDIT_PERM_TEST_ROLE";
    private static final String USER_KEY = "AUDIT_PERM_TEST_USER";
    private static final String ROLES_CONFIG_KEY = "/config/tenants/" + TENANT + "/roles.yml";
    private static final String PERMISSIONS_CONFIG_KEY = "/config/tenants/" + TENANT + "/permissions.yml";
    private static final Pageable PAGE = PageRequest.of(0, 10);

    private static final String ROLES_YML = TEST_ROLE + ":\n"
        + "  name:\n"
        + "    en: \"Audit permission test role\"\n"
        + "  description: \"role used only by AuditResourcePermissionIntTest\"\n";

    @Autowired
    private DashboardResource dashboardResource;

    @Autowired
    private WidgetResource widgetResource;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private TenantContextHolder tenantContextHolder;

    @Autowired
    private LepManagementService lepManagementService;

    @BeforeEach
    public void setup() {
        TenantContextUtils.setTenant(tenantContextHolder, TENANT);
        lepManagementService.beginThreadContext();
        roleService.onRefresh(ROLES_CONFIG_KEY, ROLES_YML);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(USER_KEY, "",
            List.of(new SimpleGrantedAuthority(TEST_ROLE)));
        token.setDetails(userDetails());
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
        roleService.onRefresh(ROLES_CONFIG_KEY, null);
        permissionService.onRefresh(PERMISSIONS_CONFIG_KEY, null);
        lepManagementService.endThreadContext();
    }

    @Test
    public void shouldDenyDashboardAuditsWithoutPrivilege() {
        pushPermissions();

        assertThatThrownBy(() -> dashboardResource.getAllDashboardsAudits(PAGE))
            .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> dashboardResource.getDashboardAuditById(1L, PAGE))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    public void shouldDenyWidgetAuditsWithoutPrivilege() {
        pushPermissions();

        assertThatThrownBy(() -> widgetResource.getAllWidgetAudits(PAGE))
            .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> widgetResource.getWidgetAuditById(1L, PAGE))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    public void shouldReturnDashboardAuditsWithPrivilege() {
        pushPermissions("DASHBOARD.AUDIT.GET_LIST", "DASHBOARD.AUDIT.GET_LIST.ITEM");

        assertThat(dashboardResource.getAllDashboardsAudits(PAGE).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(dashboardResource.getDashboardAuditById(1L, PAGE).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldReturnWidgetAuditsWithPrivilege() {
        pushPermissions("WIDGET.AUDIT.GET_LIST", "WIDGET.AUDIT.GET_LIST.ITEM");

        assertThat(widgetResource.getAllWidgetAudits(PAGE).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(widgetResource.getWidgetAuditById(1L, PAGE).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private void pushPermissions(String... privilegeKeys) {
        StringBuilder yml = new StringBuilder("dashboard:\n  " + TEST_ROLE + ":\n");
        for (String privilegeKey : privilegeKeys) {
            yml.append("  - privilegeKey: \"").append(privilegeKey).append("\"\n");
            yml.append("    disabled: false\n");
        }
        permissionService.onRefresh(PERMISSIONS_CONFIG_KEY, yml.toString());
    }

    private static XmAuthenticationDetails userDetails() {
        XmAuthenticationDetails details = mock(XmAuthenticationDetails.class);
        when(details.getDecodedDetails()).thenReturn(
            new HashMap<>(Map.of(XmAuthenticationConstants.AUTH_DETAILS_USER_KEY, USER_KEY)));
        return details;
    }
}
