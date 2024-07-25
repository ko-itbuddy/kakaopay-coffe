package org.kakaopay.coffee.api.menu.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.api.menu.MenuEntity;
import org.kakaopay.coffee.api.menu.MenuVo;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
public class MenuListResponse {

    private Long totalCount;
    private Long currentPage;
    private List<MenuVo> menus = new ArrayList<>();

    @Builder
    private MenuListResponse(Long totalCount, Long currentPage, List<MenuVo> menus) {
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.menus = menus;
    }

    public static MenuListResponse of(List<MenuEntity> pageResult, Long currentPage, Long totalCount) {
        return MenuListResponse.builder()
                               .currentPage(currentPage)
                               .menus(pageResult.stream().map(MenuVo::of).collect(
                                   Collectors.toList()))
                               .totalCount(totalCount)
                               .build();
    }
}
