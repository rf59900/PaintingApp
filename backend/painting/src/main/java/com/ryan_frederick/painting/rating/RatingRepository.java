package com.ryan_frederick.painting.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RatingRepository {

    @Autowired
    private final JdbcClient jdbcClient;

    public RatingRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void createRating(Integer paintingId, Integer userId, double rating) {
        jdbcClient.sql("INSERT INTO rating(painting, rater, rating) values(?, ?, ?)")
                .params(List.of(paintingId, userId, rating))
                .update();
    }

    public void updateRating(Integer paintingId, Integer userId, double newRating) {
        jdbcClient.sql("UPDATE rating SET rating = :newRating WHERE painting = :paintingId AND rater = :userId")
                .param("newRating", newRating)
                .param("paintingId", paintingId)
                .param("userId", userId)
                .update();
    }

    List<Rating> findAllRatings() {
        return jdbcClient.sql("SELECT * FROM rating")
                .query(Rating.class)
                .list();
    }

    public List<Rating> findAllRatingsForPainting(Integer paintingId) {
        return jdbcClient.sql("SELECT * FROM rating WHERE painting = :paintingId")
                .param("paintingId", paintingId)
                .query(Rating.class)
                .list();
    }

    public Optional<Rating> findRating(Integer paintingId, Integer userId) {
        return jdbcClient.sql("SELECT * FROM rating WHERE painting = :paintingId AND rater = :userId")
                .param("paintingId", paintingId)
                .param("userId", userId)
                .query(Rating.class)
                .optional();
    }
}
