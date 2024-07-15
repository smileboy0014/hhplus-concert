package com.hhplus.hhplusconcert.infrastructure.concert;

import com.hhplus.hhplusconcert.domain.common.exception.CustomNotFoundException;
import com.hhplus.hhplusconcert.domain.concert.entity.Concert;
import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertDateJpaRepository concertDateJpaRepository;
    private final SeatJpaRepository seatJpaRepository;

    @Override
    public List<Concert> findAllConcert() {
        return concertJpaRepository.findAll();
    }

    @Override
    public Concert findConcertByConcertId(Long concertId) {
        return concertJpaRepository.findById(concertId)
                .orElseThrow(() -> new CustomNotFoundException(CONCERT_IS_NOT_FOUND,
                        "콘서트 정보를 찾을 수 없습니다. [ConcertId : %d]".formatted(concertId)));
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

    public void deleteAll() {
        concertJpaRepository.deleteAllInBatch();
        concertDateJpaRepository.deleteAllInBatch();
        seatJpaRepository.deleteAllInBatch();
    }

    @Override
    public List<Seat> findAllSeatByConcertDateIdAndStatus(Long concertDateId, SeatStatus status) {
        return seatJpaRepository.findAllByConcertDateInfo_concertDateIdAndStatus(concertDateId, status);
    }

    @Override
    public Seat findSeatBySeatId(Long seatId) {
        return seatJpaRepository.findById(seatId)
                .orElseThrow(() -> new CustomNotFoundException(SEAT_IS_NOT_FOUND,
                        "좌석 정보가 존재하지 않습니다. [seatId : %d]".formatted(seatId)));
    }

    @Override
    public Seat findBySeatConcertDateIdAndSeatNumber(Long concertDateId, int seatNumber) {
        return seatJpaRepository.findByConcertDateInfo_concertDateIdAndSeatNumber(concertDateId, seatNumber)
                .orElseThrow(() -> new CustomNotFoundException(RESERVATION_IS_ALREADY_EXISTED,
                        "이미 해당 좌석의 예약 내역이 존재합니다. [seatNumber : %d]".formatted(seatNumber)));

    }

    @Override
    public ConcertDate findConcertDateByConcertDateIdAndConcertId(Long concertDateId, Long concertId) {
        return concertDateJpaRepository.findByConcertDateIdAndConcertInfo_concertId(concertDateId, concertId)
                .orElseThrow(() -> new CustomNotFoundException(AVAILABLE_DATE_IS_NOT_FOUND,
                        "예약 가능한 콘서트 날짜가 존재하지 않습니다."));
    }

    @Override
    public boolean existsConcertDateByConcertId(Long concertId) {
        return concertDateJpaRepository.existsConcertDateByConcertInfo_ConcertId(concertId);
    }

}
