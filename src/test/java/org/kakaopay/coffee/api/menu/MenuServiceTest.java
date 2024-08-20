package org.kakaopay.coffee.api.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kakaopay.coffee.api.menu.request.MenuListServiceRequest;
import org.kakaopay.coffee.api.menu.response.MenuListResponse;
import org.kakaopay.coffee.api.menu.response.MenuPopularListResponse;
import org.kakaopay.coffee.api.order.OrderService;
import org.kakaopay.coffee.api.order.OrderVo;
import org.kakaopay.coffee.api.order.request.OrderServiceRequest;
import org.kakaopay.coffee.config.TestContainerConfig;
import org.kakaopay.coffee.db.menu.MenuEntity;
import org.kakaopay.coffee.db.menu.MenuJpaManager;
import org.kakaopay.coffee.db.menu.MenuJpaReader;
import org.kakaopay.coffee.db.order.OrderJpaManager;
import org.kakaopay.coffee.db.ordermenu.OrderMenuJpaManager;
import org.kakaopay.coffee.db.user.UserEntity;
import org.kakaopay.coffee.db.user.UserJpaManager;
import org.kakaopay.coffee.db.userpointhistory.UserPointHistoryJpaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(TestContainerConfig.class)
@ContextConfiguration(initializers = TestContainerConfig.IntegrationTestInitializer.class)
class MenuServiceTest {

    private final int INIT_USER_POINT = 10000;
    final Integer CONCURRENT_COUNT = 10;

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuJpaManager menuJpaManager;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserJpaManager userJpaManager;

    @Autowired
    MenuJpaReader menuJpaReader;

    @Autowired
    UserPointHistoryJpaManager userPointHistoryJpaManager;

    @Autowired
    OrderMenuJpaManager orderMenuJpaManager;

    @Autowired
    OrderJpaManager orderJpaManager;


    @BeforeEach
    void init() throws Exception {

        MenuEntity menu1 = createMenuEntity(1L, "1-아메리카노", 100, 1500);
        MenuEntity menu1_1 = createMenuEntity(1L, "1-아메리카노", 100, 2500);
        MenuEntity menu2 = createMenuEntity(2L, "2-아이스티", 100, 3000);
        MenuEntity menu3 = createMenuEntity(3L, "3-카페라떼", 100, 4500);

        menuJpaManager.saveAll(List.of(menu1, menu1_1, menu2, menu3));
    }

    @AfterEach
    void tearDown() throws Exception {
        userPointHistoryJpaManager.deleteAllInBatch();
        userJpaManager.deleteAllInBatch();
        orderMenuJpaManager.deleteAllInBatch();
        menuJpaManager.deleteAllInBatch();
        orderJpaManager.deleteAllInBatch();
    }

    @Nested
    @DisplayName("1. 커피메뉴목록조회")
    class GetMenuList {

        @Test
        @DisplayName("정상")
        void withSuccessCase() throws Exception {
            // given

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
            assertThat(response.getMenus()).extracting("code", "name", "inventory", "price")
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
        @DisplayName("여러주문 생성후 인기순 메뉴 확인")
        void testRedissonLock() throws Exception {
            // given

            List<UserEntity> users = createUserWithCount(CONCURRENT_COUNT);

            userJpaManager.saveAllAndFlush(users);

            CountDownLatch latch = new CountDownLatch(CONCURRENT_COUNT * 3);
            ExecutorService executorService = Executors.newFixedThreadPool(32);

            makeCountDownLatch(latch, executorService, users, 1L, 3, 10);
            makeCountDownLatch(latch, executorService, users, 2L, 2, 5);
            makeCountDownLatch(latch, executorService, users, 3L, 1, 1);

            latch.await();

            MenuPopularListResponse response = menuService.getMenuPopularList(LocalDate.now().plusDays(1));
            // then
            assertThat(response.getMenus())
                .extracting("code", "name", "inventory", "price")
                .containsExactly(
                    tuple(1L, "1-아메리카노", 70, 2500),
                    tuple(2L, "2-아이스티", 90, 3000),
                    tuple(3L, "3-카페라떼", 99, 4500)
                );

        }

    }

    private void makeCountDownLatch(CountDownLatch latch, ExecutorService executorService, List<UserEntity> users, Long menuCode, Integer qty, int repeat) {


        for (int i = 0; i < repeat; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    orderService.order(
                        getOrderByIdx(users, List.of(makeOrderVo(menuCode, qty)),
                            finalI)
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
    }


    private MenuEntity createMenuEntity(Long menuId, String name, Integer inventory,
        Integer price) {
        return MenuEntity.builder()
                         .menuCode(menuId)
                         .name(name)
                         .inventory(inventory)
                         .price(price)
                         .build();
    }

    private List<UserEntity> createUserWithCount(int count) {
        if (9999 < count || 0 > count) {
            throw new RuntimeException("count는 0~9999");
        }
        List<UserEntity> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int number = 9999 - i;
            result.add(
                UserEntity.builder()
                          .phone(String.format("010-%4d-%4d", number, number))
                          .name("user_" + i)
                          .password(String.format("%6d", number))
                          .point(INIT_USER_POINT)
                          .build()
            );
        }
        return result;
    }

    private OrderVo makeOrderVo(Long menuCode, int quantity) {
        return OrderVo.builder()
                      .menuCode(menuCode)
                      .quantity(quantity)
                      .build();
    }

    private OrderServiceRequest getOrderByIdx(List<UserEntity> userList, List<OrderVo> orderVoList,
        int idx) {
        return OrderServiceRequest.builder()
                                  .userId(getUserIdByIndex(userList, idx))
                                  .orderVos(orderVoList)
                                  .build();
    }

    private Long getUserIdByIndex(List<UserEntity> userList, int index) {
        return userList.get(index % userList.size()).getId();
    }


}