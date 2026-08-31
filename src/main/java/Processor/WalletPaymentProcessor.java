package Processor;

import DTO.PaymentRequest;
import Repository.PaymentProcessor;
import org.springframework.stereotype.Component;

@Component
public class WalletPaymentProcessor implements PaymentProcessor {

    @Override
    public Boolean process(PaymentRequest paymentRequest) {
        return true;
    }
}

