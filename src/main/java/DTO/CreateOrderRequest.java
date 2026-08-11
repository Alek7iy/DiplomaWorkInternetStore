package DTO;

import java.util.List;

public record CreateOrderRequest(
        List<OrderItemCreateRequest> items
) {}