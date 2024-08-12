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
    private Long menuCode;

    @NotNull
    @Positive
    private int quantity;

    @Builder
    private OrderVo(Long menuCode, int quantity) {
        this.menuCode = menuCode;
        this.quantity = quantity;
    }
}
