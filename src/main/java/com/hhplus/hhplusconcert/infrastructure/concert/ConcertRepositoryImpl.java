package com.hhplus.hhplusconcert.infrastructure.concert;

import com.hhplus.hhplusconcert.domain.concert.entity.*;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertDateJpaRepository concertDateJpaRepository;
    private final SeatJpaRepository seatJpaRepository;
    private final PlaceJpaRepository placeJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Place addPlace(Place place) {
        return placeJpaRepository.save(place);
    }

    @Override
    public List<Concert> findAllConcert() {
        return concertJpaRepository.findAll();
    }

    @Override
    public Optional<Concert> findConcertByConcertId(Long concertId) {
        return concertJpaRepository.findById(concertId);
    }

    @Override
    public Concert addConcert(Concert concert) {
        return concertJpaRepository.save(concert);
    }

    @Override
    public List<ConcertDate> addConcertDates(List<ConcertDate> concertDates) {
        return concertDateJpaRepository.saveAll(concertDates);
    }

    @Override
    public List<Seat> addSeats(List<Seat> seat) {
        return seatJpaRepository.saveAll(seat);
    }

    @Override
    public List<ConcertDate> findAllConcertDateByConcertId(Long concertId) {
        return concertDateJpaRepository.findAllByConcertInfo_concertId(concertId);
    }

    @Override
    public List<ConcertDate> findAllConcertDates() {
        return concertDateJpaRepository.findAll();
    }

    @Override
    public boolean existSeatByConcertDateAndStatus(Long concertDateId, SeatStatus status) {
        return seatJpaRepository.existsByConcertDateInfo_concertDateIdAndStatus(concertDateId, status);
    }

    @Override
    public List<Reservation> findAllReservation() {
        return reservationJpaRepository.findAll();
    }

    @Override
    public List<Reservation> findAllReservationByUserId(Long userId) {
        return reservationJpaRepository.findAllByUserId(userId);
    }

    @Override
    public List<Reservation> findAllReservationByStatusIs(ReservationStatus status) {
        return reservationJpaRepository.findAllByStatusIs(status);
    }

    @Override
    public Optional<Reservation> findReservationByReservationId(Long reservationId) {
        return reservationJpaRepository.findById(reservationId);
    }

    @Override
    public Reservation reserve(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }


    @Override
    public boolean existsReservationByConcertDateIdAndSeatNumberAndStatusIsNot(Long concertDateId, int seatNumber, ReservationStatus status) {
        return reservationJpaRepository.existsByConcertDateIdAndSeatNumberAndStatusIsNot(concertDateId, seatNumber, status);
    }

    @Override
    public List<Seat> findAllSeatByConcertDateIdAndStatus(Long concertDateId, SeatStatus status) {
        return seatJpaRepository.findAllByConcertDateInfo_concertDateIdAndStatus(concertDateId, status);
    }

    @Override
    public Optional<Seat> findSeatBySeatId(Long seatId) {
        return seatJpaRepository.findById(seatId);
    }

    @Override
    public Optional<Seat> findSeatByConcertDateIdAndSeatNumber(Long concertDateId, int seatNumber) {
        return seatJpaRepository.findByConcertDateInfo_concertDateIdAndSeatNumber(concertDateId, seatNumber);

    }

    @Override
    public Optional<Seat> findSeatByConcertDateIdAndSeatNumberWithLock(Long concertDateId, int seatNumber) {
        return seatJpaRepository.findSeatWithPessimisticLock(concertDateId, seatNumber);
    }


    @Override
    public Optional<ConcertDate> findConcertDateByConcertDateIdAndConcertId(Long concertDateId, Long concertId) {
        return concertDateJpaRepository.findByConcertDateIdAndConcertInfo_concertId(concertDateId, concertId);
    }

    @Override
    public boolean existsConcertDateByConcertId(Long concertId) {
        return concertDateJpaRepository.existsConcertDateByConcertInfo_ConcertId(concertId);
    }

    public void deleteAll() {
        placeJpaRepository.deleteAllInBatch();
        concertJpaRepository.deleteAllInBatch();
        concertDateJpaRepository.deleteAllInBatch();
        seatJpaRepository.deleteAllInBatch();
        reservationJpaRepository.deleteAllInBatch();
    }

}
