package org.kakaopay.coffee.api.order.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.api.order.OrderVo;

@Getter
@NoArgsConstructor
public class OrderRequest {

    private String userPhone;
    private List<OrderVo> orders;

    @Builder
    private OrderRequest(String userPhone, List<OrderVo> orderVos) {
        this.userPhone = userPhone;
        this.orders = orderVos;
    }
}
