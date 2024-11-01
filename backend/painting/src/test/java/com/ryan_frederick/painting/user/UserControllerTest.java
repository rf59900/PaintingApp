package com.ryan_frederick.painting.user;

import com.ryan_frederick.painting.auth.AuthTokenResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    JdbcClient jdbcClient;

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
    void beforeEach() {
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
    void shouldFindAllUsers() {
        get("/users")
                .then()
                .statusCode(200);
    }

    @Test
    void shouldFindUserById() {
       get("users/id/1")
               .then()
               .statusCode(200);
    }

    @Test
    void shouldFindUserByUsername() {
        get("users/username/Ryan")
                .then()
                .statusCode(200);
    }

    @Test
    void shouldCreateUser() {
        CreateUserRequest user = new CreateUserRequest(
                "Bob",
                "Password123"
        );
        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users")
                .then()
                .statusCode(201);
    }

    @Test
    void shouldDeleteUser() {
        Response response = given()
                .auth().basic("Ryan", "Password")
                .post("/login");

        String authToken = response.getBody().as(AuthTokenResponse.class).jwt();
        String refreshToken = response.getCookie("jwt");

        given()
        .cookie("jwt", refreshToken)
        .header("Authorization", "Bearer " + authToken)
        .delete("/users/1")
        .then()
        .statusCode(200);
    }

    @Test
    void shouldUpdateUserPassword() {
        UserPasswordUpdate update = new UserPasswordUpdate(
                1,
                "New Password"
        );

        given()
                .contentType(ContentType.JSON)
                .body(update)
                .when()
                .patch("/users")
                .then()
                .statusCode(200);
    }
}