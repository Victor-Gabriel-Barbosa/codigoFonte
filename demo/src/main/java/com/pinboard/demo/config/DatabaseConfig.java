package com.pinboard.demo.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    private final Environment env;

    public DatabaseConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            String jdbcUrl = convertToJdbcUrl(databaseUrl);
            String[] userInfo = extractUserInfo(databaseUrl);

            return DataSourceBuilder.create()
                    .driverClassName("org.postgresql.Driver")
                    .url(jdbcUrl)
                    .username(userInfo[0])
                    .password(userInfo[1])
                    .build();
        }

        // Fallback: construct JDBC URL from individual PG* environment variables
        String pgHost = getEnvOrDefault("PGHOST", "localhost");
        String pgPort = getEnvOrDefault("PGPORT", "5432");
        String pgDatabase = getEnvOrDefault("PGDATABASE", "pinboard");
        String pgUser = getEnvOrDefault("PGUSER", "postgres");
        String pgPassword = getEnvOrDefault("PGPASSWORD", "");

        String jdbcUrl = "jdbc:postgresql://" + pgHost + ":" + pgPort + "/" + pgDatabase;

        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url(jdbcUrl)
                .username(pgUser)
                .password(pgPassword)
                .build();
    }

    private String convertToJdbcUrl(String databaseUrl) {
        // Normalize scheme to jdbc:postgresql://
        String normalized = databaseUrl
                .replace("postgresql://", "jdbc:postgresql://")
                .replace("postgres://", "jdbc:postgresql://");

        // Strip embedded user:password@ from the URL so credentials are passed separately
        int schemeEnd = normalized.indexOf("://");
        if (schemeEnd != -1) {
            int atIndex = normalized.indexOf('@', schemeEnd);
            if (atIndex != -1) {
                normalized = normalized.substring(0, schemeEnd + 3) + normalized.substring(atIndex + 1);
            }
        }
        return normalized;
    }

    private String[] extractUserInfo(String databaseUrl) {
        int atIndex = databaseUrl.indexOf('@');
        if (atIndex == -1) {
            return new String[]{"postgres", ""};
        }

        String userPass = databaseUrl.substring(databaseUrl.indexOf("//") + 2, atIndex);
        String[] parts = userPass.split(":", 2);

        return new String[]{
            parts.length > 0 ? parts[0] : "postgres",
            parts.length > 1 ? parts[1] : ""
        };
    }

    private String getEnvOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}
