package com.ryan_frederick.painting.user;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

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

    public Optional<User> findUserByUsername(String username) {
        return jdbcClient.sql("SELECT * FROM users WHERE username = :username")
                .param("username", username)
                .query(User.class)
                .optional();
    }

    // TODO: find all users sort by highest average rating
    public List<User> findAllUsersByRating() {
        return jdbcClient.sql("SELECT * FROM users ORDER BY average_rating DESC")
                .query(User.class)
                .list();
    }



    // TODO: find users by newest joined
    // TODO: find users by oldest joined

    public void createUser(User user) {
        int created = jdbcClient.sql("INSERT INTO users(username, password, joined, average_rating, roles) values(?, ?, ?, ?, ?)")
                .params(List.of(user.username(), user.password(), user.joined(), user.averageRating(), user.roles()))
                .update();
    }

    void updateUserAverageRating(Integer id, double newRating) {
        int updated = jdbcClient.sql("UPDATE users SET average_rating = :newRating WHERE id = :id")
                .param("newRating", newRating)
                .param("id", id)
                .update();

    }

    void updateUserPassword(Integer id, String newPassword) {
        int updated = jdbcClient.sql("UPDATE users SET password = :newPassword WHERE id = :id")
                .param("newPassword", newPassword)
                .param("id", id)
                .update();
    }

    public void updateUserRefreshToken(String username, String newRefreshToken) {
        int updated = jdbcClient.sql("UPDATE users SET refresh_token = :newRefreshToken WHERE username = :username")
                .param("newRefreshToken", newRefreshToken)
                .param("username", username)
                .update();
    }


    void deleteUserById(Integer id) {
        int deleted = jdbcClient.sql("DELETE FROM users WHERE id = :id")
                .param("id", id)
                .update();
    }
}
