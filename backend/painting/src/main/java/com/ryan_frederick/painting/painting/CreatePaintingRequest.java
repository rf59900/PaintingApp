package com.ryan_frederick.painting.painting;

public record CreatePaintingRequest(
    String title,
    String description,
    String dataUrl
) {}
