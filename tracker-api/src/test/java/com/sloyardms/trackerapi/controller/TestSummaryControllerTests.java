package com.sloyardms.trackerapi.controller;

import com.sloyardms.trackerapi.bookmark.BookmarkRepository;
import com.sloyardms.trackerapi.group.GroupRepository;
import com.sloyardms.trackerapi.security.DevSecurityConfig;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@Import(DevSecurityConfig.class)
public class TestSummaryControllerTests {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private TagS

    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.5"))
                    .withDatabaseName("tracker_db")
                    .withUsername("user")
                    .withPassword("password");

    @BeforeAll
    static void setupAll(@LocalServerPort int port) {
        RestAssured.port = port;
    }

    @BeforeEach
    void beforeEach() {

    }


}
