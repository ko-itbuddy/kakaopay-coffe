package org.kakaopay.coffee.api.order.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderResponse {

    private Long orderId;

    @Builder
    private OrderResponse(Long orderId) {
        this.orderId = orderId;
    }
}
