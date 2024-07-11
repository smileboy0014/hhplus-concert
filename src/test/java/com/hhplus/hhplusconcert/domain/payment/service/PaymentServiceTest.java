package com.hhplus.hhplusconcert.domain.payment.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomBadRequestException;
import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import com.hhplus.hhplusconcert.domain.payment.repository.PaymentRepository;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PayServiceRequest;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PaymentResponse;
import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.queue.repository.WaitingQueueRepository;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.NOT_AVAILABLE_STATE_PAYMENT;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.NOT_ENOUGH_BALANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WaitingQueueRepository waitingQueueRepository;
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

        Reservation reservation = Reservation.builder().build();

        Payment payment = Payment
                .builder()
                .status(PaymentStatus.WAIT.getStatus())
                .price(BigDecimal.valueOf(50000))
                .reservation(reservation)
                .build();

        User user = User
                .builder()
                .balance(BigDecimal.valueOf(100000))
                .build();

        WaitingQueue waitingQueue = WaitingQueue.builder().build();

        when(paymentRepository.findByReservationId(request.reservationId())).thenReturn(payment);
        when(userRepository.findUserByUserId(request.userId())).thenReturn(user);
        when(waitingQueueRepository.findByUserIdAndStatusIs(1L, WaitingQueueStatus.ACTIVE.getStatus()))
                .thenReturn(waitingQueue);

        //when
        PaymentResponse result = paymentService.pay(request);

        //then
        assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETE.getStatus());
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
                .status(PaymentStatus.COMPLETE.getStatus())
                .build();

        when(paymentRepository.findByReservationId(request.reservationId())).thenReturn(payment);

        //when //then
        assertThatThrownBy(() -> paymentService.pay(request))
                .isInstanceOf(CustomBadRequestException.class)
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
                .status(PaymentStatus.WAIT.getStatus())
                .build();

        User user = User
                .builder()
                .balance(BigDecimal.valueOf(50000))
                .build();

        when(paymentRepository.findByReservationId(request.reservationId())).thenReturn(payment);
        when(userRepository.findUserByUserId(request.userId())).thenReturn(user);


        //when //then
        assertThatThrownBy(() -> paymentService.pay(request))
                .isInstanceOf(CustomBadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(NOT_ENOUGH_BALANCE);
    }


}