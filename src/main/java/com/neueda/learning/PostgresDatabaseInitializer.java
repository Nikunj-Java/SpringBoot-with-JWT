package com.neueda.learning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class PostgresDatabaseInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresDatabaseInitializer.class);
    private static final String JDBC_PREFIX = "jdbc:postgresql://";

    private PostgresDatabaseInitializer() {
    }

    public static void initialize(ConfigurableApplicationContext context) {
        Environment environment = context.getEnvironment();
        boolean autoCreateEnabled = Boolean.parseBoolean(
                environment.getProperty("app.datasource.auto-create-database", "false")
        );
        if (!autoCreateEnabled) {
            return;
        }

        String dataSourceUrl = requiredProperty(environment, "spring.datasource.url");
        String username = requiredProperty(environment, "spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password", "");

        ParsedUrl parsedUrl = parseJdbcUrl(dataSourceUrl);
        createDatabaseIfMissing(parsedUrl, username, password);
    }

    private static void createDatabaseIfMissing(ParsedUrl parsedUrl, String username, String password) {
        try (Connection connection = DriverManager.getConnection(parsedUrl.adminUrl(), username, password)) {
            if (databaseExists(connection, parsedUrl.databaseName())) {
                return;
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE DATABASE \"" + parsedUrl.databaseName().replace("\"", "\"\"") + "\"");
            }
            LOGGER.info("Created PostgreSQL database: {}", parsedUrl.databaseName());
        } catch (SQLException ex) {
            throw new IllegalStateException(
                    "Failed to auto-create PostgreSQL database '" + parsedUrl.databaseName() + "'.",
                    ex
            );
        }
    }

    private static boolean databaseExists(Connection connection, String databaseName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM pg_database WHERE datname = ?"
        )) {
            statement.setString(1, databaseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static ParsedUrl parseJdbcUrl(String url) {
        if (!url.startsWith(JDBC_PREFIX)) {
            throw new IllegalStateException(
                    "Auto-create database supports only PostgreSQL JDBC URLs. Found: " + url
            );
        }

        String hostAndPath = url.substring(JDBC_PREFIX.length());
        int slashIndex = hostAndPath.indexOf('/');
        if (slashIndex <= 0 || slashIndex == hostAndPath.length() - 1) {
            throw new IllegalStateException("Invalid PostgreSQL JDBC URL: " + url);
        }

        String hostPart = hostAndPath.substring(0, slashIndex);
        String dbWithQuery = hostAndPath.substring(slashIndex + 1);
        int queryIndex = dbWithQuery.indexOf('?');

        String databaseName = queryIndex >= 0 ? dbWithQuery.substring(0, queryIndex) : dbWithQuery;
        if (databaseName.isBlank()) {
            throw new IllegalStateException("Database name is missing in JDBC URL: " + url);
        }

        String query = queryIndex >= 0 ? dbWithQuery.substring(queryIndex) : "";
        String adminUrl = JDBC_PREFIX + hostPart + "/postgres" + query;
        return new ParsedUrl(adminUrl, databaseName);
    }

    private static String requiredProperty(Environment environment, String key) {
        String value = environment.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return value;
    }

    private record ParsedUrl(String adminUrl, String databaseName) {
    }
}
