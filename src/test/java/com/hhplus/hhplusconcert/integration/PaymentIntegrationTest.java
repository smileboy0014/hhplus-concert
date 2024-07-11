package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.application.payment.PaymentFacade;
import com.hhplus.hhplusconcert.common.utils.DateUtils;
import com.hhplus.hhplusconcert.common.utils.JwtUtils;
import com.hhplus.hhplusconcert.domain.concert.entity.*;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.TicketClass;
import com.hhplus.hhplusconcert.domain.concert.repository.ConcertRepository;
import com.hhplus.hhplusconcert.domain.concert.repository.PlaceRepository;
import com.hhplus.hhplusconcert.domain.concert.repository.ReservationRepository;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ReservationReserveServiceRequest;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import com.hhplus.hhplusconcert.domain.payment.repository.PaymentRepository;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PayServiceRequest;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PaymentResponse;
import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.queue.repository.WaitingQueueRepository;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PaymentIntegrationTest {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private WaitingQueueRepository waitingQueueRepository;
    @Autowired
    private PaymentFacade paymentFacade;

    @BeforeEach
    void setUp() {
        int seatsCnt = 50;

        Place place = placeRepository.addPlace(Place.builder()
                .name("서울대공원")
                .totalSeat(seatsCnt)
                .build());

        Concert concert = concertRepository.addConcert(Concert.builder()
                .name("싸이 흠뻑쇼")
                .place(place)
                .build());

        List<ConcertDate> concertDates = new ArrayList<>();
        concertDates.add(ConcertDate.builder()
                .concertInfo(concert)
                .concertDate(DateUtils.getLocalDateTimeToString(LocalDateTime.of(2024, 6, 25, 13, 0)))
                .placeInfo(place)
                .build());

        List<ConcertDate> addedConcertDates = concertRepository.addConcertDates(concertDates);

        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= seatsCnt; i++) {
            if (i <= 20) { // C class
                seats.add(Seat.builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(120000))
                        .concertDateInfo(addedConcertDates.get(0))
                        .ticketClass(TicketClass.C.getDegree())
                        .status(SeatStatus.UNAVAILABLE.getStatus())
                        .build());
            } else if (i <= 30) { // B class
                seats.add(Seat.builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(150000))
                        .concertDateInfo(addedConcertDates.get(0))
                        .ticketClass(TicketClass.B.getDegree())
                        .status(SeatStatus.UNAVAILABLE.getStatus())
                        .build());
            } else if (i <= 40) { // A class
                seats.add(Seat.builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(170000))
                        .concertDateInfo(addedConcertDates.get(0))
                        .ticketClass(TicketClass.A.getDegree())
                        .status(SeatStatus.UNAVAILABLE.getStatus())
                        .build());
            } else {
                seats.add(Seat.builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(190000))
                        .concertDateInfo(addedConcertDates.get(0))
                        .ticketClass(TicketClass.S.getDegree())
                        .status(SeatStatus.AVAILABLE.getStatus())
                        .build());
            }
        }
        concertRepository.addSeats(seats);

        User user = userRepository.addUser(User.builder().balance(BigDecimal.valueOf(200000)).build());
        ;

        String token = jwtUtils.createToken(user.getUserId());

        WaitingQueue queue = WaitingQueue
                .builder()
                .user(user)
                .token(token)
                .status(WaitingQueueStatus.ACTIVE.getStatus())
                .build();

        waitingQueueRepository.save(queue);

        ReservationReserveServiceRequest request = ReservationReserveServiceRequest
                .builder()
                .concertId(concert.getConcertId())
                .concertDateId(addedConcertDates.get(0).getConcertDateId())
                .seatNumber(45)
                .userId(user.getUserId())
                .build();

        Seat seat = concertRepository.findBySeatConcertDateIdAndSeatNumber(request.concertDateId(), request.seatNumber());
        seat.changeStatus(SeatStatus.UNAVAILABLE.getStatus());

        Reservation reservation = reservationRepository.reserve(request.toReservationEntity(addedConcertDates.get(0), seat));

        paymentRepository.createPayment(request.toPaymentEntity(reservation, seat));
    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("결제 요청을 하면 결제 완료 정보를 반환한다.")
    void pay() {
        List<User> user = userRepository.findAll();
        List<Reservation> reservations = reservationRepository.findAllByUserId(user.get(0).getUserId());

        //given
        PayServiceRequest request = PayServiceRequest
                .builder()
                .reservationId(reservations.get(0).getReservationId())
                .userId(user.get(0).getUserId())
                .build();

        //when
        PaymentResponse result = paymentFacade.pay(request);

        //then
        assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETE.getStatus());
    }

}
