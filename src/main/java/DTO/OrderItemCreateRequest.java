package DTO;

public record OrderItemCreateRequest(
        Long productId,
        Integer quantity
) {}