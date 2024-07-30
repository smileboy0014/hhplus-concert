package com.hhplus.hhplusconcert.domain.concert;

import com.hhplus.hhplusconcert.domain.concert.command.ReservationCommand;
import com.hhplus.hhplusconcert.domain.payment.command.PaymentCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertValidator concertValidator;

    /**
     * 콘서트 정보를 요청하면 콘서트 정보를 반환한다.
     *
     * @return 콘서트 정보를 반환한다.
     */
    public List<Concert> getConcerts() {
        List<Concert> concerts = concertRepository.getConcerts();

        return concerts.stream()
                .map(concert -> {
                    List<ConcertDate> concertDates = concertRepository.getConcertDates(concert.getConcertId());
                    return Concert.builder()
                            .concertId(concert.getConcertId())
                            .name(concert.getName())
                            .concertDates(concertDates)
                            .build();
                }).toList();
    }

    /**
     * 콘서트 상세 정보를 요청하면 콘서트 상세 정보를 반환한다.
     *
     * @param concertId concertId 정보
     * @return 콘서트 상세 정보 반환
     */
    public Concert getConcert(Long concertId) {
        List<ConcertDate> concertDates = concertRepository.getConcertDates(concertId);
        concertValidator.existConcert(concertId, concertDates);

        return Concert
                .builder()
                .concertId(concertDates.get(0).getConcert().getConcertId())
                .name(concertDates.get(0).getConcert().getName())
                .concertDates(concertDates)
                .build();
    }

    /**
     * 콘서트 예약 가능한 날짜를 요청하면 콘서트 예약 날짜 정보를 반환한다.
     *
     * @param concertId concertId 정보
     * @return 콘서트 예약 날짜 정보를 반환한다.
     */
    public List<ConcertDate> getAvailableConcertDates(Long concertId) {
        List<ConcertDate> concertDates = concertRepository.getConcertDates(concertId);
        concertValidator.existAvailableConcertDates(concertId, concertDates);

        return concertDates.stream()
                .map(concertDate -> {
                    boolean available = concertRepository.existAvailableSeats(concertDate.getConcertDateId());
                    return ConcertDate.builder()
                            .concertDateId(concertDate.getConcertDateId())
                            .place(concertDate.getPlace())
                            .concertDate(concertDate.getConcertDate())
                            .isAvailable(available)
                            .build();
                }).toList();
    }

    /**
     * 예약 가능한 좌석을 요청하면 예약 가능한 좌석 정보를 반환한다.
     *
     * @param concertDateId concertDateId 정보
     * @return 예약 가능한 좌석 정보를 반환한다.
     */
    public List<Seat> getAvailableSeats(Long concertDateId) {
        List<Seat> availableSeats = concertRepository.getAvailableSeats(concertDateId);
        concertValidator.existAvailableSeats(concertDateId, availableSeats);

        return availableSeats;
    }

    /**
     * 좌석 예약을 요청하면 예약 완료 정보를 반환한다.
     *
     * @param command concertId, concertDateId, seatNumber, userId 정보
     * @return 예약 완료 정보를 반환한다.
     */
    @Transactional
    public ConcertReservationInfo reserveSeat(ReservationCommand.Create command) {
        // 1 이미 예약이 있는지 확인
        boolean checkedReservation = concertRepository.checkAlreadyReserved(command.concertId(), command.concertDateId(),
                command.seatNumber());
        concertValidator.checkAlreadyReserved(checkedReservation, command.concertDateId(), command.seatNumber());
        // 2. concertDate 정보 조회
        Optional<ConcertDate> availableDates = concertRepository.getAvailableDates(command.concertDateId(),
                command.concertId());
        ConcertDate concertDate = concertValidator.checkExistConcertDate(availableDates, command.concertDateId());
        // 3. 좌석 점유
        Optional<Seat> availableSeats = concertRepository.getAvailableSeats(command.concertDateId(),
                command.seatNumber());
        Seat seat = concertValidator.checkExistSeat(availableSeats, "예약 가능한 좌석이 존재하지 않습니다.");
        seat.occupy();
        concertRepository.saveSeat(seat);
        // 4. 예약 테이블 저장
        ConcertReservationInfo reservationInfo = command.toReservationDomain(seat, concertDate);

        return concertValidator.checkSavedReservation(concertRepository.saveReservation(reservationInfo), "예약에 실패하였습니다");
    }

    /**
     * 예약 내역을 요청하면 유저의 예약 정보를 반환한다.
     *
     * @param userId userId 정보
     * @return 유저의 예약 정보를 반환한다.
     */
    public List<ConcertReservationInfo> getMyReservations(Long userId) {
        return concertRepository.getMyReservations(userId);
    }

    /**
     * 예약을 취소한다.
     *
     * @param reservationId reservationId 정보
     */
    @Transactional
    public ConcertReservationInfo cancelReservation(Long reservationId) {
        // 1. 예약 조회
        Optional<ConcertReservationInfo> reservation = concertRepository.getReservation(reservationId);
        ConcertReservationInfo reservationInfo = concertValidator.checkExistReservation(reservation,
                "취소할 예약 내역이 존재하지 않습니다");
        // 2. 예약 취소
        reservationInfo.cancel();
        Optional<ConcertReservationInfo> cancelReservation = concertRepository.saveReservation(reservationInfo);

        return concertValidator.checkSavedReservation(cancelReservation, "예약 취소에 실패하였습니다.");
    }

    /**
     * 좌석 점유를 해지한다.
     *
     * @param seatId seat 정보
     */
    @Transactional
    public void cancelOccupiedSeat(Long seatId) {
        // 좌석 정보 조회
        Optional<Seat> seat = concertRepository.getSeat(seatId);
        // 좌석 점유 취소
        Seat seatInfo = concertValidator.checkExistSeat(seat, "좌석 예약을 취소할 좌석이 존재하지 않습니다.");
        seatInfo.cancel();
        concertRepository.saveSeat(seatInfo);
    }

    /**
     * 예약을 완료한다.
     *
     * @param command reservation 정보
     */
    public ConcertReservationInfo completeReservation(PaymentCommand.Create command) {

        Optional<ConcertReservationInfo> reservation = concertRepository.getReservation(command.reservationId());
        ConcertReservationInfo reservationInfo = concertValidator.checkExistReservation(reservation,
                "예약 완료할 예약 내역이 존재하지 않습니다");
        //예약 완료로 상태 변경
        reservationInfo.complete();
        Optional<ConcertReservationInfo> completeReservation = concertRepository.saveReservation(reservationInfo);

        return concertValidator.checkSavedReservation(completeReservation, "예약 완료에 실패하였습니다");
    }

    /**
     * 좌석을 계속 점유할 수 있는지 확인한다.
     */
    @Transactional
    public void checkOccupiedSeat() {
        // 임시 예약인 모든 예약 조회
        List<ConcertReservationInfo> allTempReservation = concertRepository.getAllTempReservation();

        allTempReservation.forEach(reservation -> {
            LocalDateTime createdAtTime = reservation.getCreatedAt();
            Duration duration = Duration.between(createdAtTime, LocalDateTime.now());

            if (duration.toSeconds() > 5 * 60) { //정해진 시간을 넘었는지 (default:5분)
                // 1. 예약 취소
                reservation.cancel();
                concertRepository.saveReservation(reservation);
                // 2. 좌석 점유 취소(다시 예약 가능 상태로 변경)
                Optional<Seat> seat = concertRepository.getSeat(reservation.getSeatId());
                Seat seatInfo = concertValidator.checkExistSeat(seat, "좌석 정보가 존재하지 않습니다");
                seatInfo.cancel();
                concertRepository.saveSeat(seatInfo);
            }
        });
    }
}