package com.hhplus.hhplusconcert.application;

import com.hhplus.hhplusconcert.application.payment.PaymentFacade;
import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo;
import com.hhplus.hhplusconcert.domain.concert.ConcertService;
import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.domain.payment.PaymentService;
import com.hhplus.hhplusconcert.domain.payment.command.PaymentCommand;
import com.hhplus.hhplusconcert.domain.queue.WaitingQueueService;
import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PaymentFacadeTest {

    @Mock
    PaymentService paymentService;
    @Mock
    ConcertService concertService;
    @Mock
    UserService userService;
    @Mock
    WaitingQueueService waitingQueueService;

    @InjectMocks
    PaymentFacade paymentFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("결제 요청을 하는 유즈케이스를 실행한다.")
    void pay() {
        // given
        PaymentCommand.Create command = new PaymentCommand.Create(1L, 1L);
        ConcertReservationInfo reservation = ConcertReservationInfo.builder()
                .userId(1L)
                .seatPrice(BigDecimal.valueOf(100000))
                .build();
        Payment payment = Payment.builder().status(Payment.PaymentStatus.COMPLETE).build();
        User user = User.builder().build();

        when(concertService.completeReservation(command)).thenReturn(reservation);
        when(paymentService.createPayment(reservation)).thenReturn(payment);
        when(userService.usePoint(reservation.getUserId(), reservation.getSeatPrice())).thenReturn(user);

        // when
        Payment result = paymentFacade.pay(command);

        // then
        assertThat(result.getStatus()).isEqualTo(Payment.PaymentStatus.COMPLETE);
    }

}