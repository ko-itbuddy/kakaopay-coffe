package org.kakaopay.coffee.api.menu.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MenuListResponse {

    private Long id;
    private String name;
    private int price;
    private int inventory;

    @Builder
    private MenuListResponse(Long id, String name, int price, int inventory) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.inventory = inventory;
    }
}
