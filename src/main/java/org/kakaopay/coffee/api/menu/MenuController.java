package org.kakaopay.coffee.api.menu;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.api.common.ApiResponse;
import org.kakaopay.coffee.api.menu.request.MenuListRequest;
import org.kakaopay.coffee.api.menu.request.MenuPopularListRequest;
import org.kakaopay.coffee.api.menu.response.MenuListResponse;
import org.kakaopay.coffee.api.menu.response.MenuPopularListResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MenuController {

    /*
     * 1. 커피메뉴목록조회API
     * */
    @GetMapping("api/menus")
    public ApiResponse<MenuListResponse> getMenuList(@Valid @RequestBody MenuListRequest request) {

        MenuListResponse result = new MenuListResponse();

        for (int i = 0; i < 10; i++) {
            result.getMenus().add(Menu.builder()
                                       .id(Long.valueOf(i))
                                       .name("coffee" + i).inventory(10).price(1500)
                                       .build());
        }

        return ApiResponse.of(HttpStatus.OK, result);
    }

    /*
     * 4. 인기메뉴 목록 조회 API
     * */
    @GetMapping("api/menus/popular")
    public ApiResponse<MenuPopularListResponse> getMenuPopularList(@Valid @RequestBody MenuPopularListRequest request) {

        MenuPopularListResponse result = new MenuPopularListResponse();

        for (int i = 0; i < 10; i++) {
            result.getMenus().add(Menu.builder()
                                      .id(Long.valueOf(i))
                                      .name("coffee" + i).inventory(10).price(1500)
                                      .build());
        }

        return ApiResponse.of(HttpStatus.OK, result);
    }

}
