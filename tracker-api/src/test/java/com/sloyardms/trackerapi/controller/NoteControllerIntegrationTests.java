package com.sloyardms.trackerapi.controller;

import com.sloyardms.trackerapi.bookmark.dto.BookmarkCreateDto;
import com.sloyardms.trackerapi.bookmark.dto.BookmarkDto;
import com.sloyardms.trackerapi.common.service.ImageStorageService;
import com.sloyardms.trackerapi.note.NoteRepository;
import com.sloyardms.trackerapi.note.dto.NoteCreateDto;
import com.sloyardms.trackerapi.note.dto.NoteDto;
import com.sloyardms.trackerapi.note.dto.NoteUpdateDto;
import com.sloyardms.trackerapi.note_image.dto.NoteImageDto;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@Import(DevSecurityConfig.class)
public class NoteControllerIntegrationTests {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    //Need for the tests
    private static UUID validBookmarkUuid;

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

        // Create valid User

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

        // Create valid Bookmark (without Group)

        BookmarkCreateDto bookmarkCreateDto = new BookmarkCreateDto();
        bookmarkCreateDto.setTitle("title");
        bookmarkCreateDto.setDescription("description");
        bookmarkCreateDto.setUrl("url");
        bookmarkCreateDto.setFavorite(true);

        BookmarkDto savedBookmark = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/v1/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BookmarkDto.class);

        validBookmarkUuid = savedBookmark.getUuid();
    }

    @BeforeEach
    void beforeEach(){
        noteRepository.deleteAll();
        System.out.println("Deleted all Notes");
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
                .get("/api/v1/notes/ping")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("pong"));
    }

    @Test
    @DisplayName("Create Note - Valid Body")
    void testCreateNote_whenValidBodyProvided_returnsCreatedNote(){
        //Arrange note
        NoteCreateDto createNoteDto = new NoteCreateDto();
        createNoteDto.setNote("my note");

        //Act - save note in valid bookmark
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createNoteDto)
                .when()
                .post("/api/v1/bookmarks/" + validBookmarkUuid + "/notes");

        //Assert
        response.then()
                .statusCode(HttpStatus.CREATED.value())
                .body("uuid", notNullValue())
                .body("note", equalTo(createNoteDto.getNote()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    @DisplayName("Create Note - Blank Note")
    void testCreateNote_whenBlankNoteProvided_returnsBadRequest(){
        //Arrange note
        NoteCreateDto createNoteDto = new NoteCreateDto();

        //Act - try to save note with blank note
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createNoteDto)
                .when()
                .post("/api/v1/bookmarks/" + validBookmarkUuid + "/notes");

        //Assert
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Create Note - Invalid Bookmark")
    void testCreateNote_whenInvalidBookmarkProvided_returnsBookmarkNotFound(){
        //Arrange note
        NoteCreateDto createNoteDto = new NoteCreateDto();
        createNoteDto.setNote("my note");

        //Arrange invalid bookmark uuid
        UUID invalidBookmarkUuid = UUID.randomUUID();

        //Act - try to save note with invalid bookmark
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createNoteDto)
                .when()
                .post("/api/v1/bookmarks/" + invalidBookmarkUuid + "/notes");

        //Assert
        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Find Note - Valid UUID")
    void testFindNote_whenValidUuidProvided_returnsNote(){
        //Arrange note
        NoteCreateDto createNoteDto = new NoteCreateDto();
        createNoteDto.setNote("my note");

        //Act - save note in valid bookmark
        NoteDto savedNote = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createNoteDto)
                .when()
                .post("/api/v1/bookmarks/" + validBookmarkUuid + "/notes")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(NoteDto.class);

        //Act - find note by uuid
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/notes/{uuid}", savedNote.getUuid());

        //Assert
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("uuid", equalTo(savedNote.getUuid().toString()))
                .body("note", equalTo(savedNote.getNote()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    @DisplayName("Find Note - Invalid UUID")
    void testFindNote_whenInvalidUuidProvided_returnsNoteNotFound(){
        //Arrange invalid uuid
        UUID invalidUuid = UUID.randomUUID();

        //Act - find note by uuid
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/notes/{uuid}", invalidUuid);

        //Assert
        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Find Notes of Bookmark - Valid Bookmark UUID")
    void testFindNotesByBookmark_whenValidBookmarkUuidProvided_returnsPageOfNotes(){
        //Arrange note
        NoteCreateDto createNoteDto = new NoteCreateDto();
        createNoteDto.setNote("my note");

        //Act - save note in valid bookmark
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createNoteDto)
                .when()
                .post("/api/v1/bookmarks/" + validBookmarkUuid + "/notes")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        //Act - find notes by bookmark uuid
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/bookmarks/{uuid}/notes", validBookmarkUuid);

        //Assert
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].uuid", notNullValue())
                .body("content[0].note", equalTo(createNoteDto.getNote()))
                .body("content[0].createdAt", notNullValue())
                .body("content[0].updatedAt", notNullValue())
                .body("totalElements", equalTo(1))
                .body("totalPages", equalTo(1))
                .body("size", equalTo(10))
                .body("number", equalTo(0));

    }

    @Test
    @DisplayName("Find Notes of Bookmark - Invalid Bookmark UUID")
    void testFindNotesByBookmark_whenInvalidBookmarkUuidProvided_returnsBookmarkNotFound(){
        //Arrange note
        UUID invalidBookmarkUuid = UUID.randomUUID();

        //Act - find notes by bookmark uuid
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/bookmarks/{uuid}/notes", invalidBookmarkUuid);

        //Assert
        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Update Note - Valid Body")
    void testUpdateNote_whenValidBodyProvided_returnsUpdatedNote(){
        //Arrange note
        NoteCreateDto createNoteDto = new NoteCreateDto();
        createNoteDto.setNote("my note");

        //Arrange update body
        NoteUpdateDto updateBody = new NoteUpdateDto();
        updateBody.setNote("updated note");

        //Act - save note first
        NoteDto savedNote = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createNoteDto)
                .when()
                .post("/api/v1/bookmarks/" + validBookmarkUuid + "/notes")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(NoteDto.class);

        //Act - update note
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(updateBody)
                .when()
                .patch("/api/v1/notes/{uuid}", savedNote.getUuid());

        //Assert
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("uuid", equalTo(savedNote.getUuid().toString()))
                .body("note", equalTo(updateBody.getNote()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    @DisplayName("Update Note - Blank Note")
    void testUpdateNote_whenBlankNoteProvided_returnsBadRequest(){
        //Arrange note
        NoteCreateDto createNoteDto = new NoteCreateDto();
        createNoteDto.setNote("my note");

        //Arrange update body
        NoteUpdateDto updateBody = new NoteUpdateDto();

        //Act - save note first
        NoteDto savedNote = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createNoteDto)
                .when()
                .post("/api/v1/bookmarks/" + validBookmarkUuid + "/notes")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(NoteDto.class);

        //Act - update note
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(updateBody)
                .when()
                .patch("/api/v1/notes/{uuid}", savedNote.getUuid());

        //Assert
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Update Note - Blank Note")
    void testUpdateNote_whenInvalidUuidProvided_returnsNoteNotFound(){
        //Arrange update body
        NoteUpdateDto updateBody = new NoteUpdateDto();
        updateBody.setNote("updated note");

        //Arrange invalid uuid
        UUID invalidUuid = UUID.randomUUID();

        //Act - update note
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(updateBody)
                .when()
                .patch("/api/v1/notes/{uuid}", invalidUuid);

        //Assert
        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Delete Note - Valid UUID")
    void testDeleteNote_whenValidUuidProvided_returnsNothing(){
        //Arrange note
        NoteCreateDto createNoteDto = new NoteCreateDto();
        createNoteDto.setNote("my note");

        //Act - save note in valid bookmark
        NoteDto savedNote = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createNoteDto)
                .when()
                .post("/api/v1/bookmarks/" + validBookmarkUuid + "/notes")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(NoteDto.class);

        //Act - delete note by uuid
        given()
                .accept(ContentType.JSON)
                .when()
                .delete("/api/v1/notes/{uuid}", savedNote.getUuid())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        //Act - find note by uuid
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/notes/{uuid}", savedNote.getUuid());

        //Assert
        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Delete Note - Invalid UUID")
    void testDeleteNote_whenInvalidUuidProvided_returnsNoteNotFound(){
        //Arrange invalid UUID
        UUID invalidUuid = UUID.randomUUID();

        //Act - delete note by uuid
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .delete("/api/v1/notes/{uuid}", invalidUuid);

        //Assert
        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Create NoteImage - Valid Body")
    void testCreateNoteImage_whenValidBodyAndFileProvided_returnsCreatedNoteImage() {
        //Arrange note
        NoteCreateDto createNoteDto = new NoteCreateDto();
        createNoteDto.setNote("my note");

        //Arrange image file
        File imageFile = new File(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource("test-image.jpg")
                ).getFile()
        );

        //Act - save note in valid bookmark
        NoteDto savedNote = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createNoteDto)
                .when()
                .post("/api/v1/bookmarks/" + validBookmarkUuid + "/notes")
                .then()
                .extract()
                .as(NoteDto.class);

        //Act - upload image
        Response response = given()
                .multiPart("file", imageFile)
                .accept(ContentType.JSON)
                .when()
                .post("/api/v1/notes/{uuid}/images", savedNote.getUuid());

        //Assert - NoteImage created
        response.then()
                .statusCode(HttpStatus.CREATED.value())
                .body("uuid", notNullValue())
                .body("thumbnailPath", notNullValue())
                .body("thumbnailMimeType", notNullValue())
                .body("originalImageMimeType", notNullValue())
                .body("originalImagePath", notNullValue());

        //Cleanup - delete the created note files
        given()
                .when()
                .delete("/api/v1/notes/{uuid}", savedNote.getUuid())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

}
