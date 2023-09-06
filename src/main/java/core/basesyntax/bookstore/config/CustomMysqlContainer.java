package core.basesyntax.bookstore.config;

import org.testcontainers.containers.MySQLContainer;

public class CustomMysqlContainer extends MySQLContainer<CustomMysqlContainer> {
    private static final String DB_IMAGE = "mysql:8";
    private static final String URL = "TEST_DB_URL";
    private static final String USERNAME = "TEST_DB_USERNAME";
    private static final String PASSWORD = "TEST_DB_PASSWORD";

    private static CustomMysqlContainer mysqlContainer;

    private CustomMysqlContainer() {
        super(DB_IMAGE);
    }

    public static synchronized CustomMysqlContainer getInstance() {
        if (mysqlContainer == null) {
            mysqlContainer = new CustomMysqlContainer();
        }
        return mysqlContainer;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty(URL, mysqlContainer.getJdbcUrl());
        System.setProperty(USERNAME, mysqlContainer.getUsername());
        System.setProperty(PASSWORD, mysqlContainer.getPassword());
    }

    @Override
    public void stop() {

    }
}
