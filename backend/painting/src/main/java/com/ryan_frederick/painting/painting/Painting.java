package com.ryan_frederick.painting.painting;

import java.time.LocalDateTime;

public record Painting(
    String title,
    String description,
    LocalDateTime created,
    double rating,
    String imageName
) {}
