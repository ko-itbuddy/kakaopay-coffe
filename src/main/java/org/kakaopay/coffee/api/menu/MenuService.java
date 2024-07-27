package org.kakaopay.coffee.api.menu;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.api.common.ApiResponse;
import org.kakaopay.coffee.api.menu.request.MenuListServiceRequest;
import org.kakaopay.coffee.api.menu.request.MenuPopularListServiceRequest;
import org.kakaopay.coffee.api.menu.response.MenuListResponse;
import org.kakaopay.coffee.api.menu.response.MenuPopularListResponse;
import org.kakaopay.coffee.db.menu.MenuEntity;
import org.kakaopay.coffee.db.menu.MenuJpaReader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuJpaReader menuJpaReader;


    /*
     * 1. 커피메뉴목록조회
     * */
    public MenuListResponse getMenuList(MenuListServiceRequest request) {

        List<MenuEntity> result = menuJpaReader.findAllGroupByMenuIdPickLatestMenu(request.getPage(),
            request.getCount(), request.getSort());

        Long totalCount = menuJpaReader.findAllGroupByMenuIdPickLatestMenuTotalCount();

        return MenuListResponse.of(result, request.getPage(), totalCount);
    }


    /*
     * 4. 인기메뉴 목록 조회
     * */
    public ApiResponse<MenuPopularListResponse> getMenuPopularList(
        MenuPopularListServiceRequest request) {

        MenuPopularListResponse result = new MenuPopularListResponse();

        for (int i = 0; i < 10; i++) {
            result.getMenus().add(MenuVo.builder()
                                        .id(Long.valueOf(i))
                                        .name("coffee" + i).inventory(10).price(1500)
                                        .build());
        }

        return ApiResponse.of(HttpStatus.OK, result);
    }
}
