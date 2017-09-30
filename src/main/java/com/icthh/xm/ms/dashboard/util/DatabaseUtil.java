package com.icthh.xm.ms.dashboard.util;

import static com.icthh.xm.ms.dashboard.config.Constants.DDL_CREATE_SCHEMA;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility for database operations.
 */
public final class DatabaseUtil {

    private DatabaseUtil() {

    }

    /**
     * Creates new database scheme.
     *
     * @param dataSource the datasource
     * @param name       schema name
     */
    public static void createSchema(DataSource dataSource, String name) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format(DDL_CREATE_SCHEMA, name));
        } catch (SQLException e) {
            throw new RuntimeException("Can not connect to database", e);
        }
    }

}
