package com.sloyardms.trackerapi.controller;

import com.sloyardms.trackerapi.bookmark.BookmarkRepository;
import com.sloyardms.trackerapi.bookmark.dto.BookmarkCreateDto;
import com.sloyardms.trackerapi.bookmark.dto.BookmarkDto;
import com.sloyardms.trackerapi.bookmark.dto.BookmarkUpdateDto;
import com.sloyardms.trackerapi.group.dto.GroupCreateDto;
import com.sloyardms.trackerapi.group.dto.GroupDto;
import com.sloyardms.trackerapi.security.DevSecurityConfig;
import com.sloyardms.trackerapi.security.FakeAuthFilter;
import com.sloyardms.trackerapi.user.dto.UserCreateDto;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@Import(DevSecurityConfig.class)
public class BookmarkControllerIntegrationTests {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    //Needed for the tests
    private static GroupDto dbValidGroup;
    private static GroupDto dbValidGroup1;

    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.5"))
                    .withDatabaseName("tracker_db")
                    .withUsername("user")
                    .withPassword("password");

    @BeforeAll
    static void setupAll(@LocalServerPort int port){
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

        GroupCreateDto group = new GroupCreateDto();
        group.setName("validGroup");
        group.setDescription("validGroupDescription");

        dbValidGroup = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(group)
                .when()
                .post("/api/v1/groups")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(GroupDto.class);

        System.out.println("Created valid group: " + FakeAuthFilter.DEV_USER_UUID);

        GroupCreateDto group1 = new GroupCreateDto();
        group1.setName("validGroup1");
        group1.setDescription("validGroupDescription1");

        dbValidGroup1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(group1)
                .when()
                .post("/api/v1/groups")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(GroupDto.class);

        System.out.println("Created valid group1: " + FakeAuthFilter.DEV_USER_UUID);
    }

    @BeforeEach
    void beforeEach(){
        bookmarkRepository.deleteAll();
        System.out.println("Deleted all bookmarks");
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
                .get("/api/v1/bookmarks/ping")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("pong"));
    }

    @Test
    @DisplayName("Create Bookmark - Valid Bookmark")
    void testCreateBookmark_whenValidBookmarkProvided_returnsCreatedBookmark(){
        //Arrange bookmarkDto
        BookmarkCreateDto bookmarkCreateDto = new BookmarkCreateDto();
        bookmarkCreateDto.setTitle("title");
        bookmarkCreateDto.setDescription("description");
        bookmarkCreateDto.setUrl("url");
        bookmarkCreateDto.setFavorite(true);
        bookmarkCreateDto.setGroupUuid(dbValidGroup.getUuid());

        //Act
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto)
                .when()
                .post("/api/v1/bookmarks");

        //Assert
        response.then()
                .statusCode(HttpStatus.CREATED.value())
                .body("uuid", notNullValue())
                .body("title", equalTo(bookmarkCreateDto.getTitle()))
                .body("description", equalTo(bookmarkCreateDto.getDescription()))
                .body("url", equalTo(bookmarkCreateDto.getUrl()))
                .body("favorite", equalTo(bookmarkCreateDto.isFavorite()))
                .body("group.uuid", equalTo(bookmarkCreateDto.getGroupUuid().toString()));
    }

    @Test
    @DisplayName("Create Bookmark - Duplicated Title")
    void testCreateBookmark_whenValidDuplicatedTitleIsProvided_returnsBookmarkTitleAlreadyExistException(){
        //Arrange bookmarkDto
        BookmarkCreateDto bookmarkCreateDto1 = new BookmarkCreateDto();
        bookmarkCreateDto1.setTitle("title");
        bookmarkCreateDto1.setDescription("description");
        bookmarkCreateDto1.setUrl("url");
        bookmarkCreateDto1.setFavorite(true);
        bookmarkCreateDto1.setGroupUuid(dbValidGroup.getUuid());

        BookmarkCreateDto bookmarkCreateDto2 = new BookmarkCreateDto();
        bookmarkCreateDto2.setTitle("title");
        bookmarkCreateDto2.setDescription("description2");
        bookmarkCreateDto2.setUrl("url2");
        bookmarkCreateDto2.setFavorite(true);
        bookmarkCreateDto2.setGroupUuid(dbValidGroup.getUuid());

        //Act
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto1)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto2)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Create Bookmark - Duplicated URL")
    void testCreateBookmark_whenValidDuplicatedUrlIsProvided_returnsBookmarkUrlAlreadyExistException(){
        //Arrange bookmarkDto
        BookmarkCreateDto bookmarkCreateDto1 = new BookmarkCreateDto();
        bookmarkCreateDto1.setTitle("title");
        bookmarkCreateDto1.setDescription("description");
        bookmarkCreateDto1.setUrl("url");
        bookmarkCreateDto1.setFavorite(true);
        bookmarkCreateDto1.setGroupUuid(dbValidGroup.getUuid());

        BookmarkCreateDto bookmarkCreateDto2 = new BookmarkCreateDto();
        bookmarkCreateDto2.setTitle("title2");
        bookmarkCreateDto2.setDescription("description2");
        bookmarkCreateDto2.setUrl("url");
        bookmarkCreateDto2.setFavorite(true);
        bookmarkCreateDto2.setGroupUuid(dbValidGroup.getUuid());

        //Act
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto1)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto2)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Find Bookmark - Valid UUID")
    void testFindBookmark_whenValidUuidProvided_returnsFoundBookmark(){
        //Arrange bookmarkDto
        BookmarkCreateDto bookmarkCreateDto1 = new BookmarkCreateDto();
        bookmarkCreateDto1.setTitle("title");
        bookmarkCreateDto1.setDescription("description");
        bookmarkCreateDto1.setUrl("url");
        bookmarkCreateDto1.setFavorite(true);
        bookmarkCreateDto1.setGroupUuid(dbValidGroup.getUuid());

        //Act - create valid bookmark 1
        BookmarkDto savedBookmark = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto1)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BookmarkDto.class);

        // Act & Assert find bookmark by uuid
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/bookmarks/{id}", savedBookmark.getUuid())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("uuid", notNullValue())
                .body("title", equalTo(bookmarkCreateDto1.getTitle()))
                .body("description", equalTo(bookmarkCreateDto1.getDescription()))
                .body("url", equalTo(bookmarkCreateDto1.getUrl()))
                .body("favorite", equalTo(bookmarkCreateDto1.isFavorite()))
                .body("group.uuid", equalTo(bookmarkCreateDto1.getGroupUuid().toString()));
    }

    @Test
    @DisplayName("Find Bookmark - Valid UUID")
    void testFindBookmark_whenInvalidUuidProvided_returnsBookmarkNotFoundException(){
        //Arrange bookmarkDto
        UUID invalidUuid = UUID.randomUUID();

        // Act & Assert find bookmark by uuid
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/bookmarks/{id}", invalidUuid)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Find Bookmarks of User - Valid User UUID")
    void testFindBookmarksOfUser_whenValidUserProvided_returnsUserBookmarks(){
        //Arrange user
        UUID userId = FakeAuthFilter.DEV_USER_UUID;

        //Arrange bookmark
        BookmarkCreateDto bookmarkCreateDto1 = new BookmarkCreateDto();
        bookmarkCreateDto1.setTitle("title");
        bookmarkCreateDto1.setDescription("description");
        bookmarkCreateDto1.setUrl("url");
        bookmarkCreateDto1.setFavorite(true);
        bookmarkCreateDto1.setGroupUuid(dbValidGroup.getUuid());

        //Act - create valid bookmark 1
        BookmarkDto savedBookmark = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto1)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BookmarkDto.class);

        // Act
        Response response = given()
                .accept(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "title,asc")
                .when()
                .get("/api/v1/bookmarks/user/{uuid}", userId);

        // Assert
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].uuid", equalTo(savedBookmark.getUuid().toString()))
                .body("content[0].title", equalTo(savedBookmark.getTitle()))
                .body("content[0].description", equalTo(savedBookmark.getDescription()))
                .body("content[0].url", equalTo(savedBookmark.getUrl()))
                .body("content[0].favorite", equalTo(savedBookmark.isFavorite()))
                .body("content[0].group.uuid", equalTo(dbValidGroup.getUuid().toString()))
                .body("totalElements", equalTo(1))
                .body("totalPages", equalTo(1))
                .body("size", equalTo(10))
                .body("number", equalTo(0));
    }

    @Test
    @DisplayName("Find Bookmarks of User - User has no Bookmarks")
    void testFindBookmarksOfUser_whenUserHasNoBookmarks_returnsEmptyPage(){
        //Arrange user
        UUID userId = FakeAuthFilter.DEV_USER_UUID;

        // Act
        Response response = given()
                .accept(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "title,asc")
                .when()
                .get("/api/v1/bookmarks/user/{uuid}", userId);

        // Assert
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("totalElements", equalTo(0))
                .body("totalPages", equalTo(0))
                .body("size", equalTo(10))
                .body("number", equalTo(0));
    }

    @Test
    @DisplayName("Update Bookmark - Valid Body")
    void testUpdateBookmark_whenValidBodyProvided_returnsUpdatedBookmark(){
        //Arrange bookmarkDto
        BookmarkCreateDto bookmarkCreateDto1 = new BookmarkCreateDto();
        bookmarkCreateDto1.setTitle("title");
        bookmarkCreateDto1.setDescription("description");
        bookmarkCreateDto1.setUrl("url");
        bookmarkCreateDto1.setFavorite(true);
        bookmarkCreateDto1.setGroupUuid(dbValidGroup.getUuid());

        //Arrange update bookmark request
        BookmarkUpdateDto updateBody = new BookmarkUpdateDto();
        updateBody.setTitle("updatedTitle");
        updateBody.setDescription("updatedDescription");
        updateBody.setUrl("updatedUrl");
        updateBody.setFavorite(false);
        updateBody.setGroupUuid(dbValidGroup1.getUuid());

        //Act - create valid bookmark 1
        BookmarkDto savedBookmark = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto1)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BookmarkDto.class);

        // Act - Update bookmark 1
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(updateBody)
                .when()
                .patch("/api/v1/bookmarks/{uuid}", savedBookmark.getUuid());

        // Assert
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("uuid", equalTo(savedBookmark.getUuid().toString()))
                .body("title", equalTo(updateBody.getTitle()))
                .body("description", equalTo(updateBody.getDescription()))
                .body("favorite", equalTo(updateBody.getFavorite()))
                .body("url", equalTo(updateBody.getUrl()))
                .body("group.uuid", equalTo(updateBody.getGroupUuid().toString()));
    }

    @Test
    @DisplayName("Update Bookmark - Invalid Bookmark UUID")
    void testUpdateBookmark_whenInvalidBookmarkUuidProvided_returnsBookmarkNotFoundException(){
        //Arrange update bookmark request
        BookmarkUpdateDto updateBody = new BookmarkUpdateDto();
        updateBody.setTitle("updatedTitle");
        updateBody.setDescription("updatedDescription");
        updateBody.setUrl("updatedUrl");
        updateBody.setFavorite(false);
        updateBody.setGroupUuid(dbValidGroup1.getUuid());

        UUID bookmarkUUID = UUID.randomUUID();

        // Act - Update bookmark 1
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(updateBody)
                .when()
                .patch("/api/v1/bookmark/{uuid}", bookmarkUUID);

        // Assert
        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Update Bookmark - Duplicate Title")
    void testUpdateBookmark_whenTitleAlreadyExists_returnsBookmarkTitleAlreadyExistException(){
        //Arrange bookmarkDto
        BookmarkCreateDto bookmarkCreateDto1 = new BookmarkCreateDto();
        bookmarkCreateDto1.setTitle("title");
        bookmarkCreateDto1.setDescription("description");
        bookmarkCreateDto1.setUrl("url");
        bookmarkCreateDto1.setFavorite(true);
        bookmarkCreateDto1.setGroupUuid(dbValidGroup.getUuid());

        //Arrange bookmarkDto
        BookmarkCreateDto bookmarkCreateDto2 = new BookmarkCreateDto();
        bookmarkCreateDto2.setTitle("anotherTitle");
        bookmarkCreateDto2.setDescription("anotherDescription");
        bookmarkCreateDto2.setUrl("anotherUrl");
        bookmarkCreateDto2.setFavorite(true);
        bookmarkCreateDto2.setGroupUuid(dbValidGroup.getUuid());

        //Arrange update bookmark request
        BookmarkUpdateDto updateBody = new BookmarkUpdateDto();
        updateBody.setTitle("anotherTitle");
        updateBody.setDescription("updatedDescription");
        updateBody.setUrl("updatedUrl");
        updateBody.setFavorite(false);
        updateBody.setGroupUuid(dbValidGroup1.getUuid());

        //Act - create valid bookmark 1
        BookmarkDto savedBookmark1= given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto1)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BookmarkDto.class);

        //Act - create valid bookmark 2
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto2)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BookmarkDto.class);

        // Act - Update bookmark 1
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(updateBody)
                .when()
                .patch("/api/v1/bookmarks/{uuid}", savedBookmark1.getUuid());

        // Assert
        response.then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Update Bookmark - Duplicated URL")
    void testUpdateBookmark_whenUrlAlreadyExists_returnsBookmarkUrlAlreadyExistException(){
        //Arrange bookmarkDto
        BookmarkCreateDto bookmarkCreateDto1 = new BookmarkCreateDto();
        bookmarkCreateDto1.setTitle("title");
        bookmarkCreateDto1.setDescription("description");
        bookmarkCreateDto1.setUrl("url");
        bookmarkCreateDto1.setFavorite(true);
        bookmarkCreateDto1.setGroupUuid(dbValidGroup.getUuid());

        //Arrange bookmarkDto
        BookmarkCreateDto bookmarkCreateDto2 = new BookmarkCreateDto();
        bookmarkCreateDto2.setTitle("anotherTitle");
        bookmarkCreateDto2.setDescription("anotherDescription");
        bookmarkCreateDto2.setUrl("anotherUrl");
        bookmarkCreateDto2.setFavorite(true);
        bookmarkCreateDto2.setGroupUuid(dbValidGroup.getUuid());

        //Arrange update bookmark request
        BookmarkUpdateDto updateBody = new BookmarkUpdateDto();
        updateBody.setTitle("anotherTitle");
        updateBody.setDescription("updatedDescription");
        updateBody.setUrl("anotherUrl");
        updateBody.setFavorite(false);
        updateBody.setGroupUuid(dbValidGroup1.getUuid());

        //Act - create valid bookmark 1
        BookmarkDto savedBookmark1= given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto1)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BookmarkDto.class);

        //Act - create valid bookmark 2
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto2)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BookmarkDto.class);

        // Act - Update bookmark 1
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(updateBody)
                .when()
                .patch("/api/v1/bookmarks/{uuid}", savedBookmark1.getUuid());

        // Assert
        // Assert
        response.then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Update Bookmark - Invalid Group")
    void testUpdateBookmark_whenInvalidGroup_returnsGroupNotFoundException(){
        //Arrange bookmarkDto
        BookmarkCreateDto bookmarkCreateDto1 = new BookmarkCreateDto();
        bookmarkCreateDto1.setTitle("title");
        bookmarkCreateDto1.setDescription("description");
        bookmarkCreateDto1.setUrl("url");
        bookmarkCreateDto1.setFavorite(true);
        bookmarkCreateDto1.setGroupUuid(dbValidGroup.getUuid());

        //Arrange bookmarkDto
        BookmarkCreateDto bookmarkCreateDto2 = new BookmarkCreateDto();
        bookmarkCreateDto2.setTitle("anotherTitle");
        bookmarkCreateDto2.setDescription("anotherDescription");
        bookmarkCreateDto2.setUrl("anotherUrl");
        bookmarkCreateDto2.setFavorite(true);
        bookmarkCreateDto2.setGroupUuid(dbValidGroup.getUuid());

        //Arrange update bookmark request
        BookmarkUpdateDto updateBody = new BookmarkUpdateDto();
        updateBody.setTitle("updatedTitle");
        updateBody.setDescription("updatedDescription");
        updateBody.setUrl("anotherDescription");
        updateBody.setFavorite(false);
        updateBody.setGroupUuid(UUID.randomUUID());

        //Act - create valid bookmark 1
        BookmarkDto savedBookmark1= given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto1)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BookmarkDto.class);

        //Act - create valid bookmark 2
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto2)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BookmarkDto.class);

        // Act - Update bookmark 1
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(updateBody)
                .when()
                .patch("/api/v1/bookmarks/{uuid}", savedBookmark1.getUuid());

        // Assert
        // Assert
        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Delete Bookmark - Valid Bookmark UUID")
    void testDeleteBookmark_whenValidBookmarkUuidProvided_returnsNothing(){
        //Arrange bookmarkDto
        BookmarkCreateDto bookmarkCreateDto1 = new BookmarkCreateDto();
        bookmarkCreateDto1.setTitle("title");
        bookmarkCreateDto1.setDescription("description");
        bookmarkCreateDto1.setUrl("url");
        bookmarkCreateDto1.setFavorite(true);
        bookmarkCreateDto1.setGroupUuid(dbValidGroup.getUuid());

        //Act - create valid bookmark 1
        BookmarkDto savedBookmark1= given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookmarkCreateDto1)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BookmarkDto.class);

        // Act - Delete bookmark 1
        given()
                .when()
                .delete("/api/v1/bookmarks/{uuid}", savedBookmark1.getUuid())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Assert - bookmark 1 is not found
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/bookmarks/{uuid}", savedBookmark1.getUuid())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Delete Bookmark - Invalid Bookmark UUID")
    void testDeleteBookmark_whenInvalidBookmarkUuidProvided_returnsBookmarkNotFoundException(){
        // Arrange bookmarkDto
        UUID invalidUuid = UUID.randomUUID();

        // Act - Delete bookmark 1
        given()
                .when()
                .delete("/api/v1/bookmarks/{uuid}", invalidUuid)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

}
