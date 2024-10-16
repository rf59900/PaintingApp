package com.ryan_frederick.painting.user;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {
    List<User> users = new ArrayList<User>();

    List<User> findAll() {
        return users;
    }

    @PostConstruct
    private void init() {
        users.add(new User(
                "Bob",
                "password123",
                LocalDateTime.now(),
                List.of(123, 456, 789),
                3.5
        ));
        users.add(new User(
                "Same",
                "password456",
                LocalDateTime.now(),
                List.of(123, 456, 789),
                4.7
        ));
    }
}
