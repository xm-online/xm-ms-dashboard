package com.icthh.xm.ms.dashboard.config.tenant;

import com.icthh.xm.commons.exceptions.BusinessException;
import com.icthh.xm.commons.migration.db.tenant.provisioner.TenantDatabaseProvisioner;
import com.icthh.xm.commons.tenantendpoint.TenantManager;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantAbilityCheckerProvisioner;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantListProvisioner;
import com.icthh.xm.ms.dashboard.service.tenant.TenantDefaultDashboardProvisioner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

@Slf4j
@org.springframework.context.annotation.Configuration
public class TenantManagerConfiguration {

    @Bean
    public TenantManager tenantManager(TenantAbilityCheckerProvisioner abilityCheckerProvisioner,
                                       TenantDatabaseProvisioner databaseProvisioner,
                                       TenantDefaultDashboardProvisioner dashboardProvisioner,
                                       TenantListProvisioner tenantListProvisioner) {

        TenantManager manager = TenantManager.builder()
                                             .service(abilityCheckerProvisioner)
                                             .service(tenantListProvisioner)
                                             .service(databaseProvisioner)
                                             .service(dashboardProvisioner)
                                             .exceptionHandler(defaultExceptionHandler())
                                             .build();
        log.info("Configured tenant manager: {}", manager);
        return manager;
    }

    // TODO remove after commons 2.0.17
    private static <E extends Exception> Consumer<E> defaultExceptionHandler() {
        return e -> {
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                throw new BusinessException(e.getMessage());
            }
        };
    }

}
