package com.hhplus.hhplusconcert.domain.payment.service.dto;

import lombok.Builder;

@Builder
public record PayServiceRequest(Long reservationId, Long userId) {

}
