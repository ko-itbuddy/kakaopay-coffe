package org.kakaopay.coffee.api.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.kakaopay.coffee.api.menu.request.MenuListServiceRequest;
import org.kakaopay.coffee.api.menu.response.MenuListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @AfterEach
    void tearDown() {
        menuRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("1. 커피메뉴목록조회")
    class GetMenuList {

        @Test
        @DisplayName("정상")
        void withSuccessCase() {
            // given
            MenuEntity menu1 = createMenuEntity(1L, "1-아메리카노", 100, 1500);
            MenuEntity menu1_1 = createMenuEntity(1L, "1-아메리카노", 100, 2500);
            MenuEntity menu2 = createMenuEntity(2L, "2-아이스티", 100, 3000);
            MenuEntity menu3 = createMenuEntity(3L, "3-카페라떼", 100, 4500);

            menuRepository.saveAll(List.of(menu1, menu1_1, menu2, menu3));

            MenuListServiceRequest request = MenuListServiceRequest.builder()
                                                                   .page(1L)
                                                                   .count(10L)
                                                                   .sort(MenuListSort.NAME_ASC)
                                                                   .build();

            // when
            MenuListResponse response = menuService.getMenuList(request);

            // then
            assertThat(response.getMenus()).hasSize(3);
            assertThat(response.getCurrentPage()).isEqualTo(1);
            assertThat(response.getTotalCount()).isEqualTo(3);
            assertThat(response.getMenus()).extracting("id", "name", "inventory", "price")
                                           .containsExactly(
                                               tuple(1L, "1-아메리카노", 100, 2500),
                                               tuple(2L, "2-아이스티", 100, 3000),
                                               tuple(3L, "3-카페라떼", 100, 4500)
                                           );
        }
    }

    @Nested
    @DisplayName("4. 인기메뉴 목록 조회")
    class GetMenuPopularList {

        @Test
        @DisplayName("정상")
        void withSuccessCase() {
            // given

            // when

            // then
        }
    }


    private MenuEntity createMenuEntity(Long menuId, String name, Integer inventory,
        Integer price) {
        return MenuEntity.builder()
                         .menuId(menuId)
                         .name(name)
                         .inventory(inventory)
                         .price(price)
                         .build();
    }


}