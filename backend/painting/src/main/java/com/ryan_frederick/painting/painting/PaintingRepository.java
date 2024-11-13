package com.ryan_frederick.painting.painting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PaintingRepository {
    Logger logger = LogManager.getLogger(PaintingRepository.class);
    @Autowired
    private final JdbcClient jdbcClient;

    public PaintingRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    List<Painting> findAllPaintings() {
        return jdbcClient.sql("SELECT * FROM painting")
                .query(Painting.class)
                .list();
    }

    public void createPainting(Painting painting, Integer userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql("INSERT INTO painting(title, description, created, rating, image_name) values(?, ?, ?, ?, ?)")
                .params(List.of(painting.title(), painting.description(), painting.created(), painting.rating(), painting.imageName()))
                .update(keyHolder);

        Integer paintingId = (Integer) keyHolder.getKeys().get("id");


        jdbcClient.sql("INSERT INTO painted(painter, painting) values(?, ?)")
                .params(List.of(userId, paintingId))
                .update();

    }

    void deletePainting(Integer id) {

        // delete painted record before deleting painting record due to foreign key constaint
        jdbcClient.sql("DELETE FROM painted WHERE painting = :id")
                .param("id", id)
                .update();

        jdbcClient.sql("DELETE FROM painting WHERE ID = :id")
                .param("id", id)
                .update();
    }

    Optional<Painting> findPaintingById(Integer id) {
        return jdbcClient.sql("SELECT * FROM painting WHERE id = :id")
                .param("id", id)
                .query(Painting.class)
                .optional();
    }

    void updatePaintingRating(Integer paintingId, double newRating) {
        int updated = jdbcClient.sql("UPDATE painting SET rating = :newRating WHERE id = :paintingId")
                .param("newRating", newRating)
                .param("paintingId", paintingId)
                .update();
    }

    List<Painting> findAllPaintingsByUser(Integer id) {
        return jdbcClient.sql("SELECT * FROM painting WHERE id IN (" +
                "SELECT painting FROM painted WHERE painter = :id)")
                .param("id", id)
                .query(Painting.class)
                .list();
    }

    List<Painting> findAllPaintingsTopRated() {
        return jdbcClient.sql("SELECT * FROM painting ORDER BY rating DESC")
                .query(Painting.class)
                .list();
    }

}
