package com.ryan_frederick.painting.auth;

import com.ryan_frederick.painting.user.CreateUserRequest;
import com.ryan_frederick.painting.user.User;
import com.ryan_frederick.painting.user.UserRepository;
import io.restassured.RestAssured;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

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
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

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
        jdbcClient.sql("TRUNCATE TABLE users RESTART IDENTITY CASCADE")
                .update();

        RestAssured.port = port;

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
    void shouldFailToLogout() {
        get("/logout")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldFailToGetRefreshToken() {
        get("/refresh")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldFailToLogin() {
        given().auth().preemptive().basic("Greg", "Heffley")
                .post("/login")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldLogin() {
        given().auth().preemptive().basic("Ryan", "Password")
                .post("/login")
                .then()
                .statusCode(200)
                .body("jwt", notNullValue())
                .cookie("jwt", notNullValue())
                .log().all();
    }

    @Test
    void shouldGetRefreshToken() {
       Response response = given()
               .auth().basic("Ryan", "Password")
               .post("/login");

       String authToken = response.getBody().as(AuthTokenResponse.class).jwt();
       String refreshToken = response.getCookie("jwt");

        given()
                .cookie("jwt", refreshToken)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/refresh")
                .then()
                .statusCode(200)
                .body("jwt", notNullValue())
                .cookie("jwt", notNullValue())
                .log()
                .all();
    }

    @Test
    void shouldLogout() {
        Response response = given()
                .auth().basic("Ryan", "Password")
                .post("/login");

        String authToken = response.getBody().as(AuthTokenResponse.class).jwt();
        String refreshToken = response.getCookie("jwt");

        given()
                .cookie("jwt", refreshToken)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/logout")
                .then()
                .statusCode(200)
                .body("$", not(hasKey("jwt")))
                .cookie("jwt", equalTo(""))
                .log()
                .all();
    }
}

