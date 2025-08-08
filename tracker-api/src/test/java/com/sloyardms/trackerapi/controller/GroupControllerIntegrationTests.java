package com.sloyardms.trackerapi.controller;

import com.sloyardms.trackerapi.group.dto.GroupCreateDto;
import com.sloyardms.trackerapi.group.dto.GroupDto;
import com.sloyardms.trackerapi.group.dto.GroupUpdateDto;
import com.sloyardms.trackerapi.user.dto.UserCreateDto;
import com.sloyardms.trackerapi.group.GroupRepository;
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
public class GroupControllerIntegrationTests {

    @Autowired
    private GroupRepository groupRepository;

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
        groupRepository.deleteAll();
        System.out.println("Deleted all groups");
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
                .get("/api/v1/groups/ping")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("pong"));
    }

    @Test
    @DisplayName("Create Group - Valid Group")
    void testCreateGroup_whenValidGroupProvided_returnsCreatedGroup(){
        //Arrange group
        String validName = "validGroup1Name";
        String validDescription = "validGroup1Description";

        GroupCreateDto group = new GroupCreateDto();
        group.setName(validName);
        group.setDescription(validDescription);

        // Act & Assert - create group
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(group)
                .when()
                .post("/api/v1/groups")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("uuid", notNullValue())
                .body("userUuid", notNullValue())
                .body("name", equalTo(validName))
                .body("description", equalTo(validDescription));
    }

    @Test
    @DisplayName("Create Group - Duplicate Group Name")
    void testCreateGroup_whenInvalidGroupProvided_returnsGroupNameAlreadyExistException(){
        //Arrange group
        GroupCreateDto group1 = new GroupCreateDto();
        group1.setName("group1");
        group1.setDescription("description");

        GroupCreateDto group2 = new GroupCreateDto();
        group2.setName("group1");
        group2.setDescription("description");

        // Act - create valid group
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(group1)
                .when()
                .post("/api/v1/groups")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Act & Assert - try to create invalid group
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(group2)
                .when()
                .post("/api/v1/groups")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Find Group - Valid UUID")
    void testFindGroup_whenValidUuidProvided_returnsFoundGroup(){
        //Arrange group
        GroupCreateDto group1 = new GroupCreateDto();
        group1.setName("group1");
        group1.setDescription("description");

        // Act - create valid group
        GroupDto createdGroup = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(group1)
                .when()
                .post("/api/v1/groups")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(GroupDto.class);

        // Act & Assert find group by uuid
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/groups/{id}", createdGroup.getUuid())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("uuid", equalTo(createdGroup.getUuid().toString()))
                .body("name", equalTo(createdGroup.getName()))
                .body("description", equalTo(createdGroup.getDescription()));
    }

    @Test
    @DisplayName("Find Group - Invalid UUID")
    void testFindGroup_whenInvalidUuidProvided_returnsGroupNotFoundException(){
        //Arrange user
        UUID invalidUuid = UUID.randomUUID();

        // Act & Assert find group by uuid
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/groups/{id}", invalidUuid)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Find Groups of User - Valid User UUID")
    void testFindGroupsOfUser_whenValidUserProvided_returnsUserGroups(){
        //Arrange user
        UUID userId = FakeAuthFilter.DEV_USER_UUID;

        //Arrange group
        GroupCreateDto group1 = new GroupCreateDto();
        group1.setName("group1");
        group1.setDescription("description");

        // Act - create valid group for the user
        GroupDto createdGroup = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(group1)
                .when()
                .post("/api/v1/groups")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(GroupDto.class);

        // Act
        Response response = given()
                .accept(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "name,asc")
                .when()
                .get("/api/v1/groups/user/{uuid}", userId);

        // Assert
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].uuid", equalTo(createdGroup.getUuid().toString()))
                .body("content[0].name", equalTo(createdGroup.getName()))
                .body("content[0].description", equalTo(createdGroup.getDescription()))
                .body("totalElements", equalTo(1))
                .body("totalPages", equalTo(1))
                .body("size", equalTo(10))
                .body("number", equalTo(0));
    }

    @Test
    @DisplayName("Find Groups of User - Valid User UUID")
    void testFindGroupsOfUser_whenUserHasNoGroups_returnsEmptyPage(){
        //Arrange group
        UUID userId = FakeAuthFilter.DEV_USER_UUID;

        // Act
        Response response = given()
                .accept(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "name,asc")
                .when()
                .get("/api/v1/groups/user/{uuid}", userId);

        // Assert
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("totalElements", equalTo(0))
                .body("totalPages", equalTo(0))
                .body("size", equalTo(10))
                .body("number", equalTo(0));
    }

    @Test
    @DisplayName("Update Group - Valid Group Name")
    void testUpdateGroup_whenValidGroupNameProvided_returnsUpdatedGroup(){
        //Arrange group
        GroupCreateDto group1 = new GroupCreateDto();
        group1.setName("name");
        group1.setDescription("description");

        //Arrange update group request
        GroupUpdateDto updateBody = new GroupUpdateDto();
        updateBody.setName("updatedName");
        updateBody.setDescription("updatedDescription");

        // Act - create valid group for the user
        GroupDto createdGroup = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(group1)
                .when()
                .post("/api/v1/groups")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(GroupDto.class);

        // Act - Update group
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(updateBody)
                .when()
                .patch("/api/v1/groups/{uuid}", createdGroup.getUuid());

        // Assert
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("uuid", equalTo(createdGroup.getUuid().toString()))
                .body("name", equalTo(updateBody.getName()))
                .body("description", equalTo(updateBody.getDescription()));
    }

    @Test
    @DisplayName("Update Group - Duplicated Group Name")
    void testUpdateGroup_whenDuplicatedGroupNameProvided_returnsGroupNameAlreadyExistException(){
        //Arrange groups
        GroupCreateDto group1 = new GroupCreateDto();
        group1.setName("name1");
        group1.setDescription("description1");

        GroupCreateDto group2 = new GroupCreateDto();
        group2.setName("name2");
        group2.setDescription("description2");

        //Arrange update group request
        GroupUpdateDto updateBody = new GroupUpdateDto();
        updateBody.setName("name2");
        updateBody.setDescription("updatedDescription");

        // Act - create valid group 1
        GroupDto createdGroup1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(group1)
                .when()
                .post("/api/v1/groups")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(GroupDto.class);

        // Act - create valid group 2
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(group2)
                .when()
                .post("/api/v1/groups")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Act - Update group1 with group2 name
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(updateBody)
                .when()
                .patch("/api/v1/groups/{uuid}", createdGroup1.getUuid());

        // Assert
        response.then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Delete Group - Valid UUID")
    void testDeleteGroup_whenValidUuidProvided_returnsNothing(){
        //Arrange groups
        GroupCreateDto group1 = new GroupCreateDto();
        group1.setName("name1");
        group1.setDescription("description1");

        // Act - create valid group 1
        GroupDto createdGroup1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(group1)
                .when()
                .post("/api/v1/groups")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(GroupDto.class);

        // Act - Delete group1
        given()
                .when()
                .delete("/api/v1/groups/{uuid}", createdGroup1.getUuid())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Assert - group1 is not found
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/groups/{uuid}", createdGroup1.getUuid())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Delete Group - Invalid UUID")
    void testDeleteGroup_whenInvalidUuidProvided_returnsGroupNotFoundException(){
        //Arrange groups
        UUID invalidUuid = UUID.randomUUID();

        // Act - Delete group1
        given()
                .when()
                .delete("/api/v1/groups/{uuid}", invalidUuid)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

}
