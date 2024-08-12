package com.hhplus.hhplusconcert.infrastructure.client;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.payment.client.PushClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.PUSH_KAKAOTALK_MESSAGE_FAIL;

@Slf4j
@Component
public class PushClientImpl implements PushClient {
    @Override
    public boolean pushKakaotalk() {
        try {
            Thread.sleep(1000);
            log.info("[PushClient] : SUCCESS Push Kakaotalk ");
        } catch (InterruptedException e) {
            throw new CustomException(PUSH_KAKAOTALK_MESSAGE_FAIL, PUSH_KAKAOTALK_MESSAGE_FAIL.getMsg());
        }
        return true;
    }
}
