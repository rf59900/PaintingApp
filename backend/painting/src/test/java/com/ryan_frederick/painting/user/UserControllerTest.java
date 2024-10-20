package com.ryan_frederick.painting.user;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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

        User userToAdd = new User(
                null,
                "Ryan",
                "Password",
                LocalDateTime.now(),
                3.5
        );
        userRepository.createUser(userToAdd);
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
        User user = new User(
                null,
                "Bob",
                "Password123",
                LocalDateTime.now(),
                2.4
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
        delete("/users/1")
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