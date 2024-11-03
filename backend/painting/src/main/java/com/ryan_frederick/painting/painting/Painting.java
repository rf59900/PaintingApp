package com.ryan_frederick.painting.painting;

import java.time.LocalDateTime;

public record Painting(
    Integer id,
    String title,
    String description,
    LocalDateTime created,
    double rating,
    String imageName
) {}
