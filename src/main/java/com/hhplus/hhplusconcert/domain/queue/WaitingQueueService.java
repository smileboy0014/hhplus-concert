package com.hhplus.hhplusconcert.domain.queue;

import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.support.aop.DistributedLock;
import com.hhplus.hhplusconcert.support.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.hhplus.hhplusconcert.domain.queue.WaitingQueue.WaitingQueueStatus.ACTIVE;
import static com.hhplus.hhplusconcert.domain.queue.WaitingQueue.WaitingQueueStatus.WAIT;
import static com.hhplus.hhplusconcert.domain.queue.WaitingQueueConstants.AUTO_EXPIRED_TIME;
import static com.hhplus.hhplusconcert.domain.queue.WaitingQueueConstants.ENTER_10_SECONDS;

@Service
@RequiredArgsConstructor

public class WaitingQueueService {

    private final JwtUtils jwtUtils;
    private final WaitingQueueRepository waitingQueueRepository;

    /**
     * 토큰의 활성화 여부를 체크하여 토큰 대기열 정보를 반환한다.
     *
     * @param user  user 정보
     * @param token token 정보
     * @return WaitingQueue 대기열 정보
     */
    @Transactional
    @DistributedLock(key = "'waitingQueueLock'")
    public WaitingQueue checkWaiting(User user, String token) {
        // 1. 토큰을 발급한다.
        if (token == null) token = jwtUtils.createToken(user.getUserId());
        // 2. 현재 활성 유저 수 확인
        long activeTokenCnt = waitingQueueRepository.getActiveCnt();
        // 3. 활성화 시킬 수 있는 수 계산
        long availableActiveTokenCnt = WaitingQueue.calculateActiveCnt(activeTokenCnt);

        if (availableActiveTokenCnt > 0) {
            return getInActive(user, token); // 활성화 정보 반환
        }
        return getInWaiting(user, token); // 대기열 정보 반환
    }

    private WaitingQueue getInActive(User user, String token) {
        // 1. 활성 유저열에 추가
        waitingQueueRepository.saveActiveQueue(user, token);
        // 2. ttl 설정
        waitingQueueRepository.setTimeout(token, AUTO_EXPIRED_TIME, TimeUnit.MILLISECONDS);
        // 3. 대기열에서 토큰 정보 제거
        waitingQueueRepository.deleteWaitingQueue(user, token);
        // 4. 활성화 정보 반환
        return WaitingQueue.builder()
                .token(token)
                .user(user)
                .status(ACTIVE)
                .build();
    }

    private WaitingQueue getInWaiting(User user, String token) {
        Long myWaitingNum = waitingQueueRepository.getMyWaitingNum(user, token);
        if (myWaitingNum == null) { // 대기순번이 없다면 대기열에 없는 유저
            // 대기열에 추가
            waitingQueueRepository.saveWaitingQueue(user, token);
            // 내 대기순번 반환
            myWaitingNum = waitingQueueRepository.getMyWaitingNum(user, token);
        }
        // 대기 잔여 시간 계산 (10초당 활성 전환 수)
        long waitTimeInSeconds = (long) Math.ceil((double) (myWaitingNum - 1) / ENTER_10_SECONDS) * 10;

        return WaitingQueue.builder()
                .token(token)
                .user(user)
                .status(WAIT)
                .waitingNum(myWaitingNum)
                .waitTimeInSeconds(waitTimeInSeconds)
                .build();
    }

    /**
     * N초당 M 명씩 active token 으로 전환한다.
     */
    public void activeTokens() {
        // 대기열에서 순서대로 정해진 유저만큼 가져오기
        Set<String> waitingTokens = waitingQueueRepository.getWaitingTokens();
        // 대기열에서 가져온만큼 삭제
        waitingQueueRepository.deleteWaitingTokens();
        // 활성화 열로 유저 변경
        waitingQueueRepository.saveActiveQueues(waitingTokens);
    }

    /**
     * 강제로 active token 을 만료시킨다.
     *
     * @param token token 정보
     */
    public void forceExpireToken(String token) {
        waitingQueueRepository.deleteExpiredToken(token);
    }
}
