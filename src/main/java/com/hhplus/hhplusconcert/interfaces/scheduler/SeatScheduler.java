package com.hhplus.hhplusconcert.interfaces.scheduler;

import com.hhplus.hhplusconcert.application.reservation.ReservationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class SeatScheduler { //좌석 관련 스케줄러

    private final ReservationFacade reservationFacade;

    @Scheduled(fixedRate = 5000) // 매 5초마다 스케줄러 실행
    public void checkOccupiedSeat() {
        log.info("5초 마다 예약 후, 5분이 지났는지 체크하는 스케줄러 실행");
        reservationFacade.checkOccupiedSeat();
    }
}