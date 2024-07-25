package com.hhplus.hhplusconcert.infrastructure.concert;

import com.hhplus.hhplusconcert.domain.concert.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo.ReservationStatus;
import static com.hhplus.hhplusconcert.domain.concert.Seat.SeatStatus.AVAILABLE;
import static com.hhplus.hhplusconcert.support.utils.DateUtils.getLocalDateTimeToString;
import static java.time.LocalDateTime.now;

@Repository
@RequiredArgsConstructor
@Transactional
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertDateJpaRepository concertDateJpaRepository;
    private final SeatJpaRepository seatJpaRepository;
    private final PlaceJpaRepository placeJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public List<Concert> getConcerts() {
        return concertJpaRepository.findAll()
                .stream()
                .map(ConcertEntity::toDomain)
                .toList();
    }

    @Override
    public List<ConcertDate> getConcertDates(Long concertId) {
        return concertDateJpaRepository.findAllByConcertInfo_concertIdAndConcertDateAfter(
                        concertId,
                        getLocalDateTimeToString(now()))
                .stream()
                .map(ConcertDateEntity::toDomain)
                .toList();
    }


    @Override
    public boolean existAvailableSeats(Long concertDateId) {
        return seatJpaRepository.existsByConcertDateInfo_concertDateIdAndStatus(concertDateId, AVAILABLE);

    }

    @Override
    public List<Seat> getAvailableSeats(Long concertDateId) {
        return seatJpaRepository.findAllByConcertDateIdAndStatus(concertDateId, AVAILABLE)
                .stream()
                .map(SeatEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Place> savePlace(Place place) {
        PlaceEntity placeEntity = placeJpaRepository.save(PlaceEntity.toEntity(place));
        return Optional.of(placeEntity.toDomain());
    }

    @Override
    public Optional<Concert> saveConcert(Concert concert) {
        ConcertEntity concertEntity = concertJpaRepository.save(ConcertEntity.toEntity(concert));
        return Optional.of(concertEntity.toDomain());
    }

    @Override
    public List<ConcertDate> saveConcertDates(List<ConcertDate> concertDates) {
        return concertDateJpaRepository.saveAll(concertDates.stream()
                        .map(ConcertDateEntity::toEntity)
                        .toList())
                .stream()
                .map(ConcertDateEntity::toDomain)
                .toList();
    }

    @Override
    public List<Seat> saveSeats(List<Seat> seats) {
        return seatJpaRepository.saveAll(seats.stream()
                        .map(SeatEntity::toEntity)
                        .toList())
                .stream()
                .map(SeatEntity::toDomain)
                .toList();
    }

    @Override
    public boolean checkAlreadyReserved(Long concertId, Long concertDateId, int seatNumber) {
        return reservationJpaRepository.existsByConcertIdAndConcertDateIdAndSeatNumberAndStatusIsNot(concertId,
                concertDateId, seatNumber, ReservationStatus.CANCEL);
    }

    @Override
    public Optional<ConcertDate> getAvailableDates(Long concertDateId, Long concertId) {
        Optional<ConcertDateEntity> concertDateEntity = concertDateJpaRepository
                .findByConcertDateIdAndConcertInfo_concertId(concertDateId, concertId);

        if (concertDateEntity.isPresent()) {
            return concertDateEntity.map(ConcertDateEntity::toDomain);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Seat> getAvailableSeats(Long concertDateId, int seatNumber) {

        // 락을 안걸었을 때
        Optional<SeatEntity> seatEntity = seatJpaRepository
                .findByConcertDateInfo_concertDateIdAndSeatNumber(concertDateId, seatNumber);

        //비관적 락
//        Optional<SeatEntity> seatEntity = seatJpaRepository
//                .findSeatWithPessimisticLock(concertDateId, seatNumber);


        if (seatEntity.isPresent()) {
            return seatEntity.map(SeatEntity::toDomain);
        }

        return Optional.empty();
    }

    @Override
    public Optional<ConcertReservationInfo> saveReservation(ConcertReservationInfo reservation) {
        ConcertReservationEntity reservationEntity = reservationJpaRepository.save(
                ConcertReservationEntity.toEntity(reservation));

        return Optional.of(reservationEntity.toDomain());
    }

    @Override
    public List<ConcertReservationInfo> getMyReservations(Long userId) {

        return reservationJpaRepository.findAllByUserId(userId)
                .stream()
                .map(ConcertReservationEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<ConcertReservationInfo> getReservation(Long reservationId) {
        Optional<ConcertReservationEntity> reservationEntity = reservationJpaRepository.findById(reservationId);

        if (reservationEntity.isPresent()) {
            return reservationEntity.map(ConcertReservationEntity::toDomain);
        }
        return Optional.empty();
    }

    @Override
    public void deleteAllReservation() {
        reservationJpaRepository.deleteAllInBatch();
    }

    @Override
    public Optional<Seat> getSeat(Long seatId) {
        Optional<SeatEntity> seatEntity = seatJpaRepository.findById(seatId);

        if (seatEntity.isPresent()) {
            return seatEntity.map(SeatEntity::toDomain);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Seat> saveSeat(Seat seat) {
        SeatEntity seatEntity = seatJpaRepository.save(
                SeatEntity.toEntity(seat));

        return Optional.of(seatEntity.toDomain());
    }

    @Override
    public List<ConcertReservationInfo> getAllTempReservation() {
        List<ConcertReservationEntity> reservationEntities = reservationJpaRepository
                .findAllByStatusIs(ReservationStatus.TEMPORARY_RESERVED);

        return reservationEntities.stream()
                .map(ConcertReservationEntity::toDomain)
                .toList();
    }

    @Override
    public List<ConcertReservationInfo> getReservations() {
        return reservationJpaRepository.findAll()
                .stream().map(ConcertReservationEntity::toDomain)
                .toList();
    }

    public void deleteAll() {
        placeJpaRepository.deleteAllInBatch();
        concertJpaRepository.deleteAllInBatch();
        concertDateJpaRepository.deleteAllInBatch();
        seatJpaRepository.deleteAllInBatch();
        reservationJpaRepository.deleteAllInBatch();
    }

}
