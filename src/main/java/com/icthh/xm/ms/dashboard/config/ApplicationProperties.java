package com.icthh.xm.ms.dashboard.config;

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
public class ApplicationProperties {

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

        private StorageType type;
        private boolean storeConfigurationEnabled;
        private MsConfigStorageProperties msConfig;

        public enum StorageType {
            MSCONFIG, RDBMS
        }

        @Getter
        @Setter
        public static class MsConfigStorageProperties {
                private String tenantDashboardsFolderPathPattern;
                private String tenantDashboardsFolderPath;
                private String tenantDashboardPropertiesIdPathPattern;
                private String tenantDashboardPropertiesIdName;
                private Long idReservedQuantity;
        }

    }
}
