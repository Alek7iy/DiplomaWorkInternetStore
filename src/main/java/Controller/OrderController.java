package Controller;

import DTO.AddToOrderRequest;
import DTO.CreateOrderRequest;
import DTO.OrderResponse;
import Service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Управление заказами")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Создать заказ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заказ успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь или товар не найден")
    })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-User-ID") Long userId,
            @RequestBody @Valid CreateOrderRequest request
    ) {
        OrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Удалить товар из заказа")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Товар удалён из заказа"),
            @ApiResponse(responseCode = "404", description = "Заказ или товар не найден"),
            @ApiResponse(responseCode = "403", description = "Нельзя удалить товар из чужого заказа")
    })
    public ResponseEntity<Void> removeItemFromOrder(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestHeader("X-User-ID") Long userId
    ) {
        orderService.removeItemFromOrder(orderId, itemId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{orderId}/items")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Добавить товар в заказ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар добавлен в заказ"),
            @ApiResponse(responseCode = "400", description = "Недостаточно товара на складе"),
            @ApiResponse(responseCode = "403", description = "Нельзя изменить чужой заказ")
    })
    ResponseEntity<OrderResponse> addItemToOrder(
            @PathVariable Long orderId,
            @RequestHeader("X-User-ID") Long userId,
            @RequestBody @Valid AddToOrderRequest request
    ) {
        OrderResponse response = orderService.addItemToOrder(orderId, userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Отменить заказ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Заказ отменён"),
            @ApiResponse(responseCode = "403", description = "Нельзя отменить чужой заказ"),
            @ApiResponse(responseCode = "400", description = "Заказ нельзя отменить в текущем статусе")
    })
    ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader("X-User-ID") Long userId
    ) {
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }
}