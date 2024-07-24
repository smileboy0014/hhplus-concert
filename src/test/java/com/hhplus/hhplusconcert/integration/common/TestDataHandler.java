package com.hhplus.hhplusconcert.integration.common;

import com.hhplus.hhplusconcert.domain.concert.*;
import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.domain.payment.PaymentRepository;
import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.domain.user.UserRepository;
import com.hhplus.hhplusconcert.infrastructure.concert.ConcertRepositoryImpl;
import com.hhplus.hhplusconcert.infrastructure.payment.PaymentRepositoryImpl;
import com.hhplus.hhplusconcert.infrastructure.user.UserRepositoryImpl;
import com.hhplus.hhplusconcert.support.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.concert.Seat.builder;

@Component
public class TestDataHandler {
    public TestDataHandler(ConcertRepositoryImpl concertRepository, PaymentRepositoryImpl paymentRepository,
                           UserRepositoryImpl userRepository) {
        this.concertRepository = concertRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;

    }

    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;
    private final PaymentRepository paymentRepository;

    public void settingConcertInfo() {
        int totalSeatCnt = 50;
        Place place = Place.builder()
                .name("올림픽 경기장")
                .totalSeat(totalSeatCnt)
                .build();

        Place savedPlace1 = concertRepository.savePlace(place).orElse(null);

        Concert concert1 = Concert.builder()
                .name("싸이 흠뻑쇼")
                .build();

        Concert concert2 = Concert.builder()
                .name("GOD 콘서트")
                .build();

        Concert savedConcert1 = concertRepository.saveConcert(concert1).orElse(null);
        Concert savedConcert2 = concertRepository.saveConcert(concert2).orElse(null);

        List<ConcertDate> concertDates = new ArrayList<>();

        concertDates.add(ConcertDate.builder()
                .concert(savedConcert1)
                .concertDate(DateUtils.getLocalDateTimeToString(LocalDateTime.now().plusMonths(3)))
                .place(savedPlace1)
                .build());

        concertDates.add(ConcertDate.builder()
                .concert(savedConcert2)
                .concertDate(DateUtils.getLocalDateTimeToString(LocalDateTime.now().plusMonths(3)))
                .place(savedPlace1)
                .build());

        List<ConcertDate> savedConcertDates = concertRepository.saveConcertDates(concertDates);

        List<Seat> seats = new ArrayList<>();

        for (int i = 1; i <= totalSeatCnt; i++) {
            if (i <= 20) { // C class
                seats.add(builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(120000))
                        .concertDateInfo(savedConcertDates.get(0))
                        .ticketClass(Seat.TicketClass.C)
                        .status(Seat.SeatStatus.UNAVAILABLE)
                        .build());
            } else if (i <= 30) { // B class
                seats.add(builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(150000))
                        .concertDateInfo(savedConcertDates.get(0))
                        .ticketClass(Seat.TicketClass.B)
                        .status(Seat.SeatStatus.UNAVAILABLE)
                        .build());
            } else if (i <= 40) { // A class
                seats.add(builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(170000))
                        .concertDateInfo(savedConcertDates.get(0))
                        .ticketClass(Seat.TicketClass.A)
                        .status(Seat.SeatStatus.AVAILABLE)
                        .build());
            } else {
                seats.add(builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(190000))
                        .concertDateInfo(savedConcertDates.get(0))
                        .ticketClass(Seat.TicketClass.S)
                        .status(Seat.SeatStatus.AVAILABLE)
                        .build());
            }
        }
        concertRepository.saveSeats(seats);
    }

    public User settingUser(BigDecimal balance) {
        return userRepository.saveUser(User.builder()
                .balance(balance).build()).get();
    }

    public void reserveSeats() {
        Long concertId = 1L;
        List<ConcertDate> concertDates = concertRepository.getConcertDates(concertId);
        Seat seat1 = concertRepository.getSeatForReservation(concertDates.get(0).getConcertDateId(), 35).orElse(null);
        Seat seat2 = concertRepository.getSeatForReservation(concertDates.get(0).getConcertDateId(), 41).orElse(null);

        ConcertReservationInfo reservation1 = ConcertReservationInfo.builder()
                .concertId(concertId)
                .concertDateId(concertDates.get(0).getConcertDateId())
                .concertName(concertDates.get(0).getConcert().getName())
                .userId(1L)
                .seatNumber(seat1.getSeatNumber())
                .seatId(seat1.getSeatId())
                .seatPrice(seat1.getPrice())
                .concertDate(DateUtils.getLocalDateTimeToString(LocalDateTime.now()))
                .build();
        concertRepository.saveReservation(reservation1);
        seat1.occupy();
        concertRepository.saveSeat(seat1);


        ConcertReservationInfo reservation2 = ConcertReservationInfo.builder()
                .concertId(concertId)
                .concertDateId(concertDates.get(0).getConcertDateId())
                .concertName(concertDates.get(0).getConcert().getName())
                .userId(1L)
                .seatNumber(seat2.getSeatNumber())
                .seatId(seat2.getSeatId())
                .seatPrice(seat2.getPrice())
                .concertDate(DateUtils.getLocalDateTimeToString(LocalDateTime.now()))
                .build();

        concertRepository.saveReservation(reservation2);
        seat2.occupy();
        concertRepository.saveSeat(seat2);
    }


    // 결제 건 생성
    public void createPayment(Payment.PaymentStatus status) {
        Optional<ConcertReservationInfo> reservation = concertRepository.getReservation(1L);

        paymentRepository.savePayment(Payment.builder()
                .concertReservationInfo(reservation.get())
                .paymentPrice(BigDecimal.valueOf(89000))
                .status(status)
                .build());
    }


}
