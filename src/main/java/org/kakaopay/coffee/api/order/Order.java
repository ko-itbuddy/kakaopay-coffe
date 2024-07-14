package org.kakaopay.coffee.api.order;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Order {

    private Long menuId;
    private int quantity;

    @Builder
    private Order(Long menuId, int quantity) {
        this.menuId = menuId;
        this.quantity = quantity;
    }
}
