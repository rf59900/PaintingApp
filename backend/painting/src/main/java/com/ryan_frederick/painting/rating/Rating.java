package com.ryan_frederick.painting.rating;

public record Rating(
        Integer painting,
        Integer rater,
        double rating
) {}
