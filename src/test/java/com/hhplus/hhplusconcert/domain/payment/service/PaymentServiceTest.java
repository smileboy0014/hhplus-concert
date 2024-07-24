package com.hhplus.hhplusconcert.domain.payment.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PayServiceRequest;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PaymentInfo;
import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.queue.service.WaitingQueueFinder;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.service.UserFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.NOT_AVAILABLE_STATE_PAYMENT;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.NOT_ENOUGH_BALANCE;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


class PaymentServiceTest {

    @Mock
    private PaymentFinder paymentFinder;
    @Mock
    private PaymentReader paymentReader;
    @Mock
    private UserFinder userFinder;
    @Mock
    private WaitingQueueFinder waitingQueueFinder;
    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("결제 요청을 하면 결제 완료 정보를 반환한다.")
    void pay() {
        //given
        PayServiceRequest request = PayServiceRequest
                .builder()
                .reservationId(1L)
                .userId(1L)
                .build();

        Reservation reservation = Reservation.builder()
                .status(ReservationStatus.TEMPORARY_RESERVED).build();

        Payment payment = Payment
                .builder()
                .status(PaymentStatus.WAIT)
                .price(BigDecimal.valueOf(50000))
                .reservation(reservation)
                .build();

        Payment afterPayment = Payment
                .builder()
                .status(PaymentStatus.COMPLETE)
                .price(BigDecimal.valueOf(50000))
                .reservation(reservation)
                .build();

        User user = User
                .builder()
                .balance(BigDecimal.valueOf(100000))
                .build();

        User afterUser = User
                .builder()
                .balance(BigDecimal.valueOf(50000))
                .build();

        WaitingQueue waitingQueue = WaitingQueue.builder()
                .requestTime(now()).build();

        PaymentInfo info = PaymentInfo.of(afterPayment, afterUser);

        when(paymentFinder.findPaymentByReservationId(request.reservationId())).thenReturn((payment));
        when(userFinder.findUserByUserIdWithLock(request.userId())).thenReturn((user));
        when(waitingQueueFinder.findWaitingQueueByUserIdAndStatusIs(1L, WaitingQueueStatus.ACTIVE))
                .thenReturn(waitingQueue);
        when(paymentReader.readPayment(payment, user)).thenReturn(info);

        //when
        PaymentInfo result = paymentService.pay(request);

        //then
        assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETE);
    }

    @Test
    @DisplayName("결제 상태가 wait 이 아니면 NOT_AVAILABLE_STATE_PAYMENT 예외를 반환한다.")
    void payWithNoWaitState() {
        //given
        PayServiceRequest request = PayServiceRequest
                .builder()
                .reservationId(1L)
                .userId(1L)
                .build();

        Payment payment = Payment
                .builder()
                .status(PaymentStatus.COMPLETE)
                .build();

        when(paymentFinder.findPaymentByReservationId(request.reservationId())).thenReturn((payment));

        //when //then
        assertThatThrownBy(() -> paymentService.pay(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(NOT_AVAILABLE_STATE_PAYMENT);
    }

    @Test
    @DisplayName("결제 잔액이 충분하지 않으면 NOT_ENOUGH_BALANCE 예외를 반환한다.")
    void payWithNotEnoughBalance() {
        //given
        PayServiceRequest request = PayServiceRequest
                .builder()
                .reservationId(1L)
                .userId(1L)
                .build();

        Payment payment = Payment
                .builder()
                .price(BigDecimal.valueOf(100000))
                .status(PaymentStatus.WAIT)
                .build();

        User user = User
                .builder()
                .balance(BigDecimal.valueOf(50000))
                .build();

        when(paymentFinder.findPaymentByReservationId(request.reservationId())).thenReturn((payment));
        when(userFinder.findUserByUserIdWithLock(request.userId())).thenReturn((user));


        //when //then
        assertThatThrownBy(() -> paymentService.pay(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(NOT_ENOUGH_BALANCE);
    }


}