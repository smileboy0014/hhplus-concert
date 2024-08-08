package com.hhplus.hhplusconcert.domain.payment.client;

import com.hhplus.hhplusconcert.domain.payment.Payment;

public interface DataPlatformClient {

    boolean sendPaymentResult(Payment payment);
}
