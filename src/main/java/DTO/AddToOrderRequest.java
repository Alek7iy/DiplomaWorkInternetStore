package DTO;

public record AddToOrderRequest(
        Long productId,
        Integer quantity
) {}
