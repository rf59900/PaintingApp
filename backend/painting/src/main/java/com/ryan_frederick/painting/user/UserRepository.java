package com.ryan_frederick.painting.user;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    @Autowired
    private final JdbcClient jdbcClient;

    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    List<User> findAllUsers() {
        return jdbcClient.sql("SELECT * FROM users")
                .query(User.class)
                .list();
    }

    Optional<User> findUserById(Integer id) {
        return jdbcClient.sql("SELECT * FROM users WHERE id = :id")
                .param("id", id)
                .query(User.class)
                .optional();
    }

    void createUser(User user) {
        int update = jdbcClient.sql("INSERT INTO users(username, password, joined, average_rating) values(?, ?, ?, ?)")
                .params(List.of(user.username(), user.password(), user.joined(), user.averageRating()))
                .update();
    }
}
