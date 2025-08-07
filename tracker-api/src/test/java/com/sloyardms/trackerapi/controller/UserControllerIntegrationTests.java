package com.sloyardms.trackerapi.controller;

import com.sloyardms.trackerapi.user.dto.UserCreateDto;
import com.sloyardms.trackerapi.user.dto.UserUpdateDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class UserControllerIntegrationTests {

    @LocalServerPort
    private int port;

    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.5"))
                    .withDatabaseName("tracker_db")
                    .withUsername("user")
                    .withPassword("password");

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("Ping - Pong")
    void testPing_whenPinged_returnsPong() {
        given()
                .when()
                .get("/api/v1/users/ping")
                .then()
                .statusCode(200)
                .body(equalTo("pong"));
    }

    @Test
    @DisplayName("Test Postgres container is created and running")
    void testPostgresContainer_whenCreatedAndRunning_returnsTrue() {
        Assertions.assertTrue(postgresContainer.isCreated(), "Postgres container should be created");
        Assertions.assertTrue(postgresContainer.isRunning(), "Postgres container should be running");
    }

    @Test
    @DisplayName("Create User - Valid User")
    void testCreateUser_whenValidUserProvided_returnsCreatedUser(){
        // Arrange
        UserCreateDto user1 = new UserCreateDto();
        UUID userId = UUID.randomUUID();
        user1.setUuid(userId);
        user1.setUsername("validUsername");
        user1.setDarkMode(true);
        user1.setKeepOriginalImage(true);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user1)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(201)
                .body("uuid", equalTo(userId.toString()))
                .body("username", equalTo("validUsername"))
                .body("darkMode", equalTo(true))
                .body("keepOriginalImage", equalTo(true))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    @DisplayName("Create User - Duplicate username")
    void testCreateUser_whenDuplicateUsernameProvided_returnsResourceDuplicatedException(){
        //Arrange
        String duplicatedUsername = "duplicatedUsername";

        UserCreateDto user1 = new UserCreateDto();
        user1.setUuid(UUID.randomUUID());
        user1.setUsername(duplicatedUsername);

        UserCreateDto user2 = new UserCreateDto();
        user2.setUuid(UUID.randomUUID());
        user2.setUsername(duplicatedUsername);

        //The first user should be created
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user1)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(201);

        //Act and Assert - Second user (same username) should return 409
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user2)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(409);

    }

    @Test
    @DisplayName("Create User - Duplicated UUID")
    void testCreateUser_whenDuplicateIdProvided_returnsResourceDuplicatedException(){
        //Arrange
        UUID duplicateUuid = UUID.randomUUID();

        UserCreateDto user1 = new UserCreateDto();
        user1.setUuid(duplicateUuid);
        user1.setUsername("username1");

        UserCreateDto user2 = new UserCreateDto();
        user2.setUuid(duplicateUuid);
        user2.setUsername("username2");

        //The First user should be created
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user1)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(201);

        //Act and Assert Create second user with same UUID , should return 409
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user2)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(409);
    }

    @Test
    @DisplayName("Find User - Valid UUID")
    void testFindUser_whenValidIdProvided_returnsFoundUser(){
        //Arrange
        UUID userId = UUID.randomUUID();

        UserCreateDto user = new UserCreateDto();
        user.setUuid(userId);
        user.setUsername("username");
        user.setDarkMode(true);
        user.setKeepOriginalImage(true);

        // Create user
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(201);

        // Act and Assert: Find user by id
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/users/{id}", userId)
                .then()
                .statusCode(200)
                .body("uuid", equalTo(userId.toString()))
                .body("username", equalTo("username"))
                .body("darkMode", equalTo(true))
                .body("keepOriginalImage", equalTo(true))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());

    }

    @Test
    @DisplayName("Update User - Valid UUID And Body")
    void testUpdate_whenValidIdAndBodyProvided_returnsUpdatedUser(){
        //Arrange
        UUID userId = UUID.randomUUID();

        UserCreateDto dbUser = new UserCreateDto();
        dbUser.setUuid(userId);
        dbUser.setUsername("savedUsername");

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUsername("updatedUsername");
        userUpdateDto.setDarkMode(true);

        // The user should be created
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dbUser)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(201);

        // Act & Assert - Update user
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(userUpdateDto)
                .when()
                .patch("/api/v1/users/{id}", userId)
                .then()
                .statusCode(200)
                .body("uuid", equalTo(userId.toString()))
                .body("username", equalTo("updatedUsername"))
                .body("darkMode", equalTo(true))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    @DisplayName("Update User - Invalid UUID")
    void testUpdate_whenInvalidIdProvided_returnsResourceNotFound(){
        //Arrange
        UUID invalidUserId  = UUID.randomUUID();

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUsername("updatedUsername");
        userUpdateDto.setDarkMode(true);

        // Act + Assert
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(userUpdateDto)
                .when()
                .patch("/api/v1/users/{id}", invalidUserId)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Delete User - Valid UUID")
    void testDelete_whenValidIdProvided_returnsNothing(){
        //Arrange
        UUID userId = UUID.randomUUID();
        UserCreateDto dbUser = new UserCreateDto();
        dbUser.setUuid(userId);
        dbUser.setUsername("savedUsername");

        // The first user should be created
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dbUser)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(201);

        // Act - Delete the user
        given()
                .when()
                .delete("/api/v1/users/{id}", userId)
                .then()
                .statusCode(204);

        // Assert - Check user no longer exists
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/users/{id}", userId)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Delete User - Invalid UUID")
    void testDelete_whenInvalidIdProvided_returnsResourceNotFound(){
        //Arrange
        UUID invalidUserId = UUID.randomUUID();

        // Act & Assert
        given()
                .when()
                .delete("/api/v1/users/{id}", invalidUserId)
                .then()
                .statusCode(404);
    }

}
