package org.kakaopay.coffee.api.menu.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.api.menu.MenuListSort;

@Getter
@NoArgsConstructor
public class MenuPopularListRequest {

    @PositiveOrZero(message = "page는 0이상의 수 입니다.")
    private int page;

    @Positive(message = "count는 양수입니다.")
    private int count;

    private MenuListSort sort = MenuListSort.NAME_ASC;

    @Builder
    private MenuPopularListRequest(int page, int count, MenuListSort sort) {
        this.page = page;
        this.count = count;
        this.sort = sort;
    }


}
