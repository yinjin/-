package com.material.system;

import org.junit.jupiter.api.Assumptions;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.DockerClientFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class AbstractMySQLTest {

    private static MySQLContainer<?> mysql;

    static {
        // Assume Docker is available; if not, skip tests
        Assumptions.assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker is not available, skipping MySQL container tests");

        // Use MySQL 8.0 for compatibility with production
        mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
        mysql.start();
    }

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
    }
}

