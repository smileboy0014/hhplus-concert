package com.hhplus.hhplusconcert.domain.concert.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomBadRequestException;
import com.hhplus.hhplusconcert.domain.common.exception.CustomNotFoundException;
import com.hhplus.hhplusconcert.domain.concert.entity.Concert;
import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.repository.ConcertRepository;
import com.hhplus.hhplusconcert.domain.concert.repository.ReservationRepository;
import com.hhplus.hhplusconcert.domain.concert.service.dto.*;
import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import com.hhplus.hhplusconcert.domain.payment.repository.PaymentRepository;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;


    /**
     * 콘서트 정보를 요청하면 콘서트 정보를 반환한다.
     *
     * @return ConcertResponse 콘서트 정보를 반환한다.
     */
    public List<ConcertResponse> getConcerts() {

        return concertRepository.findAllConcert().stream()
                .map(concert -> {
                    List<ConcertDate> concertDates = concertRepository.findAllConcertDateByConcertId(concert.getConcertId());
                    return ConcertResponse.of(concert, concertDates);
                })
                .toList();
    }

    /**
     * 콘서트 상세 정보를 요청하면 콘서트 상세 정보를 반환한다.
     *
     * @return ConcertResponse 콘서트 상세 정보를 반환한다.
     */
    public ConcertResponse getConcert(Long concertId) {
        Concert concert = concertRepository.findConcertByConcertId(concertId);
        List<ConcertDate> concertDates = concertRepository.findAllConcertDateByConcertId(concertId);

        return ConcertResponse.of(concert, concertDates);
    }

    /**
     * 콘서트 예약 가능한 날짜를 요청하면 콘서트 예약 날짜 정보를 반환한다.
     *
     * @param concertId concertId 정보
     * @return 콘서트 예약 날짜 정보를 반환한다.
     */
    public List<ConcertDateResponse> getConcertDates(Long concertId) {
        List<ConcertDate> concertDates = concertRepository.findAllConcertDateByConcertId(concertId);
        validAvailableConcertDates(concertId, concertDates);

        return concertDates.stream()
                .map(concertDate -> {
                    boolean available = concertRepository.existSeatByConcertDateAndStatus(concertDate.getConcertDateId(), SeatStatus.AVAILABLE.getStatus());
                    return ConcertDateResponse.of(concertDate, available);
                })
                .toList();
    }

    /**
     * 예약 가능한 좌석을 요청하면 예약 가능한 좌석 정보를 반환한다.
     *
     * @param concertDateId concertDateId 정보
     * @return 예약 가능한 좌석 정보를 반환한다.
     */
    public List<ConcertSeatResponse> getAvailableSeats(Long concertDateId) {
        List<Seat> seats = concertRepository.findAllSeatByConcertDateIdAndStatus(concertDateId, SeatStatus.AVAILABLE.getStatus());
        validAvailableSeats(concertDateId, seats);

        return seats.stream()
                .map(ConcertSeatResponse::of)
                .collect(Collectors.toList());
    }


    /**
     * 좌석 예약을 요청하면 예약 완료 정보를 반환한다.
     *
     * @param request concertId, concertDateId, seatNumber, userId 정보
     * @return ReservationResponse 예약 완료 정보를 반환한다.
     */

    @Transactional
    public ReservationResponse reserveSeat(ReservationReserveServiceRequest request) {
        // 1 이미 예약이 있는지 확인
        validateExistingReservation(request.concertDateId(), request.seatNumber());
        // 2. concertDate 정보 조회
        ConcertDate concertDate = concertRepository.findConcertDateByConcertDateIdAndConcertId(request.concertDateId(), request.concertId());
        // 3. seat 상태 변경
        Seat seat = concertRepository.findBySeatConcertDateIdAndSeatNumber(request.concertDateId(), request.seatNumber());
        seat.changeStatus(SeatStatus.UNAVAILABLE.getStatus());
        // 3. 예약 테이블 저장
        Reservation reservation = reservationRepository.reserve(request.toReservationEntity(concertDate, seat));
        // 4. 결제 건 생성
        Payment payment = paymentRepository.createPayment(request.toPaymentEntity(reservation, seat));

        return ReservationResponse.of(reservation,
                ReservationConcertResponse.of(concertDate, seat),
                ReservationPaymentResponse.of(payment)
        );
    }


    /**
     * 예약 내역을 요청하면 유저의 예약 정보를 반환한다.
     *
     * @param userId userId 정보
     * @return ReservationResponse 유저의 예약 정보를 반환한다.
     */
    public List<ReservationResponse> getReservations(Long userId) {

        return reservationRepository.findAllByUserId(userId).stream()
                .map(reservation -> {
                    Payment payment = paymentRepository.findByReservationId(reservation.getReservationId());
                    return ReservationResponse.of(reservation,
                            ReservationConcertResponse.of(reservation),
                            ReservationPaymentResponse.of(payment));
                })
                .toList();
    }

    /**
     * 예약을 취소한다.
     *
     * @param reservationId reservationId 정보
     */
    @Transactional
    public void cancelReservation(Long reservationId) {
        // 1. 예약 취소로 상태 변경
        Reservation reservation = reservationRepository.findByReservationId(reservationId);
        reservation.changeStatus(ReservationStatus.CANCEL.getStatus());
        // 2. 결제 상태 변경
        Payment payment = paymentRepository.findByReservationId(reservationId);

        if (payment.getStatus().equals(PaymentStatus.WAIT.getStatus())) {
            // 2-1. 결제 전에 취소한 경우
            payment.changeStatus(PaymentStatus.CANCEL.getStatus());
        } else if (payment.getStatus().equals(PaymentStatus.COMPLETE.getStatus())) {
            // 2-2. 결제 후에 취소한 경우
            payment.changeStatus(PaymentStatus.REFUND.getStatus());
            // 2-3. 유저 잔액 반환
            User user = userRepository.findUserByUserId(reservation.getUserId());
            user.chargeBalance(payment.getPaymentPrice());
        }

        // 3. 좌석 예약 가능으로 상태 변경
        Seat seat = concertRepository.findSeatBySeatId(reservation.getSeatId());
        seat.changeStatus(SeatStatus.AVAILABLE.getStatus());
    }

    /**
     * 좌석을 계속 점유할 수 있는지 스케줄러로 확인한다.
     */
    @Transactional
    public void checkOccupiedSeat() {
        LocalDateTime nowTime = LocalDateTime.now();

        List<Reservation> reservations = reservationRepository.findAllByStatusIs(
                ReservationStatus.PROGRESSING.getStatus());

        reservations.forEach(reservation -> {
            LocalDateTime reservedAtTime = reservation.getReservedAt();
            Duration duration = Duration.between(reservedAtTime, nowTime);

            if (duration.toSeconds() > 5 * 60) { //5분이 넘었는지 확인
                // 1. 예약 상태 취소로 변경
                reservation.changeStatus(ReservationStatus.CANCEL.getStatus());
                // 2. 결제 정보 취소
                Payment payment = paymentRepository.findByReservationId(reservation.getReservationId());
                payment.changeStatus(PaymentStatus.CANCEL.getStatus());
                // 3. 좌석 점유 취소(다시 예약 가능 상태로 변경)
                Seat seat = concertRepository.findSeatBySeatId(reservation.getSeatId());
                seat.changeStatus(SeatStatus.AVAILABLE.getStatus());

            }
        });
    }

    private void validAvailableConcertDates(Long concertId, List<ConcertDate> concertDates) {
        if (concertDates.isEmpty()) throw new CustomNotFoundException(AVAILABLE_DATE_IS_NOT_FOUND,
                "예약 가능한 콘서트 날짜가 존재하지 않습니다. [concertId : %d]".formatted(concertId));
    }

    private void validAvailableSeats(Long concertDateId, List<Seat> seats) {
        if (seats.isEmpty()) throw new CustomNotFoundException(SEAT_IS_NOT_FOUND,
                "예약 가능한 좌석이 존재하지 않습니다. [concertDateId : %d]".formatted(concertDateId));
    }

    private void validateExistingReservation(Long concertDateId, int seatNumber) {
        if (reservationRepository.existsByConcertDateIdAndSeatNumberAndStatusIs(
                concertDateId, seatNumber, ReservationStatus.PROGRESSING.getStatus())) {
            throw new CustomBadRequestException(RESERVATION_IS_ALREADY_EXISTED,
                    "이미 해당 좌석의 예약 내역이 존재합니다. [concertDateId: %d, seatNumber: %d]"
                            .formatted(concertDateId, seatNumber));
        }
    }
}
