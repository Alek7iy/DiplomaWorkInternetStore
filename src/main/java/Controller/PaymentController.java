package Controller;

import DTO.PaymentRequest;
import DTO.PaymentResponse;
import Service.PaymentService;
import Transfers.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<PaymentStatus> getPaymentStatus(@PathVariable Long orderId) {
        PaymentStatus status = paymentService.getPaymentStatus(orderId);
        return ResponseEntity.ok(status);
    }
}

