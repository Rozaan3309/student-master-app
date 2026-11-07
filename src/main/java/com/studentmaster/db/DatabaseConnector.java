package com.studentmaster.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnector {

    private static final Properties CONFIG = loadConfig();

    private DatabaseConnector() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url"),
                CONFIG.getProperty("db.user"),
                CONFIG.getProperty("db.password", ""));
    }

    private static Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream in = DatabaseConnector.class.getResourceAsStream("/db.properties")) {
            if (in != null) props.load(in);
        } catch (IOException e) {
            System.err.println("Could not read default db.properties: " + e.getMessage());
        }
        Path local = Path.of("db.properties");
        if (Files.isRegularFile(local)) {
            try (InputStream in = Files.newInputStream(local)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("Could not read ./db.properties: " + e.getMessage());
            }
        }
        override(props, "db.url", "DB_URL");
        override(props, "db.user", "DB_USER");
        override(props, "db.password", "DB_PASSWORD");
        return props;
    }

    private static void override(Properties props, String key, String envName) {
        String value = System.getenv(envName);
        if (value != null) props.setProperty(key, value);
    }
}
