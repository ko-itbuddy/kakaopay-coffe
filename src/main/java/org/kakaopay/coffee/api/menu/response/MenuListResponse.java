package org.kakaopay.coffee.api.menu.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.api.menu.Menu;

@Getter
@NoArgsConstructor
public class MenuListResponse {

    private Long totalCount;
    private Long currentPage;
    private List<Menu> menus;

    @Builder
    private MenuListResponse(Long totalCount, Long currentPage, List<Menu> menus) {
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.menus = menus;
    }
}
