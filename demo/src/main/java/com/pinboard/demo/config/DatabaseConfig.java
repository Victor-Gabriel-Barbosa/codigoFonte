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
            String url = convertRailwayUrl(databaseUrl);
            String[] userInfo = extractUserInfo(databaseUrl);

            return DataSourceBuilder.create()
                    .driverClassName("org.postgresql.Driver")
                    .url(url)
                    .username(userInfo[0])
                    .password(userInfo[1])
                    .build();
        }

        // Fallback: usar propriedades do application.properties
        return DataSourceBuilder.create()
                .url(env.getProperty("SPRING_DATASOURCE_URL", "jdbc:postgresql://localhost:5432/pinboard"))
                .username(env.getProperty("SPRING_DATASOURCE_USERNAME", "postgres"))
                .password(env.getProperty("SPRING_DATASOURCE_PASSWORD", ""))
                .build();
    }

    private String convertRailwayUrl(String databaseUrl) {
        return databaseUrl.replace("postgresql://", "jdbc:postgresql://").replace("postgres://", "jdbc:postgresql://");
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
}
