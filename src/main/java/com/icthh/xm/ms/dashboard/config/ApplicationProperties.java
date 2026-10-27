package com.icthh.xm.ms.dashboard.config;

import com.icthh.xm.commons.config.XmCommonsApplicationProperties;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to JHipster.
 * <p> Properties are configured in the application.yml file. </p>
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Getter
@Setter
public class ApplicationProperties extends XmCommonsApplicationProperties {

    private final Amazon amazon = new Amazon();
    private final Retry retry = new Retry();

    private String xmEndpoint;
    private List<String> tenantIgnoredPathList = Collections.emptyList();
    private List<String> timelineIgnoredHttpMethods = Collections.emptyList();
    private boolean timelinesEnabled;
    private boolean kafkaEnabled;
    private String kafkaSystemQueue;
    private String dbSchemaSuffix;

    private String specificationPathPattern;
    private String specificationName;
    private final Storage storage = new Storage();

    private Integer cacheLimit;

    @Getter
    @Setter
    public static class Amazon {

        private final Aws aws = new Aws();
        private final S3 s3 = new S3();

        @Getter
        @Setter
        public static class Aws {

            private String region;
            private String accessKeyId;
            private String accessKeySecret;
        }

        @Getter
        @Setter
        public static class S3 {

            private String defaultBucket;
        }
    }

    @Getter
    @Setter
    private static class Retry {

        private int maxAttempts;
        private long delay;
        private int multiplier;
    }

    @Getter
    @Setter
    public static class Storage {
        private boolean storeConfigurationEnabled;
        private boolean auditSupport;
        private MsConfigStorageProperties msConfig;

        @Getter
        @Setter
        public static class MsConfigStorageProperties {
                private String tenantDashboardsFolderPathPattern;
                private String tenantDashboardsFolderPath;
                private String tenantDashboardPropertiesIdPathPattern;
                private String tenantDashboardPropertiesIdName;
                private Long idReservedQuantity;
                /**
                 * MSCONFG mode: write dashboard files through the xm-ms-config Kafka queue instead of the HTTP API
                 * with the user token, and generate ids from time instead of the dashboards-id.yml counter.
                 */
                private boolean updateViaKafka;
        }

    }
}
