package DTO;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long orderId;
    private String paymentMethod;
    private Double amount;
}
