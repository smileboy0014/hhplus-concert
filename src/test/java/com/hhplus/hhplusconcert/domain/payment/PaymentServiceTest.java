package com.hhplus.hhplusconcert.domain.payment;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.PAYMENT_ALREADY_CANCEL_OR_REFUND;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.PAYMENT_IS_FAILED;
import static com.hhplus.hhplusconcert.domain.payment.Payment.PaymentStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ApplicationEventPublisher publisher;
    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("결제 요청을 하면 결제 완료 정보를 반환한다.")
    void createPayment() {
        //given
        ConcertReservationInfo reservationInfo = ConcertReservationInfo.builder()
                .seatPrice(BigDecimal.valueOf(500000))
                .build();
        Payment payment = Payment.builder().status(PaymentStatus.COMPLETE).build();

        when(paymentRepository.savePayment(any(Payment.class))).thenReturn(Optional.ofNullable(payment));

        //when
        Payment result = paymentService.pay(reservationInfo, "jwt-token");

        //then
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETE);
    }

    @Test
    @DisplayName("결제 내역 생성 실패 시 PAYMENT_IS_FAILED 예외를 반환한다.")
    void createPaymentWithFailToCreatePayment() {
        // given
        ConcertReservationInfo reservationInfo = ConcertReservationInfo.builder()
                .seatPrice(BigDecimal.valueOf(500000))
                .build();

        // when // then
        assertThatThrownBy(() -> paymentService.pay(reservationInfo, "jwt-token"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PAYMENT_IS_FAILED);
    }

    @Test
    @DisplayName("결제 내역을 취소한다.")
    void cancelPayment() {
        // given
        ConcertReservationInfo reservationInfo = ConcertReservationInfo.builder()
                .reservationId(1L)
                .build();

        Payment paymentResult = Payment.builder().status(PaymentStatus.CANCEL).build();

        when(paymentRepository.getPayment(reservationInfo.getReservationId())).thenReturn(Optional.empty());
        when(paymentRepository.savePayment(any(Payment.class))).thenReturn(Optional.ofNullable(paymentResult));

        // when
        Payment result = paymentService.cancelPayment(reservationInfo);

        // then
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.CANCEL);
    }

    @Test
    @DisplayName("환불 처리 대상인데 이미 취소된 결제라면 PAYMENT_ALREADY_CANCEL_OR_REFUND 예외를 반환한다.")
    void cancelPaymentWithAlreadyCanceled() {
        // given
        ConcertReservationInfo reservationInfo = ConcertReservationInfo.builder()
                .reservationId(1L)
                .build();

        Payment payment = Payment.builder().status(PaymentStatus.CANCEL).build();

        when(paymentRepository.getPayment(reservationInfo.getReservationId())).thenReturn(Optional.ofNullable(payment));

        // when // then
        assertThatThrownBy(() -> paymentService.cancelPayment(reservationInfo))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PAYMENT_ALREADY_CANCEL_OR_REFUND);
    }


    @Test
    @DisplayName("결제 취소 내역 생성에 실패하였다면 PAYMENT_IS_FAILED 예외를 반환한다.")
    void cancelPaymentWithFailToCreateCancelPayment() {
        // given
        ConcertReservationInfo reservationInfo = ConcertReservationInfo.builder()
                .reservationId(1L)
                .build();

        when(paymentRepository.getPayment(reservationInfo.getReservationId())).thenReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> paymentService.cancelPayment(reservationInfo))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PAYMENT_IS_FAILED);
    }


}