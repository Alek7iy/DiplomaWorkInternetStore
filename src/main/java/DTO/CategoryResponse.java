package DTO;

import entity.Category;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        Category.Status status
) {}

