package DTO;

import entity.Category;

public record CategoryRequest(
        String name,
        String description,
        Category.Status status
) {}
