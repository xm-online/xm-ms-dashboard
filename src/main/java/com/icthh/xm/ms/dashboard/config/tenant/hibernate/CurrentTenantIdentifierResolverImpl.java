package com.icthh.xm.ms.dashboard.config.tenant.hibernate;

import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    private final TenantContextHolder tenantContextHolder;

    @Override
    public String resolveCurrentTenantIdentifier() {
        return TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder);
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
