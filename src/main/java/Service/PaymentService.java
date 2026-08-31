package Service;

import DTO.PaymentRequest;
import DTO.PaymentResponse;
import Repository.OrderRepository;
import Repository.PaymentProcessor;
import Transfers.OrderStatus;
import Transfers.PaymentStatus;
import entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentProcessor paymentProcessor; // интерфейс для разных методов оплаты

    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        try {
            Order order = orderRepository.findById(paymentRequest.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if (isPaymentConditionMet(order, paymentRequest)) {
                Boolean paymentSuccess = paymentProcessor.process(paymentRequest);

                if (paymentSuccess) {
                    completeOrder(order);
                    return new PaymentResponse(true, "Payment successful", order.getId(),
                            PaymentStatus.PAID);
                } else {
                    failPayment(order);
                    return new PaymentResponse(false, "Payment failed", order.getId(),
                            PaymentStatus.FAILED);
                }
            } else {
                cancelOrder(order);
                return new PaymentResponse(false, "Payment conditions not met", order.getId(),
                        PaymentStatus.CANCELLED);
            }
        } catch (Exception e) {
            return new PaymentResponse(false, e.getMessage(), paymentRequest.getOrderId(),
                    PaymentStatus.FAILED);
        }
    }

    private boolean isPaymentConditionMet(Order order, PaymentRequest paymentRequest) {

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.name()) {
            return false;
        }
        if (!order.getTotalAmount().equals(paymentRequest.getAmount())) {
            return false;
        }
        return true;
    }

    private void completeOrder(Order order) {
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        order.setStatus(OrderStatus.COMPLETED.name()); // заказ завершён
        orderRepository.save(order);
    }

    private void failPayment(Order order) {
        order.setPaymentStatus(PaymentStatus.FAILED);
        orderRepository.save(order);
    }

    private void cancelOrder(Order order) {
        order.setPaymentStatus(PaymentStatus.CANCELLED);
        order.setStatus(OrderStatus.CANCELLED.name());
        orderRepository.save(order);
    }

    public PaymentStatus getPaymentStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return order.getPaymentStatus();
    }
}

