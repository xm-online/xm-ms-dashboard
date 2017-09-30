package com.icthh.xm.ms.dashboard.config.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DropSchemaResolver {

    private static final String DEFAULT_COMMAND = "DROP SCHEMA IF EXISTS %s";

    private static final Map<String, String> DBCOMMANDS = new HashMap<String, String>();

    static {
        DBCOMMANDS.put("POSTGRESQL", DEFAULT_COMMAND + " CASCADE");
        DBCOMMANDS.put("H2", DEFAULT_COMMAND);
    }

    private String dbDropSchemaCommand;

    public DropSchemaResolver(Environment env) {
        String db = env.getProperty("spring.jpa.database");
        this.dbDropSchemaCommand = DBCOMMANDS.getOrDefault(db, DEFAULT_COMMAND);
        log.info("Database {} will use command '{}' for drop schema", db, dbDropSchemaCommand);
    }

    public String getSchemaDropCommand() {
        return this.dbDropSchemaCommand;
    }

}
