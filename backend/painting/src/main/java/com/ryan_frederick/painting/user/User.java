package com.ryan_frederick.painting.user;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public record User(
        String username,
        String password,
        LocalDateTime joined,
        List<Integer> paintings,
        double averageRating
) {}

