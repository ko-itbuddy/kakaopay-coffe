package org.kakaopay.coffee.api.order.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.kakaopay.coffee.api.order.OrderVo;

@Getter
@ToString
@NoArgsConstructor
public class OrderServiceRequest {

    private Long userId;
    private List<OrderVo> orders;

    @Builder
    private OrderServiceRequest(Long userId, List<OrderVo> orderVos) {
        this.userId = userId;
        this.orders = orderVos;
    }
}
