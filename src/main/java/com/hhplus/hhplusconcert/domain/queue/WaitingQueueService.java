package com.hhplus.hhplusconcert.domain.queue;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.support.aop.DistributedLock;
import com.hhplus.hhplusconcert.support.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.TOKEN_ACTIVE_IS_NOT_EXIST;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.TOKEN_IS_NOT_FOUND;

@Service
@RequiredArgsConstructor

public class WaitingQueueService {

    private final JwtUtils jwtUtils;
    private final WaitingQueueRepository waitingQueueRepository;
    private final WaitingQueueValidator waitingQueueValidator;

    /**
     * 토큰 발급을 요청하면 토큰 정보를 반환한다.
     *
     * @param userId userId 정보
     * @return WaitingQueueTokenResponse 토큰 정보를 반환한다.
     */
    public String issueToken(Long userId) {
        //토큰 발급
        return jwtUtils.createToken(userId);
    }

    /**
     * 대기열에 진입을 요청하면 대기열 정보를 반환한다.
     *
     * @param (user,token) user, token 정보
     * @return WaitingQueueResponse 대기열 정보를 반환한다.
     */
    @Transactional
    @DistributedLock(key = "'waitingQueueLock'")
    public WaitingQueue enterQueue(User user, String token) {
        // 현재 활성 유저 수 확인
        long activeTokenCnt = waitingQueueRepository.getActiveCnt();
        // 이미 유저의 활성화 된 토큰이 있다면 expired 시킴
        expiredIfExist(user.getUserId(), activeTokenCnt);
        // 활성화 시킬 수 있는 수 계산
        long availableActiveTokenCnt = WaitingQueue.calculateActiveCnt(activeTokenCnt);
        // 토큰 정보 저장
        WaitingQueue waitingToken = WaitingQueue.toDomain(availableActiveTokenCnt, user, token);

        Optional<WaitingQueue> savedWaitingToken = waitingQueueRepository.saveQueue(waitingToken);
        WaitingQueue waitingTokenInfo = waitingQueueValidator.checkSavedQueue(savedWaitingToken);

        // 만약 활성화 된 토큰이 아니라면 대기열 정보 생성
        if (waitingTokenInfo.getStatus() == WaitingQueue.WaitingQueueStatus.WAIT) {
            long waitingCnt = waitingQueueRepository.getWaitingCnt();
            waitingTokenInfo.addWaitingInfo(waitingCnt, Duration.ofMinutes(waitingCnt).toSeconds());
        }

        return waitingTokenInfo;
    }

    /**
     * 대기열 확인을 요청하면 대기열 정보를 반환한다.
     *
     * @param (userId,token) userId, token 정보
     * @return WaitingQueueResponse 대기열 정보를 반환한다.
     */
    @Transactional(readOnly = true)
    public WaitingQueue checkQueue(Long userId, String token) {

        // 1. 토큰 정보 확인
        Optional<WaitingQueue> tokenInfo = waitingQueueRepository.getToken(userId, token);

        if (tokenInfo.isEmpty()) {
            throw new CustomException(TOKEN_IS_NOT_FOUND,
                    "토큰 정보를 찾을 수 없습니다");
        }
        // 2. 토큰이 활성화 상태인지 확인
        tokenInfo.get().isActive();

        // 3. 활성화 되지 않은 토큰이면 대기 정보 반환
        long waitingCnt = waitingQueueRepository.getWaitingCnt(tokenInfo.get().getRequestTime());
        tokenInfo.get().addWaitingInfo(waitingCnt, Duration.ofMinutes(waitingCnt).toSeconds());

        return tokenInfo.get();
    }

    /**
     * 만약 토큰이 존재한다면 토큰 상태를 expired 시킨다.
     *
     * @param userId userId 정보
     */
    @Transactional
    public void expiredIfExist(Long userId, long activeTokenCnt) {
        List<WaitingQueue> tokens = waitingQueueRepository.getTokens(userId);

        tokens.forEach(token -> {
            token.expire(); //토큰 만료
            waitingQueueRepository.saveQueue(token);
        });

        activeToken(activeTokenCnt); // 토큰 활성화
    }


    /**
     * 대기열에 있는 토큰을 순차적으로 active 시킨다.
     */
    @Transactional
    public void activeToken(Long activeTokenCnt) {
        // 1. 활성 유저 수 확인
        if (activeTokenCnt == null) activeTokenCnt = waitingQueueRepository.getActiveCnt();

        long availableActiveTokenCnt = WaitingQueue.calculateActiveCnt(activeTokenCnt);

        if (availableActiveTokenCnt == 0) throw new CustomException(TOKEN_ACTIVE_IS_NOT_EXIST, "활성화 시킬 토큰이 존재하지 않습니다.");
        // 2-1 대기열에 있는 유저 토큰 조회
        List<WaitingQueue> waitingTokens = waitingQueueRepository.getWaitingTokens();
        // 2-2 활성화 할 수 있는 만큼 활성화 진행
        waitingTokens.stream()
                .limit(availableActiveTokenCnt)
                .forEach(waitingQueue -> {
                    waitingQueue.active();
                    waitingQueueRepository.saveQueue(waitingQueue);
                });
    }

    /**
     * 시간이 만료된 active token 을 expired 시킨다.
     */
    @Transactional
    public void expireToken() {

        // active 된지 10분이 지난 토큰 조회
        List<WaitingQueue> tokens = waitingQueueRepository.getActiveOver10min();

        tokens.forEach(waitingQueue -> {
            waitingQueue.expireOver10min();
            waitingQueueRepository.saveQueue(waitingQueue);
        });
    }

    /**
     * 강제로 토큰을 만료시킨다.
     */
    @Transactional
    public void forceExpireToken(Long userId) {
        Optional<WaitingQueue> activeToken = waitingQueueRepository.getActiveToken(userId);

        if (activeToken.isPresent()) {
            activeToken.get().expire();
            waitingQueueRepository.saveQueue(activeToken.get());
        }
    }

    @Transactional
    public void deleteExpiredToken() {
        waitingQueueRepository.deleteExpiredTokens();
    }
}
