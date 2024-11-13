package com.ryan_frederick.painting.rating;

import com.ryan_frederick.painting.painting.Painting;
import com.ryan_frederick.painting.painting.PaintingRepository;

import com.ryan_frederick.painting.user.User;
import com.ryan_frederick.painting.user.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RatingRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(RatingRepositoryTest.class);
    @Autowired
    JdbcClient jdbcClient;

    @Autowired
    PaintingRepository paintingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RatingRepository ratingRepository;

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

        ratingRepository.createRating(1, 1, 5);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldCreateRating() {
        ratingRepository.createRating(1, 1, 4);

        List<Rating> ratings = ratingRepository.findAllRatings();

        assertEquals(2, ratings.size());
    }

    @Test
    void shouldFindAllRatings() {
        List<Rating> ratings = ratingRepository.findAllRatings();

        assertEquals(1, ratings.size());
    }

    @Test
    void shouldFindAllRatingsForPainting() {
        List<Rating> ratings = ratingRepository.findAllRatingsForPainting(1);

        assertEquals(1, ratings.size());
    }

    @Test
    void shouldFindRating() {
        Optional<Rating> rating = ratingRepository.findRating(1, 1);

        assertTrue(rating.isPresent());

        Integer paintingId = rating.map(Rating::painting).orElse(null);
        Integer userId = rating.map(Rating::rater).orElse(null);

        assertEquals(1, paintingId);
        assertEquals(1, userId);
    }

    @Test
    void shouldFailToFindRating() {
        Optional<Rating> rating = ratingRepository.findRating(2, 2);

        assertFalse(rating.isPresent());
    }
    @Test
    void shouldUpdateRating() {
        ratingRepository.updateRating(1, 1, 1);

        double ratingValue = ratingRepository.findRating(1, 1).map(Rating::rating).orElse(null);

        assertEquals(ratingValue, 1);
    }
}