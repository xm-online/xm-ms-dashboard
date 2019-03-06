package com.icthh.xm.ms.dashboard.web.rest;

import com.icthh.xm.commons.config.client.repository.TenantListRepository;
import com.icthh.xm.commons.gen.api.TenantsApiDelegate;
import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.ms.dashboard.service.tenant.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantResource implements TenantsApiDelegate {

    private final TenantService service;

    private final TenantListRepository tenantListRepository;

    @Override
    @PreAuthorize("hasPermission({'tenant':#tenant}, 'DASHBOARD.TENANT.CREATE')")
    public ResponseEntity<Void> addTenant(Tenant tenant) {
        tenantListRepository.addTenant(tenant.getTenantKey().toLowerCase());
        service.createTenant(tenant);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasPermission({'tenantKey':#tenantKey}, 'DASHBOARD.TENANT.DELETE')")
    public ResponseEntity<Void> deleteTenant(String tenantKey) {
        service.deleteTenant(tenantKey);
        tenantListRepository.deleteTenant(tenantKey.toLowerCase());
        return ResponseEntity.ok().build();
    }

    @Override
    @PostAuthorize("hasPermission(null, 'DASHBOARD.TENANT.GET_LIST')")
    public ResponseEntity<List<Tenant>> getAllTenantInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    @PostAuthorize("hasPermission({'returnObject': returnObject.body}, 'DASHBOARD.TENANT.GET_LIST.ITEM')")
    public ResponseEntity<Tenant> getTenant(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    @PreAuthorize("hasPermission({'tenant':#tenant, 'status':#status}, 'DASHBOARD.TENANT.UPDATE')")
    public ResponseEntity<Void> manageTenant(String tenant, String state) {
        tenantListRepository.updateTenant(tenant.toLowerCase(), state.toUpperCase());
        return ResponseEntity.ok().build();
    }
}
