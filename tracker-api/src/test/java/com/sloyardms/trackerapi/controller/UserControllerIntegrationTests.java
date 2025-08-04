package com.sloyardms.trackerapi.controller;

import com.sloyardms.trackerapi.dto.UserCreateDto;
import com.sloyardms.trackerapi.dto.UserDto;
import com.sloyardms.trackerapi.dto.UserUpdateDto;
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

import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class UserControllerIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.5"))
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
    @DisplayName("Test Postgres container is created and running")
    void testPostgresContainer_whenCreatedAndRunning_returnsTrue() {
        Assertions.assertTrue(postgresContainer.isCreated(), "Postgres container should be created");
        Assertions.assertTrue(postgresContainer.isRunning(), "Postgres container should be running");
    }

    @Test
    @DisplayName("Create User - Valid User")
    void testCreateUser_whenValidUserProvided_returnsCreatedUser() throws Exception {
        // Arrange
        UserCreateDto user1 = new UserCreateDto();
        UUID userId = UUID.randomUUID();
        user1.setUuid(userId);
        user1.setUsername("validUsername");
        user1.setDarkMode(true);
        user1.setKeepOriginalImage(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<UserCreateDto> request = new HttpEntity<>(user1, headers);

        // Act
        ResponseEntity<UserDto> response = restTemplate.postForEntity("/api/v1/users", request, UserDto.class);
        UserDto createdUser = response.getBody();

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response status should be 201");
        Assertions.assertEquals(user1.getUuid().toString(), createdUser.getUuid().toString(), "User UUID should match");
        Assertions.assertEquals(user1.getUsername(), createdUser.getUsername(), "User username should match");
        Assertions.assertTrue(createdUser.getDarkMode(), "Dark mode should be true" );
        Assertions.assertTrue(createdUser.getKeepOriginalImage(), "Keep original image should be true" );
        Assertions.assertNotNull(createdUser.getCreatedAt(), "Created at should not be null" );
        Assertions.assertNotNull(createdUser.getUpdatedAt(), "Updated at should not be null");
    }

    @Test
    @DisplayName("Create User - Duplicate username")
    void testCreateUser_whenDuplicateUsernameProvided_returnsResourceDuplicatedException() throws Exception{
        //Arrange
        UserCreateDto user1 = new UserCreateDto();
        UUID user1UUID = UUID.randomUUID();
        user1.setUuid(user1UUID);
        user1.setUsername("duplicatedUsername");

        UserCreateDto user2 = new UserCreateDto();
        UUID user2UUID = UUID.randomUUID();
        user2.setUuid(user2UUID);
        user2.setUsername("duplicatedUsername");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<UserCreateDto> request1 = new HttpEntity<>(user1, headers);
        HttpEntity<UserCreateDto> request2 = new HttpEntity<>(user2, headers);

        // Act
        ResponseEntity< UserDto> response1 = restTemplate.postForEntity("/api/v1/users", request1, UserDto.class);
        ResponseEntity< UserDto> response2 = restTemplate.postForEntity("/api/v1/users", request2, UserDto.class);
        int responseStatus = response2.getStatusCode().value();

        // Assert
        Assertions.assertEquals(HttpStatus.CONFLICT.value(), responseStatus, "Response status should be 409");
    }

    @Test
    @DisplayName("Create User - Duplicate username")
    void testCreateUser_whenDuplicateIdProvided_returnsResourceDuplicatedException() throws Exception{
        //Arrange
        UserCreateDto user1 = new UserCreateDto();
        UUID user1UUID = UUID.randomUUID();
        user1.setUuid(user1UUID);
        user1.setUsername("username1");

        UserCreateDto user2 = new UserCreateDto();
        user2.setUuid(user1UUID);
        user2.setUsername("username2");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<UserCreateDto> request1 = new HttpEntity<>(user1, headers);
        HttpEntity<UserCreateDto> request2 = new HttpEntity<>(user2, headers);

        // Act
        ResponseEntity<UserDto> response1 = restTemplate.postForEntity("/api/v1/users", request1, UserDto.class);
        ResponseEntity<UserDto> response2 = restTemplate.postForEntity("/api/v1/users", request2, UserDto.class);
        int responseStatus = response2.getStatusCode().value();

        // Assert
        Assertions.assertEquals(HttpStatus.CONFLICT.value(), responseStatus, "Response status should be 409");
    }

    @Test
    @DisplayName("Find User - Valid UUID")
    void testFindUser_whenValidIdProvided_returnsFoundUser() throws Exception {
        //Arrange
        UserCreateDto savedUser = new UserCreateDto();
        UUID userId = UUID.randomUUID();
        savedUser.setUuid(userId);
        savedUser.setUsername("username");
        savedUser.setDarkMode(true);
        savedUser.setKeepOriginalImage(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<UserCreateDto> request1 = new HttpEntity<>(savedUser, headers);

        //Act
        ResponseEntity<UserDto> response1 = restTemplate.postForEntity("/api/v1/users", request1, UserDto.class);
        ResponseEntity<UserDto> response = restTemplate.getForEntity("/api/v1/users/{id}", UserDto.class, userId);
        UserDto foundUser = response.getBody();

        //Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status should be 200");
        Assertions.assertEquals(userId.toString(), foundUser.getUuid().toString(), "User UUID should match");
        Assertions.assertEquals(savedUser.getUsername(), foundUser.getUsername(), "User username should match");
        Assertions.assertTrue(foundUser.getDarkMode(), "Dark mode should be true" );
        Assertions.assertTrue(foundUser.getKeepOriginalImage(), "Keep original image should be true" );
        Assertions.assertNotNull(foundUser.getCreatedAt(), "Created at should not be null" );
        Assertions.assertNotNull(foundUser.getUpdatedAt(), "Updated at should not be null");
    }

    @Test
    @DisplayName("Update User - Invalid UUID")
    void testUpdate_whenValidIdAndBodyProvided_returnsUpdatedUser() throws Exception {
        //Arrange
        UUID userId = UUID.randomUUID();
        UserCreateDto dbUser = new UserCreateDto();
        dbUser.setUuid(userId);
        dbUser.setUsername("savedUsername");

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUsername("updatedUsername");
        userUpdateDto.setDarkMode(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<UserCreateDto> saveRequest = new HttpEntity<>(dbUser, headers);
        HttpEntity<UserUpdateDto> updateRequest = new HttpEntity<>(userUpdateDto, headers);

        //Act
        ResponseEntity<UserDto> saveResponse = restTemplate.postForEntity("/api/v1/users", saveRequest, UserDto.class);
        Assertions.assertEquals(HttpStatus.CREATED, saveResponse.getStatusCode(), "Response status should be 201 when saving user" );

        ResponseEntity<UserDto> updateResponse = restTemplate.exchange("/api/v1/users/"+userId.toString(), HttpMethod.PATCH, updateRequest, UserDto.class);
        UserDto updatedUser = updateResponse.getBody();

        //Assert
        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode(), "Response status should be 200");
        Assertions.assertEquals(userUpdateDto.getUsername(), updatedUser.getUsername(), "User username should match" );
        Assertions.assertTrue(updatedUser.getDarkMode(), "Dark mode should be true" );
        Assertions.assertNotNull(updatedUser.getCreatedAt(), "Created at should not be null" );
        Assertions.assertNotNull(updatedUser.getUpdatedAt(), "Updated at should not be null");
    }

    @Test
    @DisplayName("Update User - Invalid UUID")
    void testUpdate_whenInvalidIdProvided_returnsResourceNotFound() throws Exception {
        //Arrange
        UUID userId = UUID.randomUUID();

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUsername("updatedUsername");
        userUpdateDto.setDarkMode(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<UserUpdateDto> updateRequest = new HttpEntity<>(userUpdateDto, headers);

        //Act
        ResponseEntity<UserDto> updateResponse = restTemplate.exchange("/api/v1/users/"+userId.toString(), HttpMethod.PATCH, updateRequest, UserDto.class);
        UserDto updatedUser = updateResponse.getBody();

        //Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, updateResponse.getStatusCode(), "Response status should be 404");
    }

    @Test
    @DisplayName("Delete User - Valid UUID")
    void testDelete_whenValidIdProvided_returnsNothing() throws Exception{
        //Arrange
        UUID userId = UUID.randomUUID();
        UserCreateDto dbUser = new UserCreateDto();
        dbUser.setUuid(userId);
        dbUser.setUsername("savedUsername");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<UserCreateDto> saveRequest = new HttpEntity<>(dbUser, headers);

        //Act
        ResponseEntity<UserDto> saveResponse = restTemplate.postForEntity("/api/v1/users", saveRequest, UserDto.class);
        Assertions.assertEquals(HttpStatus.CREATED, saveResponse.getStatusCode(), "Response status should be 201 when saving user");

        restTemplate.delete("/api/v1/users/"+userId.toString());

        ResponseEntity<UserDto> getAfterDelete = restTemplate.getForEntity("/api/v1/users/{id}", UserDto.class, userId);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, getAfterDelete.getStatusCode());
    }

    @Test
    @DisplayName("Delete User - Invalid UUID")
    void testDelete_whenInvalidIdProvided_returnsResourceNotFound() throws Exception{
        //Arrange
        UUID userId = UUID.randomUUID();

        //Act
        ResponseEntity<Void> response = restTemplate.exchange("/api/v1/users/" + userId.toString(), HttpMethod.DELETE, null, Void.class);

        //Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Response status should be 404" );
    }

}
