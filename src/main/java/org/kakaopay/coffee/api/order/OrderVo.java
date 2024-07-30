package org.kakaopay.coffee.api.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class OrderVo {

    @NotNull
    private Long menuId;

    @NotNull
    @Positive
    private int quantity;

    @Builder
    private OrderVo(Long menuId, int quantity) {
        this.menuId = menuId;
        this.quantity = quantity;
    }
}
