package com.hhplus.hhplusconcert.presentation.user;

import com.hhplus.hhplusconcert.domain.user.service.dto.UserResponse;
import com.hhplus.hhplusconcert.presentation.common.dto.ApiResponse;
import com.hhplus.hhplusconcert.presentation.user.dto.UserBalanceRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    // 잔액 조회
    @GetMapping("/{userId}/balance")
    public ApiResponse<UserResponse> getBalance(@PathVariable("userId")
                                                @NotNull(message = "유저 ID는 필수값 입니다.") Long userId) {

        return ApiResponse.ok(new UserResponse(BigDecimal.valueOf(25400)));
    }

    // 잔액 충전
    @PatchMapping("/{userId}/balance")
    public ApiResponse<UserResponse> chargeBalance(@PathVariable("userId")
                                                   @NotNull(message = "유저 ID는 필수값 입니다.") Long userId,
                                                   @RequestBody UserBalanceRequest request) {

        return ApiResponse.ok(new UserResponse(BigDecimal.valueOf(80000)));
    }
}
