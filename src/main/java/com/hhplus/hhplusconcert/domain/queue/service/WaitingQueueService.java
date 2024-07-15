package com.hhplus.hhplusconcert.domain.queue.service;

import com.hhplus.hhplusconcert.common.utils.JwtUtils;
import com.hhplus.hhplusconcert.domain.common.exception.CustomBadRequestException;
import com.hhplus.hhplusconcert.domain.common.exception.CustomNotFoundException;
import com.hhplus.hhplusconcert.domain.common.exception.ErrorCode;
import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueEnums;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.queue.repository.WaitingQueueRepository;
import com.hhplus.hhplusconcert.domain.queue.service.dto.*;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.NOT_EXIST_IN_WAITING_QUEUE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_IS_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WaitingQueueService {

    private final JwtUtils jwtUtils;
    private final WaitingQueueRepository waitingQueueRepository;
    private final UserRepository userRepository;

    /**
     * 토큰 발급을 요청하면 토큰 정보를 반환한다.
     *
     * @param request userId 정보
     * @return WaitingQueueTokenResponse 토큰 정보를 반환한다.
     */
    public WaitingQueueTokenResponse issueToken(WaitingQueueTokenServiceRequest request) {
        // 유저 정보 조회
        boolean isExistUser = userRepository.existsByUserId(request.userId());
        // 유저 검증
        validUser(request, isExistUser);

        return WaitingQueueTokenResponse.of(jwtUtils.createToken(request.userId()));
    }

    /**
     * 대기열에 진입을 요청하면 대기열 정보를 반환한다.
     *
     * @param request userId, token 정보
     * @return WaitingQueueResponse 대기열 정보를 반환한다.
     */
    @Transactional
    public WaitingQueueResponse enterQueue(WaitingQueueEnterServiceRequest request) {
        long waitingNumber = 0;
        long expectedWaitTimeInSeconds = 0;

        // 1. 기존 토큰 있으면 만료시킴
        expiredIfExist(request.userId());

        // 2. 유저 정보 있나 확인
        User user = userRepository.findUserByUserId(request.userId());

        // 3. 대기열 활성 유저 수 확인
        long activeSize = waitingQueueRepository.countByStatusIs(WaitingQueueStatus.ACTIVE.getStatus());

        // 토큰 활성화 여부 체크
        boolean isActive = activeSize < WaitingQueueEnums.ACTIVE_USER_COUNT.getInfo();

        if (isActive) {
            // 3-1 유저 진입 활성화
            waitingQueueRepository.save(request.toEntity(user, WaitingQueueStatus.ACTIVE.getStatus()));
        } else {
            // 3-2 유저 비활성, 대기열 정보 생성
            waitingNumber = waitingQueueRepository.countByStatusIs(WaitingQueueStatus.WAIT.getStatus());
            // 대기 순번 결정
            waitingNumber++;
            // 대기 시간 결정
            expectedWaitTimeInSeconds = Duration.ofMinutes(waitingNumber).toSeconds();
            // 대기열에 저장
            waitingQueueRepository.save(request.toEntity(user, WaitingQueueStatus.WAIT.getStatus()));
        }

        return WaitingQueueResponse.of(isActive, request.userId(),
                WaitingQueueInfoResponse.of(waitingNumber, expectedWaitTimeInSeconds));
    }

    /**
     * 대기열 확인을 요청하면 대기열 정보를 반환한다.
     *
     * @param request userId, token 정보
     * @return WaitingQueueResponse 대기열 정보를 반환한다.
     */
    public WaitingQueueResponse checkQueue(WaitingQueueEnterServiceRequest request) {
        long waitingNumber;
        long expectedWaitTimeInSeconds;

        // 1. 대기열 정보 있나 확인
        WaitingQueue queue = waitingQueueRepository.findByUserIdAndToken(request.userId(), request.token());

        validWaitingQueue(queue);

        // 2. 활성 여부 확인
        boolean isActive = queue.getStatus().equals(WaitingQueueStatus.ACTIVE.getStatus());

        // 2-1 이미 활성화 된 토큰일 경우 예외 반환
        if (isActive) {
            throw new CustomBadRequestException(ErrorCode.ALREADY_TOKEN_IS_ACTIVE, "이미 활성화 된 토큰입니다.");
        }

        // 2-2 활성화 되지 않은 토큰이면 대기 정보 반환
        waitingNumber = waitingQueueRepository.countByRequestTimeBeforeAndStatusIs(queue.getRequestTime(),
                WaitingQueueStatus.WAIT.getStatus());

        // 대기 순번 결정
        waitingNumber++;

        expectedWaitTimeInSeconds = Duration.ofMinutes(waitingNumber).toSeconds();

        return WaitingQueueResponse.of(isActive, request.userId(),
                WaitingQueueInfoResponse.of(waitingNumber, expectedWaitTimeInSeconds));
    }

    /**
     * 만료된 토큰 삭제를 요청한다.
     */
    public void deleteAllExpireToken() {
        waitingQueueRepository.deleteAllExpireToken();
    }

    /**
     * 만약 토큰이 존재한다면 토큰 상태를 expired 시킨다.
     *
     * @param userId userId 정보
     */
    @Transactional
    public void expiredIfExist(Long userId) {
        WaitingQueue existingQueue = waitingQueueRepository.findByUserIdAndStatusIsNot(userId,
                WaitingQueueStatus.EXPIRED.getStatus());
        if (existingQueue != null) existingQueue.changeTokenStatus(WaitingQueueStatus.EXPIRED.getStatus());
        activeToken();
    }

    /**
     * 만료시간이 된 토큰을 만료시키고, 대기열에 있는 토큰을 순차적으로 active 시킨다.
     */
    @Transactional
    public void activeToken() {

        // 1. 활성 유저 수 확인
        long activeSize = waitingQueueRepository.countByStatusIs(WaitingQueueStatus.ACTIVE.getStatus());

        // 활성화 여부 체크
        boolean isActive = activeSize < WaitingQueueEnums.ACTIVE_USER_COUNT.getInfo();

        if (isActive) {
            // 2-1 대기열에 있는 유저 토큰 조회
            List<WaitingQueue> waitingQueues = waitingQueueRepository.findAllByStatusIsOrderByRequestTime(
                    WaitingQueueStatus.WAIT.getStatus());

            long availableActiveCnt = WaitingQueueEnums.ACTIVE_USER_COUNT.getInfo() - activeSize;
            int initIdx = 0;

            // 2-1 활성화 할 수 있는만큼 활성화 진행
            for (WaitingQueue w : waitingQueues) {
                if (initIdx++ == availableActiveCnt) break;
                w.activeToken();
            }
        }
    }

    private void validUser(WaitingQueueTokenServiceRequest request, boolean isExistUser) {
        if (!isExistUser) throw new CustomNotFoundException(USER_IS_NOT_FOUND,
                "유저 정보가 존재하지 않습니다. [userId : %d]".formatted(request.userId()));
    }

    private void validWaitingQueue(WaitingQueue queue) {
        if (queue == null || queue.getStatus().equals(WaitingQueueStatus.EXPIRED.getStatus())) {
            throw new CustomBadRequestException(NOT_EXIST_IN_WAITING_QUEUE,
                    "대기열에 토큰이 존재하지 않습니다. 다시 대기열에 진입해주세요.");
        }
    }

}
