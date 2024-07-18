package com.hhplus.hhplusconcert.domain.queue.service;

import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueEnums;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueEnterServiceRequest;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueInfo;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueTokenInfo;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueTokenServiceRequest;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.service.UserFinder;
import com.hhplus.hhplusconcert.support.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WaitingQueueService {

    private final JwtUtils jwtUtils;
    private final WaitingQueueAppender waitingQueueAppender;
    private final WaitingQueueReader waitingQueueReader;
    private final WaitingQueueFinder waitingQueueFinder;
    private final UserFinder userFinder;

    /**
     * 토큰 발급을 요청하면 토큰 정보를 반환한다.
     *
     * @param request userId 정보
     * @return WaitingQueueTokenResponse 토큰 정보를 반환한다.
     */
    public WaitingQueueTokenInfo issueToken(WaitingQueueTokenServiceRequest request) {
        // 유저 정보가 있는지 확인
        userFinder.existsUserByUserId(request.userId());
        String token = jwtUtils.createToken(request.userId());

        return waitingQueueReader.readWaitingQueueToken(token);
    }

    /**
     * 대기열에 진입을 요청하면 대기열 정보를 반환한다.
     *
     * @param request userId, token 정보
     * @return WaitingQueueResponse 대기열 정보를 반환한다.
     */
    @Transactional
    public WaitingQueueInfo enterQueue(WaitingQueueEnterServiceRequest request) {
        long waitingNumber = 0;
        long expectedWaitTimeInSeconds = 0;

        // 1. 기존 토큰 있으면 만료시킴
        expiredIfExist(request.userId());
        // 2. 유저 정보 조회
        User user = userFinder.findUserByUserId(request.userId());
        // 3. 대기열 활성 유저 수 확인
        long activeSize = waitingQueueFinder.countWaitingQueueByStatusIs(WaitingQueueStatus.ACTIVE);
        // 토큰 활성화 여부 확인
        boolean isActive = activeSize < WaitingQueueEnums.ACTIVE_USER_COUNT.getInfo();

        if (isActive) {
            // 3-1 유저 진입 활성화
            waitingQueueAppender.appendWaitingQueue(request.toEntity(user, WaitingQueueStatus.ACTIVE));
        } else {
            // 3-2 유저 비활성, 대기열 정보 생성
            waitingNumber = waitingQueueFinder.countWaitingQueueByStatusIs(WaitingQueueStatus.WAIT);
            // 대기 순번 결정
            waitingNumber++;
            // 대기 시간 결정
            expectedWaitTimeInSeconds = Duration.ofMinutes(waitingNumber).toSeconds();
            // 대기열에 저장
            waitingQueueAppender.appendWaitingQueue(request.toEntity(user, WaitingQueueStatus.WAIT));
        }

        return waitingQueueReader.readWaitingQueue(isActive, request.userId(),
                waitingNumber, expectedWaitTimeInSeconds);
    }

    /**
     * 대기열 확인을 요청하면 대기열 정보를 반환한다.
     *
     * @param request userId, token 정보
     * @return WaitingQueueResponse 대기열 정보를 반환한다.
     */
    public WaitingQueueInfo checkQueue(WaitingQueueEnterServiceRequest request) {
        long waitingNumber;
        long expectedWaitTimeInSeconds;

        // 1. 대기열 정보 확인
        WaitingQueue queue = waitingQueueFinder.findWaitingQueueByUserIdAndToken(request.userId(), request.token());

        // 2. 활성화 되지 않은 토큰이면 대기 정보 반환
        waitingNumber = waitingQueueFinder.countWaitingQueueByRequestTimeBeforeAndStatusIs(queue.getRequestTime(),
                WaitingQueueStatus.WAIT);
        // 대기 순번 결정
        waitingNumber++;
        // 대기 시간 결정
        expectedWaitTimeInSeconds = Duration.ofMinutes(waitingNumber).toSeconds();

        return waitingQueueReader.readWaitingQueue(false, request.userId(),
                waitingNumber, expectedWaitTimeInSeconds);
    }

    /**
     * 만료된 토큰 삭제를 요청한다.
     */
    @Transactional
    public void deleteAllExpireToken() {
        waitingQueueAppender.deleteAllExpireToken();
    }

    /**
     * 만약 토큰이 존재한다면 토큰 상태를 expired 시킨다.
     *
     * @param userId userId 정보
     */
    @Transactional
    public void expiredIfExist(Long userId) {
        WaitingQueue existingQueue = waitingQueueFinder.findByUserIdAndStatusIsNot(userId,
                WaitingQueueStatus.EXPIRED);
        if (existingQueue != null) existingQueue.expire(); //토큰 만료
        active(); // 토큰 활성화
    }

    /**
     * 대기열에 있는 토큰을 순차적으로 active 시킨다.
     */
    @Transactional
    public void active() {
        // 1. 활성 유저 수 확인
        long activeSize = waitingQueueFinder.countWaitingQueueByStatusIs(WaitingQueueStatus.ACTIVE);
        long availableActiveCnt = WaitingQueueEnums.ACTIVE_USER_COUNT.getInfo() - activeSize;
        // 2. 활성화 가능 여부 확인
        if (availableActiveCnt > 0) {
            // 2-1 대기열에 있는 유저 토큰 조회
            List<WaitingQueue> waitingQueues = waitingQueueFinder.findAllWaitingQueueByStatusIsOrderByRequestTime(
                    WaitingQueueStatus.WAIT);
            int initIdx = 0;
            // 2-2 활성화 할 수 있는만큼 활성화 진행
            for (WaitingQueue waitingQueue : waitingQueues) {
                if (initIdx++ == availableActiveCnt) break;
                waitingQueue.active();
            }
        }
    }

    /**
     * 시간이 만료된 active token 을 expired 시킨다.
     */
    @Transactional
    public void expire() {
        List<WaitingQueue> tokens = waitingQueueFinder.getActiveOver10min();
        tokens.forEach(WaitingQueue::expireOver10min);
    }
}
