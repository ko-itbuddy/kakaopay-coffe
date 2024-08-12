package org.kakaopay.coffee.api.menu.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.api.menu.MenuVo;
import org.kakaopay.coffee.db.menu.MenuEntity;

@Getter
@NoArgsConstructor
public class MenuPopularListResponse {


    private List<MenuVo> menus = new ArrayList<>();

    @Builder
    private MenuPopularListResponse(List<MenuVo> menus) {
        this.menus = menus;
    }

    public static MenuPopularListResponse of(List<MenuEntity> popularMenuList) {
        return MenuPopularListResponse.builder()
                               .menus(popularMenuList.stream().map(MenuVo::of).collect(
                                   Collectors.toList()))
                               .build();
    }
}
