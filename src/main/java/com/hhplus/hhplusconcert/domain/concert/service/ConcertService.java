package com.hhplus.hhplusconcert.domain.concert.service;

import com.hhplus.hhplusconcert.domain.concert.entity.Concert;
import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.service.dto.*;
import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import com.hhplus.hhplusconcert.domain.payment.service.PaymentAppender;
import com.hhplus.hhplusconcert.domain.payment.service.PaymentFinder;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.service.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertAppender concertAppender;
    private final ConcertFinder concertFinder;
    private final ConcertReader concertReader;
    private final PaymentAppender paymentAppender;
    private final PaymentFinder paymentFinder;
    private final UserFinder userFinder;


    /**
     * 콘서트 정보를 요청하면 콘서트 정보를 반환한다.
     *
     * @return ConcertResponse 콘서트 정보를 반환한다.
     */
    public List<ConcertInfo> getConcerts() {
        List<Concert> concerts = concertFinder.findConcerts();

        return concertReader.readConcerts(concerts);
    }

    /**
     * 콘서트 상세 정보를 요청하면 콘서트 상세 정보를 반환한다.
     *
     * @return ConcertResponse 콘서트 상세 정보를 반환한다.
     */
    public ConcertInfo getConcert(Long concertId) {
        Concert concert = concertFinder.findConcertByConcertId(concertId);
        List<ConcertDate> concertDates = concertFinder.findAllConcertDateByConcertId(concertId);

        return concertReader.readConcert(concert, concertDates);
    }

    /**
     * 콘서트 예약 가능한 날짜를 요청하면 콘서트 예약 날짜 정보를 반환한다.
     *
     * @param concertId concertId 정보
     * @return 콘서트 예약 날짜 정보를 반환한다.
     */
    public List<ConcertDateInfo> getConcertDates(Long concertId) {
        List<ConcertDate> concertDates = concertFinder.findAllConcertDateByConcertId(concertId);

        return concertReader.readConcertDates(concertDates);
    }

    /**
     * 예약 가능한 좌석을 요청하면 예약 가능한 좌석 정보를 반환한다.
     *
     * @param concertDateId concertDateId 정보
     * @return 예약 가능한 좌석 정보를 반환한다.
     */
    public List<ConcertSeatInfo> getAvailableSeats(Long concertDateId) {
        List<Seat> seats = concertFinder.findAllSeatByConcertDateIdAndStatus(concertDateId, SeatStatus.AVAILABLE);

        return concertReader.readSeats(seats);
    }


    /**
     * 좌석 예약을 요청하면 예약 완료 정보를 반환한다.
     *
     * @param request concertId, concertDateId, seatNumber, userId 정보
     * @return ReservationResponse 예약 완료 정보를 반환한다.
     */

    @Transactional
    public ReservationInfo reserveSeat(ReservationReserveServiceRequest request) {
        // 1 이미 예약이 있는지 확인
        concertFinder.existsReservationByConcertDateIdAndSeatNumber(request.concertDateId(), request.seatNumber());
        // 2. concertDate 정보 조회
        ConcertDate concertDate = concertFinder.findConcertDateByConcertDateIdAndConcertId(request.concertDateId(), request.concertId());
        // 3. seat 상태 변경
        Seat seat = concertFinder.findSeatByConcertDateIdAndSeatNumberWithLock(request.concertDateId(), request.seatNumber());
        seat.occupy();
        // 4. 예약 테이블 저장
        Reservation reservation = concertAppender.appendReservation(request.toReservationEntity(concertDate, seat));
        // 5. 결제 건 생성
        Payment payment = paymentAppender.appendPayment(request.toPaymentEntity(reservation, seat));

        return concertReader.readReservation(reservation, concertDate, seat, payment);
    }


    /**
     * 예약 내역을 요청하면 유저의 예약 정보를 반환한다.
     *
     * @param userId userId 정보
     * @return ReservationResponse 유저의 예약 정보를 반환한다.
     */
    public List<ReservationInfo> getReservations(Long userId) {
        List<Reservation> reservations = concertFinder.findAllReservationByUserId(userId);

        return concertReader.readReservations(reservations);
    }

    /**
     * 예약을 취소한다.
     *
     * @param reservationId reservationId 정보
     */
    @Transactional
    public void cancelReservation(Long reservationId) {
        // 1. 예약 조회
        Reservation reservation = concertFinder.findReservationByReservationId(reservationId);
        // 1-1 예약 취소
        reservation.cancel();
        // 2. 결제 취소 or 환불 처리
        Payment payment = paymentFinder.findPaymentByReservationId(reservationId);
        payment.cancel();

        if (payment.getStatus() == PaymentStatus.REFUND) {
            // 2-1. 유저 잔액 반환
            User user = userFinder.findUserByUserId(reservation.getUserId());
            user.chargeBalance(payment.getPaymentPrice());
        }

        // 3. 좌석 점유 취소(다시 예약 가능 상태로 변경)
        Seat seat = concertFinder.findSeatBySeatId(reservation.getSeatId());
        seat.cancel();
    }

    /**
     * 좌석을 계속 점유할 수 있는지 확인한다.
     */
    @Transactional
    public void checkOccupiedSeat(LocalDateTime now) {
        List<Reservation> reservations = concertFinder
                .findAllReservationByStatusIs(ReservationStatus.TEMPORARY_RESERVED);

        reservations.forEach(reservation -> {
            LocalDateTime reservedAtTime = reservation.getReservedAt();
            Duration duration = Duration.between(reservedAtTime, now);

            if (duration.toSeconds() > 5 * 60) { //5분이 넘었는지 확인
                // 1. 예약 취소
                reservation.cancel();
                // 2. 결제 취소
                Payment payment = paymentFinder.findPaymentByReservationId(reservation.getReservationId());
                payment.cancel();
                // 3. 좌석 점유 취소(다시 예약 가능 상태로 변경)
                Seat seat = concertFinder.findSeatBySeatId(reservation.getSeatId());
                seat.cancel();
            }
        });
    }
}