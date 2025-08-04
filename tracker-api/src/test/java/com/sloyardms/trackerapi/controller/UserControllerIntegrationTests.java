package com.sloyardms.trackerapi.controller;

import com.sloyardms.trackerapi.dto.UserDto;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Arrays;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class UserControllerIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.5"))
            .withDatabaseName("tracker_db")
            .withUsername("user")
            .withPassword("password");

//    @DynamicPropertySource
//    private static void overrideProperties(DynamicPropertyRegistry registry){
//        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", postgresContainer::getUsername);
//        registry.add("spring.datasource.password", postgresContainer::getPassword);
//    }

    @Test
    void testPing_whenPinged_returnsPong() {
        var response = restTemplate.getForEntity("/api/v1/users/ping", String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status should be 200");
    }

    @Test
    @DisplayName("Test Postgre container is created and running")
    void testPostgreContainer_whenCreatedAndRunning_returnsTrue() {
        Assertions.assertTrue(postgresContainer.isCreated(), "Postgre container should be created");
        Assertions.assertTrue(postgresContainer.isRunning(), "Postgre container should be running");
    }

    @Test
    @DisplayName("Create User - Valid User")
    void testCreateUser_whenValidUserProvided_returnsCreatedUser() throws Exception {
        // Arrange
        JSONObject requestBody = new JSONObject();
        UUID userId = UUID.randomUUID();
        requestBody.put("uuid", userId.toString());
        requestBody.put("username", "validUsername");
        requestBody.put("darkMode", true);
        requestBody.put("keepOriginalImage", true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        // Act
        ResponseEntity< UserDto> response = restTemplate.postForEntity("/api/v1/users", request, UserDto.class);
        UserDto createdUser = response.getBody();

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response status should be 201");
        Assertions.assertEquals(requestBody.getString("uuid"), createdUser.getUuid().toString(), "User UUID should match");
        Assertions.assertEquals(requestBody.getString("username"), createdUser.getUsername(), "User username should match");
        Assertions.assertTrue(createdUser.getDarkMode(), "Dark mode should be true" );
        Assertions.assertTrue(createdUser.getKeepOriginalImage(), "Keep original image should be true" );
        Assertions.assertNotNull(createdUser.getCreatedAt(), "Created at should not be null" );
        Assertions.assertNotNull(createdUser.getUpdatedAt(), "Updated at should not be null");
    }

}
