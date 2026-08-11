package Service;

import DTO.CreateOrderRequest;
import DTO.OrderItemResponse;
import DTO.OrderResponse;
import Repository.OrderRepository;
import Repository.ProductRepository;
import Repository.UserRepository;
import entity.Order;
import entity.OrderItem;
import entity.Product;
import entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import Exception.ResourceNotFoundException;
import Exception.BusinessException;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

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
}

