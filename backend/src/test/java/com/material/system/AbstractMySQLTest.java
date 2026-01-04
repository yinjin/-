package com.material.system;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.DockerClientFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class AbstractMySQLTest {

    // Do not auto-start container; start only when Docker is available to allow local H2 fallback.
    private static MySQLContainer<?> mysql;

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        // If Docker is available, start a MySQL container and override datasource properties.
        if (DockerClientFactory.instance().isDockerAvailable()) {
            mysql = new MySQLContainer<>("mysql:8.0.33")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");
            mysql.start();

            registry.add("spring.datasource.url", mysql::getJdbcUrl);
            registry.add("spring.datasource.username", mysql::getUsername);
            registry.add("spring.datasource.password", mysql::getPassword);
            registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
            registry.add("spring.flyway.enabled", () -> "true");
        }
        // If Docker isn't available, do nothing and allow existing test profile (H2) to be used.
    }
}

