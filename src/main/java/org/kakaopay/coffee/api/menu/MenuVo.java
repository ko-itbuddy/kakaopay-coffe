package org.kakaopay.coffee.api.menu;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.db.menu.MenuEntity;

@Getter
@NoArgsConstructor
public class MenuVo {

    private Long id;
    private String name;
    private int price;
    private int inventory;

    @Builder
    private MenuVo(Long id, String name, int price, int inventory) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.inventory = inventory;
    }

    public static MenuVo of(MenuEntity menuEntity) {
        return MenuVo.builder()
                     .id(menuEntity.getMenuId())
                     .name(menuEntity.getName())
                     .price(menuEntity.getPrice())
                     .inventory(menuEntity.getInventory())
                     .build();
    }
}
