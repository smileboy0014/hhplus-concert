package com.hhplus.hhplusconcert.infrastructure.client;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.domain.payment.client.DataPlatformClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.DATA_PLATFORM_SEND_FAIL;

@Slf4j
@Component
public class DataPlatformClientImpl implements DataPlatformClient {
    @Override
    public boolean sendPaymentResult(Payment payment) {
        try {
            Thread.sleep(4000);
            log.info("[DataPlatformClient] : SUCCESS Send PaymentResult :  {}", payment);
        } catch (InterruptedException e) {
            throw new CustomException(DATA_PLATFORM_SEND_FAIL, DATA_PLATFORM_SEND_FAIL.getMsg());
        }
        return true;
    }
}
