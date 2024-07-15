package com.hhplus.hhplusconcert.application.reservation;

import com.hhplus.hhplusconcert.domain.concert.service.ConcertService;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ReservationInfo;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ReservationReserveServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationFacade {

    private final ConcertService concertService;

    /**
     * 좌석 예약을 요청하는 유즈케이스를 실행한다.
     *
     * @param request concertId, concertDateId, seatNumber, userId 정보
     * @return ReservationResponse 예약 완료 정보를 반환한다.
     */
    public ReservationInfo reserveSeat(ReservationReserveServiceRequest request) {
        return concertService.reserveSeat(request);
    }

    /**
     * 예약 내역을 조회하는 유즈케이스를 실행한다.
     *
     * @param userId userId 정보
     * @return ReservationResponse 나의 예약 내역을 반환한다.
     */
    public List<ReservationInfo> getReservations(Long userId) {
        return concertService.getReservations(userId);
    }

    /**
     * 예약 취소를 하는 유즈케이스를 실행한다.
     *
     * @param reservationId reservationId 정보
     */
    public void cancelReservation(Long reservationId) {
        concertService.cancelReservation(reservationId);
    }

    /**
     * 좌석을 계속 점유할 수 있는지 확인하는 유즈케이스를 실행한다.
     */
    public void checkOccupiedSeat() {
        concertService.checkOccupiedSeat();

    }
}