package Service;

import DTO.AddToOrderRequest;
import DTO.CreateOrderRequest;
import DTO.OrderItemResponse;
import DTO.OrderResponse;
import Repository.OrderRepository;
import Repository.ProductRepository;
import Repository.UserRepository;
import Transfers.OrderStatus;
import entity.Order;
import entity.OrderItem;
import entity.Product;
import entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import Exception.ResourceNotFoundException;
import Exception.BusinessException;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Order createOrder(Order order){
        order.setStatus(OrderStatus.CREATED.name());
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPriceAtMoment()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                itemResponses
        );
    }

    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<OrderItem> orderItems = request.items().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.productId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.productId()));
                    if (product.getStockQuantity() < item.quantity()) {
                        throw new BusinessException("Not enough stock for product: " + product.getName());
                    }
                    product.setStockQuantity(product.getStockQuantity() - item.quantity());
                    OrderItem oi = new OrderItem();
                    oi.setProduct(product);
                    oi.setQuantity(item.quantity());
                    oi.setPriceAtMoment(product.getPrice());
                    return oi;
                })
                .toList();

        BigDecimal total = orderItems.stream()
                .map(oi -> oi.getPriceAtMoment().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus("NEW");
        order.setCreatedAt(LocalDateTime.now());
        order.setItems(orderItems);

        orderItems.forEach(oi -> oi.setOrder(order));

        Order saved = orderRepository.save(order);

        return new OrderResponse(
                saved.getId(),
                saved.getUser().getId(),
                saved.getTotalAmount(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getItems().stream()
                        .map(oi -> new OrderItemResponse(
                                oi.getId(),
                                oi.getProduct().getId(),
                                oi.getProduct().getName(),
                                oi.getQuantity(),
                                oi.getPriceAtMoment()
                        ))
                        .toList()
        );
    }

    @Transactional
    public OrderResponse addItemToOrder(Long orderId, Long userId, AddToOrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        //Проверка: пользователь может управлять только своими заказами
        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Cannot modify another user's order");
        }

        if (!"NEW".equals(order.getStatus())) {
            throw new BusinessException("Cannot add items to order in status: " + order.getStatus());
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStatus() != Product.Status.ACTIVE) {
            throw new BusinessException("Product is not available");
        }

        if (product.getStockQuantity() < request.quantity()) {
            throw new BusinessException("Not enough stock for product: " + product.getName());
        }

        // Проверка, есть ли уже этот товар в заказе
        Optional<OrderItem> existingItem = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Обновление количества
            OrderItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.quantity());
            item.setPriceAtMoment(product.getPrice());
        } else {
            // Добавление нового товара
            OrderItem newItem = new OrderItem();
            newItem.setProduct(product);
            newItem.setQuantity(request.quantity());
            newItem.setPriceAtMoment(product.getPrice());
            order.getItems().add(newItem);
        }

        // Обновление общего количества на складе
        product.setStockQuantity(product.getStockQuantity() - request.quantity());

        //Пересчёт общей суммы заказа
        BigDecimal total = order.getItems().stream()
                .map(oi -> oi.getPriceAtMoment().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);

        return toOrderResponse(saved);
    }

    @Transactional
    public void removeItemFromOrder(Long orderId, Long orderItemId, Long userId) {
        //Находим заказ
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        //Проверка, что пользователь - владелец заказа
        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Cannot modify another user's order");
        }

        //Находит элемент заказа для удаления
        OrderItem itemToRemove = order.getItems().stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with ID: " + orderItemId));

        //Возвращение товара на склад
        Product product = itemToRemove.getProduct();
        product.setStockQuantity(product.getStockQuantity() + itemToRemove.getQuantity());

        //Удаление элемента из заказа
        order.getItems().remove(itemToRemove);

        //Пересчитывание общей суммы заказа
        BigDecimal newTotal = order.getItems().stream()
                .map(oi -> oi.getPriceAtMoment().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(newTotal);

        //Сохранение изменений
        orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        //Найти заказ
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        //Проверка, что пользователь - владелец заказа
        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Cannot cancel another user's order");
        }

        //Проверка статуса заказа
        if (!"NEW".equals(order.getStatus())) {
            throw new BusinessException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        //Возвращение всех товаров на склад
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        //Обновление статуса заказа
        order.setStatus("CANCELLED");

        //Сохранение изменений
        orderRepository.save(order);

    }
}


