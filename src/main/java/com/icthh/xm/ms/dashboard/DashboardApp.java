package com.icthh.xm.ms.dashboard;

import com.icthh.xm.commons.logging.util.MdcUtils;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.commons.tenant.TenantKey;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import tech.jhipster.config.DefaultProfileUtil;
import tech.jhipster.config.JHipsterConstants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication(scanBasePackages = "com.icthh.xm")
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class DashboardApp {

    private static final Logger log = LoggerFactory.getLogger(DashboardApp.class);

    private final Environment env;

    private final TenantContextHolder tenantContextHolder;

    public DashboardApp(Environment env, TenantContextHolder tenantContextHolder) {
        this.env = env;
        this.tenantContextHolder = tenantContextHolder;
    }

    /**
     * Initializes dashboard.
     *
     * <p>Spring profiles can be configured with a program arguments
     * --spring.profiles.active=your-active-profile
     *
     * <p>You can find more information on how profiles work with JHipster
     * on <a href="http://jhipster.github.io/profiles/">http://jhipster.github.io/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles
            .contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run "
                + "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles
            .contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not"
                + "run with both the 'dev' and 'cloud' profiles at the same time.");
        }

        initContexts();
    }

    private void initContexts() {
        // init tenant context, by default this is XM super tenant
        TenantContextUtils.setTenant(tenantContextHolder, TenantKey.SUPER);

        // init logger MDC context
        MdcUtils.putRid(MdcUtils.getRid() + "::" + TenantKey.SUPER.getValue());
    }

    @PreDestroy
    public void destroyApplication() {
        log.info("\n----------------------------------------------------------\n\t"
                + "Application {} is closing"
                + "\n----------------------------------------------------------",
            env.getProperty("spring.application.name"));
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     * @throws UnknownHostException if the local host name could not be resolved into an address
     */
    public static void main(String[] args) throws UnknownHostException {

        MdcUtils.putRid();

        SpringApplication app = new SpringApplication(DashboardApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        log.info("\n----------------------------------------------------------\n\t"
                + "Application '{}' is running! Access URLs:\n\t"
                + "Local: \t\t{}://localhost:{}\n\t"
                + "External: \t{}://{}:{}\n\t"
                + "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            env.getProperty("server.port"),
            protocol,
            InetAddress.getLocalHost().getHostAddress(),
            env.getProperty("server.port"),
            env.getActiveProfiles());

        String configServerStatus = env.getProperty("configserver.status");
        log.info("\n----------------------------------------------------------\n\t"
                + "Config Server: \t{}\n----------------------------------------------------------",
            configServerStatus == null ? "Not found or not setup for this application" : configServerStatus);
    }
}
