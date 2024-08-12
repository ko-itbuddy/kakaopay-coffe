package org.kakaopay.coffee.api.menu;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.api.common.ApiResponse;
import org.kakaopay.coffee.api.menu.request.MenuListRequest;
import org.kakaopay.coffee.api.menu.response.MenuListResponse;
import org.kakaopay.coffee.api.menu.response.MenuPopularListResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MenuController {

    private final MenuService menuService;

    /*
     * 1. 커피메뉴목록조회API
     * */
    @GetMapping("api/menus")
    public ApiResponse<MenuListResponse> getMenuList(@Valid @RequestBody MenuListRequest request) {

        MenuListResponse result = menuService.getMenuList(request.toMenuListServiceRequest());

        return ApiResponse.of(HttpStatus.OK, result);
    }

    /*
     * 4. 인기메뉴 목록 조회 API
     * */
    @GetMapping("api/menus/popular")
    public ApiResponse<MenuPopularListResponse> getMenuPopularList() {

        MenuPopularListResponse result = menuService.getMenuPopularList();

        return ApiResponse.of(HttpStatus.OK, result);
    }

}
