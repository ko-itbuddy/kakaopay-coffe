package org.kakaopay.coffee.api.order.request;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.api.order.OrderVo;

@Getter
@NoArgsConstructor
public class OrderRequest {


    private Long userId;

    @Size(min = 1, message = "주문은 최소 1개 이상입니다.")
    private List<OrderVo> orders;

    @Builder
    private OrderRequest(Long userId, List<OrderVo> orderVos) {
        this.userId = userId;
        this.orders = orderVos;
    }

    public OrderServiceRequest toServiceRequest() {
        return OrderServiceRequest.builder()
                                  .userId(this.userId)
                                  .orderVos(this.orders)
                                  .build();
    }
}
