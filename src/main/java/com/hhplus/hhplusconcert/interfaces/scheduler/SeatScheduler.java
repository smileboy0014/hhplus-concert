package com.hhplus.hhplusconcert.interfaces.scheduler;

import com.hhplus.hhplusconcert.application.reservation.ReservationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
@Slf4j
public class SeatScheduler { //좌석 관련 스케줄러

    private final ReservationFacade reservationFacade;

    /**
     * 5초마다 좌석 점유 여부 확인 후 점유 해제 하는 스케줄링 실행
     */
    @Scheduled(fixedRate = 5000)
    public void checkOccupiedSeat() {
        reservationFacade.checkOccupiedSeat(LocalDateTime.now());
    }
}