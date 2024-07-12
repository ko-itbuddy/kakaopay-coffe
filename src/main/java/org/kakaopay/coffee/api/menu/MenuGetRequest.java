package org.kakaopay.coffee.api.menu;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MenuGetRequest {
    private int page;
    private int count;
    private MenuGetSort sort;

    @Builder
    private MenuGetRequest(int page, int count, MenuGetSort sort) {
        this.page = page;
        this.count = count;
        this.sort = sort;
    }
}
