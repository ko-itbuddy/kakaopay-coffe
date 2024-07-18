package org.kakaopay.coffee.api.menu.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.api.menu.MenuVo;

@Getter
@NoArgsConstructor
public class MenuPopularListResponse {

    private Long totalCount;
    private Long currentPage;
    private List<MenuVo> menus = new ArrayList<>();

    @Builder
    private MenuPopularListResponse(Long totalCount, Long currentPage, List<MenuVo> menus) {
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.menus = menus;
    }
}
