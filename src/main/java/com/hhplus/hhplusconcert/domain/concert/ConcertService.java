package com.hhplus.hhplusconcert.domain.concert;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.common.exception.ErrorCode;
import com.hhplus.hhplusconcert.domain.concert.command.ConcertCommand;
import com.hhplus.hhplusconcert.domain.concert.command.ReservationCommand;
import com.hhplus.hhplusconcert.domain.payment.command.PaymentCommand;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.CONCERT_IS_NOT_FOUND;
import static com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo.ReservationStatus.TEMPORARY_RESERVED;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertValidator concertValidator;
    private RBlockingQueue<ConcertReservationInfo> tempReservationQueue;
    private RDelayedQueue<ConcertReservationInfo> delayedReservationQueue;
    private final RedissonClient redissonClient;

    @PostConstruct
    public void init() {
        cancelOccupiedSeatListener();

        tempReservationQueue = redissonClient.getBlockingQueue("tempReservationQueue");
        delayedReservationQueue = redissonClient.getDelayedQueue(tempReservationQueue);
    }


    /**
     * 콘서트 정보를 요청하면 콘서트 정보를 반환한다.
     *
     * @return 콘서트 정보를 반환한다.
     */
    @Caching(cacheable = {
            @Cacheable(cacheManager = "l1LocalCacheManager", value = "concerts", key = "#pageable.pageNumber"),
            @Cacheable(cacheManager = "l2RedisCacheManager", value = "concerts", key = "#pageable.pageNumber")
    })
    @Transactional(readOnly = true)
    public Page<Concert> getConcerts(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        Page<Concert> concerts = concertRepository.getConcerts(sortedPageable);

        return concerts
                .map(concert -> {
                    List<ConcertDate> concertDates = concertRepository.getConcertDates(concert.getConcertId());
                    return Concert.builder()
                            .concertId(concert.getConcertId())
                            .name(concert.getName())
                            .concertDates(concertDates)
                            .build();
                });
    }

    /**
     * 콘서트 정보를 저장합니다.
     *
     * @return 콘서트 정보를 반환한다.
     */
    @Caching(evict = {
            @CacheEvict(value = "concerts", allEntries = true, cacheManager = "l1LocalCacheManager"),
            @CacheEvict(value = "concerts", allEntries = true, cacheManager = "l2RedisCacheManager"),
    })

    public Concert saveConcert(ConcertCommand.Create command) {
        Concert concert = Concert.builder().name(command.concertName()).build();
        Optional<Concert> savedConcert = concertRepository.saveConcert(concert);

        if (savedConcert.isEmpty()) throw new CustomException(CONCERT_IS_NOT_FOUND, CONCERT_IS_NOT_FOUND.getMsg());

        return savedConcert.get();
    }

    /**
     * 콘서트 상세 정보를 요청하면 콘서트 상세 정보를 반환한다.
     *
     * @param concertId concertId 정보
     * @return 콘서트 상세 정보 반환
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
        ConcertReservationInfo savedReservation = concertValidator.checkSavedReservation(concertRepository.saveReservation(reservationInfo), "예약에 실패하였습니다");
        // 5. 예약에 성공하면 delayedReservationQueue 에 임시 queue 저장
        delayedReservationQueue.offer(savedReservation, 5, TimeUnit.MINUTES);

        return savedReservation;
    }

    /**
     * 예약 내역을 요청하면 유저의 예약 정보를 반환한다.
     *
     * @param userId userId 정보
     * @return 유저의 예약 정보를 반환한다.
     */
    @Transactional(readOnly = true)
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
    @Transactional
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
    public void checkOccupiedSeat(Long reservationId) {
        Optional<ConcertReservationInfo> reservation = concertRepository.getReservation(reservationId);
        if (reservation.isEmpty() || !reservation.get().getStatus().equals(TEMPORARY_RESERVED)) {
            throw new CustomException(ErrorCode.RESERVATION_IS_ALREADY_CANCEL_OR_COMPLETE, "이미 완료되었거나 취소된 예약입니다.");
        }
        // 예약 상태 취소로 변경
        reservation.get().cancel();
        concertRepository.saveReservation(reservation.get());
        // 좌석 점유 취소(다시 예약 가능 상태로 변경)
        Optional<Seat> seat = concertRepository.getSeat(reservation.get().getSeatId());
        Seat seatInfo = concertValidator.checkExistSeat(seat, "좌석 정보가 존재하지 않습니다");
        seatInfo.cancel();

        concertRepository.saveSeat(seatInfo);
    }

    /**
     * 좌석 점유 해지 시간이 되면 동작한다.
     */
    private void cancelOccupiedSeatListener() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 내가 설정한 시간이 지나서 요소를 사용할 수 있을 때 까지 대기
                    if (tempReservationQueue != null) {
                        Object item = tempReservationQueue.take();
                        if (item instanceof ConcertReservationInfo reservationInfo) {
                            // 예약 상태가 임시 예약이면 예약 취소
                            checkOccupiedSeat(reservationInfo.getReservationId());
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }


}