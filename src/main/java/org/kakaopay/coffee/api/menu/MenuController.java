package org.kakaopay.coffee.api.menu;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.api.common.ApiResponse;
import org.kakaopay.coffee.api.menu.request.MenuListRequest;
import org.kakaopay.coffee.api.menu.response.MenuListResponse;
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
    public ApiResponse<List<MenuListResponse>> getMenuList(@Valid @RequestBody MenuListRequest request) {

        List<MenuListResponse> result = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            result.add(MenuListResponse.builder()
                                       .id(Long.valueOf(i))
                                       .name("coffee" + i)
                                       .inventory(10).price(1500)
                                       .build());
        }

        return ApiResponse.of(HttpStatus.OK, result);
    }

}
