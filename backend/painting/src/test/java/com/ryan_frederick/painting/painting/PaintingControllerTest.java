package com.ryan_frederick.painting.painting;

import com.ryan_frederick.painting.auth.AuthTokenResponse;
import com.ryan_frederick.painting.user.CreateUserRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaintingControllerTest {
    Logger logger = LogManager.getLogger(PaintingControllerTest.class);
    @Autowired
    JdbcClient jdbcClient;

    @Autowired
    PaintingRepository paintingRepository;

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();

    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    // reset auto generating id and table between each test
    // create user before each test
    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        RestAssured.port = port;
        jdbcClient.sql("TRUNCATE TABLE users RESTART IDENTITY CASCADE")
                .update();

        CreateUserRequest userToAdd = new CreateUserRequest(
                "Ryan",
                "Password"
        );
        given()
                .contentType(ContentType.JSON)
                .body(userToAdd)
                .when()
                .post("/users");
    }

    @Test
    void shouldCreatePainting() {
        CreatePaintingRequest createPaintingRequest = new CreatePaintingRequest(
                "The Scream",
                "A man standing on a bridge hollering."
        );

        Response response = given().auth().basic("Ryan", "Password")
                .post("/login");

        String authToken = response.getBody().as(AuthTokenResponse.class).jwt();

        given().header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(createPaintingRequest)
                .when()
                .post("/painting")
                .then()
                .statusCode(200)
                .log()
                .all();
    }

    @Nested
    class RequiresPainting {
        @BeforeEach
        void beforeEach() {
            Painting paintingToAdd = new Painting(
                    null,
                    "Starry Night",
                    "A remake of a van gogh classic.",
                    LocalDateTime.now(),
                    5,
                    "placeholder.jpg"
            );
            paintingRepository.createPainting(paintingToAdd, 1);
        }

        @Test
        void shouldRatePainting() {
            Response response = given().auth().basic("Ryan", "Password")
                    .post("/login");

            String authToken = response.getBody().as(AuthTokenResponse.class).jwt();

            given().header("Authorization", "Bearer " + authToken)
                    .contentType(ContentType.JSON)
                    .body(new PaintingRatingRequest(1, 1))
                    .post("/painting/rate")
                    .then()
                    .statusCode(201);


        }

        @Test
        void shouldRatePaintingTwice() {

        }
        @Test
        void shouldFindAllPaintingsByUser() {
            Response response = get("/painting/user/1")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            Painting[] paintings = response.getBody().as(Painting[].class);

            assertEquals(5, paintings[0].rating());

        }
    }


}