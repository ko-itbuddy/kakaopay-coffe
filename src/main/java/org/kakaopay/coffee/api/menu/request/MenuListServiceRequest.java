package org.kakaopay.coffee.api.menu.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.api.menu.MenuListSort;

@Getter
@NoArgsConstructor
public class MenuListServiceRequest {

    @PositiveOrZero(message = "page는 0이상의 수 입니다.")
    private Long page;

    @Positive(message = "count는 양수입니다.")
    private Long count;

    private MenuListSort sort = MenuListSort.NAME_ASC;

    @Builder
    private MenuListServiceRequest(Long page, Long count, MenuListSort sort) {
        this.page = page;
        this.count = count;
        this.sort = sort;
    }
}
