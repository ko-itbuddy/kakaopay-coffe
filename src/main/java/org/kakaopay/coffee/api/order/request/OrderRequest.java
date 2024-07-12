package org.kakaopay.coffee.api.order.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.api.order.Order;

@Getter
@NoArgsConstructor
public class OrderRequest {

    private String userPhone;
    private List<Order> orders;

    @Builder
    private OrderRequest(String userPhone, List<Order> orders) {
        this.userPhone = userPhone;
        this.orders = orders;
    }
}
