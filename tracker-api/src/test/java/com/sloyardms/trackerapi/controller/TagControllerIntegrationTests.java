package com.sloyardms.trackerapi.controller;

import com.sloyardms.trackerapi.dto.TagCreateDto;
import com.sloyardms.trackerapi.dto.TagDto;
import com.sloyardms.trackerapi.dto.TagUpdateDto;
import com.sloyardms.trackerapi.user.dto.UserCreateDto;
import com.sloyardms.trackerapi.repository.TagRepository;
import com.sloyardms.trackerapi.security.DevSecurityConfig;
import com.sloyardms.trackerapi.security.FakeAuthFilter;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@Import(DevSecurityConfig.class)
public class TagControllerIntegrationTests {

    @Autowired
    private TagRepository tagRepository;

    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.5"))
                    .withDatabaseName("tracker_db")
                    .withUsername("user")
                    .withPassword("password");

    @BeforeAll
    static void setUpAll(@LocalServerPort int port){
        RestAssured.port = port;

        //Create user
        UserCreateDto user = new UserCreateDto();
        user.setUuid(FakeAuthFilter.DEV_USER_UUID);
        user.setUsername("username");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        System.out.println("Created valid user: " + FakeAuthFilter.DEV_USER_UUID);
    }

    @BeforeEach
    void beforeEach(){
        tagRepository.deleteAll();
        System.out.println("Deleted all tags");
    }

    @Test
    @DisplayName("Test Postgres container is created and running")
    void testPostgresContainer_whenCreatedAndRunning_returnsTrue() {
        Assertions.assertTrue(postgresContainer.isCreated(), "Postgres container should be created");
        Assertions.assertTrue(postgresContainer.isRunning(), "Postgres container should be running");
    }

    @Test
    @DisplayName("Ping - Pong")
    void testPing_whenPinged_returnsPong() {
        given()
                .when()
                .get("/api/v1/tags/ping")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("pong"));
    }

    @Test
    @DisplayName("Create Tag - Valid Tag")
    void testCreateTag_whenValidTagProvided_returnsCreatedTag(){
        //Arrange tag
        String validName = "validTag1Name";

        TagCreateDto tag = new TagCreateDto();
        tag.setName(validName);

        // Act & Assert - create tag
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tag)
                .when()
                .post("/api/v1/tags")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("uuid", notNullValue())
                .body("name", equalTo(validName));
    }

    @Test
    @DisplayName("Create Tag - Duplicate Tag Name")
    void testCreateTag_whenInvalidTagProvided_returnsResourceDuplicatedException(){
        //Arrange tag
        TagCreateDto tag1 = new TagCreateDto();
        tag1.setName("tag1");

        TagCreateDto tag2 = new TagCreateDto();
        tag2.setName("tag1");

        // Act - create valid tag
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tag1)
                .when()
                .post("/api/v1/tags")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Act & Assert - try to create invalid tag
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tag2)
                .when()
                .post("/api/v1/tags")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Find Tag - Valid UUID")
    void testFindTag_whenValidUuidProvided_returnsFoundTag(){
        //Arrange tag
        TagCreateDto tag1 = new TagCreateDto();
        tag1.setName("tag1");

        // Act - create valid tag
        TagDto createdTag = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tag1)
                .when()
                .post("/api/v1/tags")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(TagDto.class);

        // Act & Assert find tag by uuid
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/tags/{id}", createdTag.getUuid())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("uuid", equalTo(createdTag.getUuid().toString()))
                .body("name", equalTo(createdTag.getName()));
    }

    @Test
    @DisplayName("Find Tag - Invalid UUID")
    void testFindTag_whenInvalidUuidProvided_returnsResourceNotFoundException(){
        //Arrange user
        UUID invalidUuid = UUID.randomUUID();

        // Act & Assert find tag by uuid
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/tags/{id}", invalidUuid)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Find Tags of User - Valid User UUID")
    void testFindTagsOfUser_whenValidUserProvided_returnsUserTags(){
        //Arrange user
        UUID userId = FakeAuthFilter.DEV_USER_UUID;

        //Arrange tag
        TagCreateDto tag1 = new TagCreateDto();
        tag1.setName("tag1");

        // Act - create valid tag for the user
        TagDto createdTag = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tag1)
                .when()
                .post("/api/v1/tags")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(TagDto.class);

        // Act
        Response response = given()
                .accept(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "name,asc")
                .when()
                .get("/api/v1/tags/user/{uuid}", userId);

        // Assert
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].uuid", equalTo(createdTag.getUuid().toString()))
                .body("content[0].name", equalTo(createdTag.getName()))
                .body("totalElements", equalTo(1))
                .body("totalPages", equalTo(1))
                .body("size", equalTo(10))
                .body("number", equalTo(0));
    }

    @Test
    @DisplayName("Find Tags of User - Valid User UUID with No Tags")
    void testFindTagsOfUser_whenUserHasNoTags_returnsEmptyPage(){
        //Arrange
        UUID userId = FakeAuthFilter.DEV_USER_UUID;

        // Act
        Response response = given()
                .accept(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "name,asc")
                .when()
                .get("/api/v1/tags/user/{uuid}", userId);

        // Assert
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("totalElements", equalTo(0))
                .body("totalPages", equalTo(0))
                .body("size", equalTo(10))
                .body("number", equalTo(0));
    }

    @Test
    @DisplayName("Update Tag - Valid Tag Name")
    void testUpdateTag_whenValidTagNameProvided_returnsUpdatedTag(){
        //Arrange tag
        TagCreateDto tag1 = new TagCreateDto();
        tag1.setName("name");

        //Arrange update tag request
        TagUpdateDto updateBody = new TagUpdateDto();
        updateBody.setName("updatedName");

        // Act - create valid tag for the user
        TagDto createdTag = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tag1)
                .when()
                .post("/api/v1/tags")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(TagDto.class);

        // Act - Update tag
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(updateBody)
                .when()
                .patch("/api/v1/tags/{uuid}", createdTag.getUuid());

        // Assert
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("uuid", equalTo(createdTag.getUuid().toString()))
                .body("name", equalTo(updateBody.getName()));
    }

    @Test
    @DisplayName("Update Tag - Duplicated Tag Name")
    void testUpdateTag_whenDuplicatedTagNameProvided_returnsResourceDuplicatedException(){
        //Arrange tags
        TagCreateDto tag1 = new TagCreateDto();
        tag1.setName("name1");

        TagCreateDto tag2 = new TagCreateDto();
        tag2.setName("name2");

        //Arrange update tag request
        TagUpdateDto updateBody = new TagUpdateDto();
        updateBody.setName("name2");

        // Act - create valid tag 1
        TagDto createdTag1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tag1)
                .when()
                .post("/api/v1/tags")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(TagDto.class);

        // Act - create valid tag 2
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tag2)
                .when()
                .post("/api/v1/tags")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Act - Update tag1 with tag2 name
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(updateBody)
                .when()
                .patch("/api/v1/tags/{uuid}", createdTag1.getUuid());

        // Assert
        response.then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Delete Tag - Valid UUID")
    void testDeleteTag_whenValidUuidProvided_returnsNothing(){
        //Arrange tags
        TagCreateDto tag1 = new TagCreateDto();
        tag1.setName("name1");

        // Act - create valid tag 1
        TagDto createdTag1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tag1)
                .when()
                .post("/api/v1/tags")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(TagDto.class);

        // Act - Delete tag1
        given()
                .when()
                .delete("/api/v1/tags/{uuid}", createdTag1.getUuid())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Assert - tag1 is not found
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/tags/{uuid}", createdTag1.getUuid())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Delete Tag - Invalid UUID")
    void testDeleteTag_whenInvalidUuidProvided_returnsResourceNotFoundException(){
        //Arrange
        UUID invalidUuid = UUID.randomUUID();

        // Act - Delete with invalid UUID
        given()
                .when()
                .delete("/api/v1/tags/{uuid}", invalidUuid)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
