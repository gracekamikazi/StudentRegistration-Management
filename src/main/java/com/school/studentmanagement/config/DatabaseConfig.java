package com.school.studentmanagement.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/** Creates the DataSource bean using credentials from environment variables. */
@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource(@Value("${app.datasource.url}") String url,
                                 @Value("${app.datasource.username}") String username,
                                 @Value("${app.datasource.password}") String password) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("org.postgresql.Driver");
        return ds;
    }
}
