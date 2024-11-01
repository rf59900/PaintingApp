package com.ryan_frederick.painting.user;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRepositoryTest {
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

    // reset auto generating id and table between each test
    // create user before each test
    @BeforeEach
    void beforeEach() {
        jdbcClient.sql("TRUNCATE TABLE users RESTART IDENTITY CASCADE")
                .update();

        User userToAdd = new User(
                null,
                "Ryan",
                "Password",
                LocalDateTime.now(),
                3.5,
                "ROLE_USER",
                null
        );
        userRepository.createUser(userToAdd);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldCreateUser() {
        userRepository.createUser(new User(
                null,
               "Ryan",
                "Password",
                LocalDateTime.now(),
                3.5,
                "ROLE_USER",
                null
        ));
        List<User> users =  userRepository.findAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void shouldFindAllUsers() {
        List<User> users = userRepository.findAllUsers();
        assertEquals(1, users.size());
    }

    // should find user by id and found user id should match requested id
    @Test
    void shouldFindUserById() {
        Optional<User> foundUser = userRepository.findUserById(1);

        assertTrue(foundUser.isPresent());
        foundUser.ifPresent(user -> {
            assertEquals(1, user.id());
        });
    }

    @Test
    void shouldFindUserByUsername() {
        Optional<User> foundUser = userRepository.findUserByUsername("Ryan");

        assertTrue(foundUser.isPresent());
        foundUser.ifPresent(user -> {
            assertEquals("Ryan", user.username());
        });
    }

    @Test
    void shouldUpdateRating() {
        userRepository.updateUserAverageRating(1, 4);
        Optional<User> foundUser = userRepository.findUserById(1);
        assertTrue(foundUser.isPresent());
        foundUser.ifPresent(user -> {
            assertEquals(4, user.averageRating());
        });
    }

    @Test
    void shouldUpdatePassword() {
        userRepository.updateUserPassword(1, "New Password");
        Optional<User> foundUser = userRepository.findUserById(1);
        assertTrue(foundUser.isPresent());
        foundUser.ifPresent(user -> {
            assertEquals("New Password", user.password());
        });
    }

    @Test
    void shouldDeleteUser() {
        userRepository.deleteUserById(1);
        List<User> users = userRepository.findAllUsers();
        assertEquals(0, users.size());
    }

}