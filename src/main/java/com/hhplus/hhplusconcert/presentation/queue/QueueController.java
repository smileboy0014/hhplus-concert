package com.hhplus.hhplusconcert.presentation.queue;

import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueInfoResponse;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueResponse;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueTokenResponse;
import com.hhplus.hhplusconcert.presentation.common.dto.ApiResponse;
import com.hhplus.hhplusconcert.presentation.queue.dto.QueueEnterRequest;
import com.hhplus.hhplusconcert.presentation.queue.dto.QueueTokenRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/queues")
public class QueueController {

    // 토큰 발급 요청
    @PostMapping("/issue-token")
    public ApiResponse<WaitingQueueTokenResponse> issueToken(@RequestBody @Valid QueueTokenRequest request) {

        return ApiResponse.ok(new WaitingQueueTokenResponse("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"));
    }

    // 대기열 저장
    @PostMapping("/enter")
    public ApiResponse<WaitingQueueResponse> enterQueue(@RequestBody @Valid QueueEnterRequest request) {
        return ApiResponse.ok(new WaitingQueueResponse(request.userId(), false,
                new WaitingQueueInfoResponse(3, 3)));
    }

    // 대기열 저장
    @PostMapping("/check")
    public ApiResponse<WaitingQueueResponse> checkQueue(@RequestBody @Valid QueueEnterRequest request) {
        return ApiResponse.ok(new WaitingQueueResponse(request.userId(), true,
                null));
    }


}
