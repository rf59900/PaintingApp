package com.ryan_frederick.painting.painting;

import com.ryan_frederick.painting.user.User;
import com.ryan_frederick.painting.user.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaintingRepositoryTest {
    @Autowired
    JdbcClient jdbcClient;

    @Autowired
    PaintingRepository paintingRepository;

    @Autowired
    UserRepository userRepository;

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
    // create painting before each test
    @BeforeEach
    void beforeEach() {
        jdbcClient.sql("TRUNCATE TABLE painting RESTART IDENTITY CASCADE")
                .update();

        userRepository.createUser(new User(
                null,
                "Ryan",
                "Password",
                LocalDateTime.now(),
                5,
                "ROLE_USER",
                null
        ));

        Painting paintingToAdd = new Painting(
                null,
                "The Mona Lisa",
                "A smiling woman somewhere in italy.",
                LocalDateTime.now(),
                5,
                "image.jpg"
        );

        paintingRepository.createPainting(paintingToAdd, 1);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldFindAllPaintings() {
        List<Painting> paintings = paintingRepository.findAllPaintings();

        assertEquals(1, paintings.size());
    }

    @Test
    void shouldCreatePainting() {
        Painting paintingToAdd = new Painting(
                null,
                "Starry Night",
                "A remake of a van gogh classic.",
                LocalDateTime.now(),
                4,
                "placeholder.jpg"
        );

        paintingRepository.createPainting(paintingToAdd, 1);

        assertEquals(2, paintingRepository.findAllPaintings().size());
    }

    @Test
    void shouldDeletePainting() {
        paintingRepository.deletePainting(1);

        assertEquals(0, paintingRepository.findAllPaintings().size());
    }

    @Test
    void shouldUpdatePaintingRating() {
        paintingRepository.updatePaintingRating(new PaintingRatingUpdate(
                1,
                4
        ));

        Double rating = paintingRepository.findPaintingById(1).map(Painting::rating).orElse(null);

        assertEquals(4, rating);
    }

    @Test
    void shouldGetPaintingById() {
        Optional<Painting> foundPainting = paintingRepository.findPaintingById(1);

        assertTrue(foundPainting.isPresent());
        foundPainting.ifPresent(user -> {
            assertEquals(1, user.id());
        });
    }

    @Test
    void shouldFindAllPaintingsFromUser() {
        List<Painting> paintings = paintingRepository.findAllPaintingsByUser(1);
        assertEquals(1, paintings.size());
        assertEquals("The Mona Lisa", paintings.getFirst().title());
    }
}