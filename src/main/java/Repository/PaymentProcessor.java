package Repository;

import DTO.PaymentRequest;

public interface PaymentProcessor {
    Boolean process(PaymentRequest paymentRequest);
}

