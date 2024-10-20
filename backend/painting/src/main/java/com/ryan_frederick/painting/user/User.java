package com.ryan_frederick.painting.user;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public record User(
        Integer id,
        String username,
        String password,
        LocalDateTime joined,
        double averageRating
) {}

