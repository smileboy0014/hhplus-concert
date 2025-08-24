## 트랜잭션의 범위 및 내부 로직 융합에 따른 문제점 파악

### 문제

결제 API 쪽 비즈니스 로직의 트랜잭션 생명주기가 길다.

결제 API 쪽을 한번 살펴보자.

```java

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final UserService userService;
    private final ConcertService concertService;
    private final WaitingQueueService waitingQueueService;

    /**
     * 결제 요청하는 유즈케이스를 실행한다.
     *
     * @param command reservationId, userId 정보
     * @return PaymentResponse 결제 결과를 반환한다.
     */
    @Transactional
    @DistributedLock(key = "'userLock'.concat(':').concat(#command.userId())")
    public Payment pay(PaymentCommand.Create command) {
        // 1. 예약 완료
        ConcertReservationInfo completeReservation = concertService.completeReservation(command);

        // 2. 결제 내역 생성
        Payment payment = paymentService.createPayment(completeReservation);

        // 3. 잔액 차감
        User user = userService.usePoint(completeReservation.getUserId(),
                completeReservation.getSeatPrice());

        // 4. 토큰 만료
        waitingQueueService.forceExpireToken(command.token());
				
				// 5. 결제 정보 반환
        return Payment.builder()
                .paymentId(payment.getPaymentId())
                .paymentPrice(payment.getPaymentPrice())
                .status(payment.getStatus())
                .balance(user.getBalance())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
```

비즈니스 로직은 흐름은 다음과 같다.

```java
1. 예약 완료 -> 2. 결제 내역 생성 -> 3. 잔액 차감 -> 4. 토큰 만료 -> 5. 결제 정보 반환
```

### 원인

그럼 이렇게 트랜잭션의 범위가 큰 이유는 무엇일까?

하나의 트랜잭션에서 **여러가지 비즈니스 로직을 처리하려고 하기 때문**이다.

그럼 이렇게 트랜잭션의 범위가 클 경우 어떤 문제들이 발생할 수 있을까?

- 긴 생명 주기의 Transaction 의 경우, 오랜 시간은 소요되나 후속 작업에 의해 전체 트랜잭션이 실패할 수 있음
- 혹은 트랜잭션 범위 내에서 DB 와 무관한 작업을 수행하고 있는 경우(외부 API 호출), 외부 API 로직이 실패한다면 우리 로직이 성공했어도 롤백 처리가 될 수 있다.

### 해결방법

다양한 해결방법이 있겠지만, **관점 지향적**으로 문제를 살펴보면 문제를 쉽게 해결할 수 있다.

즉, **애플리케이션 이벤트를 통해 관심사를 분리한다면** 트랜잭션 범위도 작아지고, 각 `Event` 에 의해 `본인의 관심사만 수행하도록 하여` 비즈니스 로직간의 의존을 줄일 수 있다!

여기서 주의할 점은, 이벤트를 나눌 때 **각 작업의 관계나 의존**이 어떻게 되는지를 잘 고려해야 한다.

→ `보상 트랜잭션`이나 `SAGA` 패턴을 도입 가능하다.

### 적용

우선 `주요 로직`과 `부가 로직`을 생각해 보았다.

- 주요 로직
    - 예약 완료
    - 결제 내역 생성
    - 포인트 차감
    - 토큰 만료


- 부가 로직
    - 푸쉬 이벤트
    - 결제 정보 전달

**부가 로직**은 결제의 **주요 로직**에 영향을 끼치면 안된다.

이를 토대로 이벤트를 나누면 다음과 같이 나눌 수 있다.

```sql
원래 로직(1. 예약 완료 -> 2. 결제 내역 생성) -> event publish!
 
-> 3. 잔액 차감 (event listen, before commit)
-> 4. 토큰 만료 (event listen, before commit)
-> 5. 푸쉬 이벤트 (event listen, after commit, @async)
-> 6. 결제 정보 전달 (event listen, after commit, @async)
```

여기서 트랜잭션 event listener 로 `@TransactionalEventListener` 사용할 수 있는데, 주요 로직의 문제가 생겼을 때 함께 Rollback이 발생해야함으로 *`TransactionPhase.BEFORE_COMMIT`* 옵션으로 설정하였다.

실제 코드로 적용된 걸 보면 다음과 같다.

- PaymentFacade

```java
@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final ConcertService concertService;

    /**
     * 결제 요청하는 유즈케이스를 실행한다.
     *
     * @param command reservationId, userId 정보
     * @return PaymentResponse 결제 결과를 반환한다.
     */
    @Transactional
    @DistributedLock(key = "'userLock'.concat(':').concat(#command.userId())")
    public Payment pay(PaymentCommand.Create command) {
        // 1. 예약 완료
        ConcertReservationInfo reservation = concertService.completeReservation(command);
        // 2. 결제 진행 및 결제 정보 반환
        return paymentService.pay(reservation, command.token());
    }
    
    ...
    
  }
```

- PaymentService

```java
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher publisher;

    /**
     * 결제를 요청하면 결제 정보를 반환한다.
     *
     * @param reservationInfo 결제 요청 정보
     * @return PaymentResponse 결제 정보를 반환한다.
     */
    @Transactional
    public Payment pay(ConcertReservationInfo reservationInfo, String token) {

        Payment payment = Payment.builder()
                .concertReservationInfo(reservationInfo)
                .paymentPrice(reservationInfo.getSeatPrice())
                .paidAt(now())
                .status(Payment.PaymentStatus.COMPLETE).build();

        // 1. 결제 내역 생성
        Optional<Payment> completePayment = paymentRepository.savePayment(payment);

        if (completePayment.isEmpty()) {
            throw new CustomException(PAYMENT_IS_FAILED, "결제 완료 내역 생성에 실패하였습니다");
        }
        // 2. 결제 완료 이벤트 발행
        publisher.publishEvent(new PaymentEvent(this, reservationInfo, payment, token));

        return completePayment.get();
    }
    
    ...
}
```

이렇게 이벤트를 발행하면 `PaymentEvent` 를 수신하는 모든 Listener들이 동작을 한다.(브로드캐스팅 방식이라고도 함)

- UserEventListener

```java
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserService userService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onPaymentEvent(PaymentEvent event) {
        // 잔액 차감
        userService.usePoint(event.getReservationInfo().getUserId(),
                event.getReservationInfo().getSeatPrice());
    }
}
```

- QueueEventListener

```java
@Component
@RequiredArgsConstructor
public class QueueEventListener {

    private final WaitingQueueService waitingQueueService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onPaymentEvent(PaymentEvent event) {
        waitingQueueService.forceExpireToken(event.getToken());
    }
}
```

- PaymentEventListener

```java
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final DataPlatformClient dataPlatformClient;

    private final PushClient pushClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentEvent(PaymentEvent event) {
        // 결제 정보 전달
        dataPlatformClient.sendPaymentResult(event.getPayment());
        // kakaotalk 알람 전달
        pushClient.pushKakaotalk();
    }
}
```

이렇게 이벤트를 나눠서 관리함으로써 **코드의 모듈성을 높이고,** **각 클래스가 특정 작업에만 집중하도록 도와줍니다.(응집력을 높여줌)**