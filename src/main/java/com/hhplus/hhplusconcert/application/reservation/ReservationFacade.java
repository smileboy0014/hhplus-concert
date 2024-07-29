package com.hhplus.hhplusconcert.application.reservation;

import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo;
import com.hhplus.hhplusconcert.domain.concert.ConcertService;
import com.hhplus.hhplusconcert.domain.concert.command.CancelReservationCommand;
import com.hhplus.hhplusconcert.domain.concert.command.ReservationCommand;
import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.domain.payment.PaymentService;
import com.hhplus.hhplusconcert.domain.user.UserService;
import com.hhplus.hhplusconcert.support.aop.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationFacade {

    private final ConcertService concertService;
    private final PaymentService paymentService;
    private final UserService userService;

    /**
     * 좌석 예약을 요청하는 유즈케이스를 실행한다.
     *
     * @param command concertId, concertDateId, seatNumber, userId 정보
     * @return ReservationResponse 예약 완료 정보를 반환한다.
     */
    public ConcertReservationInfo reserveSeat(ReservationCommand.Create command) {
        return concertService.reserveSeat(command);
    }

    /**
     * 예약 내역을 조회하는 유즈케이스를 실행한다.
     *
     * @param userId userId 정보
     * @return ReservationResponse 나의 예약 내역을 반환한다.
     */
    public List<ConcertReservationInfo> getMyReservations(Long userId) {
        return concertService.getMyReservations(userId);
    }

    /**
     * 예약 취소를 하는 유즈케이스를 실행한다.
     *
     * @param command reservationId, userId 정보
     */
    @Transactional
    @DistributedLock(key = "'userLock'.concat(':').concat(#command.userId())")
    public void cancelReservation(CancelReservationCommand.Delete command) {
        // 1. 예약 취소
        ConcertReservationInfo reservationCancelInfo = concertService.cancelReservation(command.reservationId());

        // 2. 결제 취소 or 환불 처리
        Payment paymentCancelInfo = paymentService.cancelPayment(reservationCancelInfo);

        // 3. 유저 잔액 반환
        if (paymentCancelInfo.getStatus() == Payment.PaymentStatus.REFUND) {
            userService.refund(command.userId(), paymentCancelInfo.getPaymentPrice());
        }

        // 4. 좌석 점유 취소
        concertService.cancelOccupiedSeat(reservationCancelInfo.getSeatId());
    }


    /**
     * 좌석을 계속 점유할 수 있는지 확인하는 유즈케이스를 실행한다.
     */
    public void checkOccupiedSeat() {
        concertService.checkOccupiedSeat();
    }
}