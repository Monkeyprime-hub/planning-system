package com.akvelon.planningsystem.util;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class JdbcUtil {
    static String DEFAULT_DATABASE_NAME = "task_system";
    static String DEFAULT_USERNAME = "postgres";
    static String DEFAULT_PASSWORD = "postgres";


    public static DataSource createDefaultPostgresDataSource() {
        String url = formatPostgresDbUrl(DEFAULT_DATABASE_NAME);
        return createPostgresDataSource(url, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public static DataSource createPostgresDataSource(String url, String username, String pass) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        dataSource.setUser(username);
        dataSource.setPassword(pass);
        return dataSource;
    }

    private static String formatPostgresDbUrl(String databaseName) {
        return String.format("jdbc:postgresql://localhost:5432/%s", databaseName);
    }


}