package org.slavbx;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

public class DatabaseProvider {
    private static final String CREATE_SCHEMA =
            "CREATE SCHEMA IF NOT EXISTS %s";

    public static Connection getConnection() throws SQLException {
        Connection connection;
            Properties properties = new Properties();
            try {
                InputStream in = Files.newInputStream(Paths.get("src/main/resources/database.properties"));
                properties.load(in);
                connection = DriverManager.getConnection(
                        properties.getProperty("database.url"),
                        properties.getProperty("database.username"),
                        properties.getProperty("database.password")
                );
            } catch (Exception e) {
                throw new SQLException("Connection to database failed");
            }
        return connection;
    }

    public static void initDatabase()  {
        Connection connection;
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("src/main/resources/liquibase.properties"))) {
            properties.load(in);
            connection = DriverManager.getConnection(
                    properties.getProperty("liquibase.url"),
                    properties.getProperty("liquibase.username"),
                    properties.getProperty("liquibase.password")
            );
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(properties.getProperty("liquibase.defaultSchemaName"));

            try (Statement statement = connection.createStatement()) {
                String sql = String.format(CREATE_SCHEMA, properties.getProperty("liquibase.defaultSchemaName"));
                statement.execute(sql);
            } catch (SQLException e) {
                System.err.println("Error create default schema: " + e.getMessage());
            }

            Liquibase liquibase = new Liquibase(properties.getProperty("liquibase.changeLogFile"),
                    new ClassLoaderResourceAccessor(), database);
            liquibase.update();

//            liquibase.getDatabase().setDefaultSchemaName("habittracker");
//            liquibase.update();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
