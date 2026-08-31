package DTO;

import Transfers.PaymentStatus;
import lombok.Data;

@Data
public class PaymentResponse {
    private Boolean success;
    private String message;
    private Long orderId;
    private PaymentStatus paymentStatus;

    public PaymentResponse(boolean b, String paymentSuccessful, Long id, PaymentStatus paymentStatus) {
    }
}