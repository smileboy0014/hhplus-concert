package com.hhplus.hhplusconcert.domain.user;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.user.command.UserCommand;
import com.hhplus.hhplusconcert.support.aop.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;

    /**
     * 잔액 조회를 요청하면 유저의 잔액 정보를 반환한다.
     *
     * @param userId userId 정보
     * @return UserResponse 유저의 잔액 정보를 반환한다.
     */
    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        Optional<User> user = userRepository.getUser(userId);

        if (user.isEmpty()) {
            throw new CustomException(USER_IS_NOT_FOUND, "유저가 존재하지 않습니다.");
        }
        return user.get();
    }

    /**
     * 잔액 충전을 요청하면 현재 잔액 정보를 반환한다.
     *
     * @param command userId, balance 정보
     * @return UserResponse 유저의 잔액 정보를 반환한다.
     */
    @Transactional
    @DistributedLock(key = "'userLock'.concat(':').concat(#command.userId())")
    public User chargeBalance(UserCommand.Create command) {
        User user = getUser(command.userId());

        user.chargeBalance(command.balance());
        Optional<User> chargedUser = userRepository.saveUser(user);

        if (chargedUser.isEmpty()) {
            throw new CustomException(USER_FAIL_TO_CHARGE,
                    "잔액 충전에 실패하였습니다");
        }

        return chargedUser.get();
    }

    @Transactional
    public void refund(Long userId, BigDecimal refundPoint) {
        User user = getUser(userId);
        user.chargeBalance(refundPoint);

        userRepository.saveUser(user);
    }

    @Transactional
    public User usePoint(Long userId, BigDecimal usePoint) {
        User user = getUser(userId);
        user.useBalance(usePoint);
        Optional<User> paidUser = userRepository.saveUser(user);

        if (paidUser.isEmpty()) throw new CustomException(USER_FAIL_TO_USE_POINT,
                "유저가 포인트 사용에 실패하였습니다.");

        return paidUser.get();
    }
}
