package org.kakaopay.coffee.api.menu;

import lombok.Builder;
import lombok.Getter;
import org.kakaopay.coffee.api.common.ApiResponse;
import org.springframework.http.HttpStatus;

@Getter
public class MenuResponse {

    private Long id;
    private String name;
    private int price;
    private int inventory;

    @Builder
    private MenuResponse(Long id, String name, int price, int inventory) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.inventory = inventory;
    }
}
