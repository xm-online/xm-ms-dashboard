package com.icthh.xm.ms.dashboard.config;

import com.icthh.xm.commons.migration.db.config.DatabaseConfiguration;
import com.icthh.xm.commons.migration.db.tenant.SchemaResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Configuration
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableJpaRepositories(value = {"com.icthh.xm.ms.dashboard.repository"})
public class XmDatabaseConfiguration extends DatabaseConfiguration {

    private static final String JPA_PACKAGES = "com.icthh.xm.ms.dashboard.domain";

    private final Environment env;

    public XmDatabaseConfiguration(Environment env,
                                   JpaProperties jpaProperties,
                                   SchemaResolver schemaResolver) {
        super(env, jpaProperties, schemaResolver);
        this.env = env;
    }

    @Override
    public String getJpaPackages() {
        return JPA_PACKAGES;
    }
}

