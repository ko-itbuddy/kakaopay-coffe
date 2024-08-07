package org.kakaopay.coffee.api.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.kakaopay.coffee.api.order.request.OrderServiceRequest;
import org.kakaopay.coffee.api.order.response.OrderResponse;
import org.kakaopay.coffee.db.menu.MenuEntity;
import org.kakaopay.coffee.db.menu.MenuJpaManager;
import org.kakaopay.coffee.db.menu.MenuJpaReader;
import org.kakaopay.coffee.db.order.OrderJpaManager;
import org.kakaopay.coffee.db.order.OrderJpaReader;
import org.kakaopay.coffee.db.ordermenu.OrderMenuEntity;
import org.kakaopay.coffee.db.ordermenu.OrderMenuJpaManager;
import org.kakaopay.coffee.db.ordermenu.OrderMenuJpaReader;
import org.kakaopay.coffee.db.user.UserEntity;
import org.kakaopay.coffee.db.user.UserJpaManager;
import org.kakaopay.coffee.db.user.UserJpaReader;
import org.kakaopay.coffee.db.userpointhistory.UserPointHistoryEntity;
import org.kakaopay.coffee.db.userpointhistory.UserPointHistoryJpaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class OrderServiceTest {

    private Long MENU_ID = null;
    private final Integer CONCURRENT_COUNT = 10;
    private final int INIT_USER_POINT = 10000;



    @Autowired
    OrderService orderService;

    @Autowired
    OrderJpaReader orderJpaReader;
    @Autowired
    OrderJpaManager orderJpaManager;

    @Autowired
    OrderMenuJpaReader orderMenuJpaReader;
    @Autowired
    OrderMenuJpaManager orderMenuJpaManager;

    @Autowired
    MenuJpaReader menuJpaReader;
    @Autowired
    MenuJpaManager menuJpaManager;

    @Autowired
    UserPointHistoryJpaManager userPointHistoryJpaManager;

    @Autowired
    UserJpaReader userJpaReader;
    @Autowired
    UserJpaManager userJpaManager;


    @BeforeEach
    void initTest() throws Exception {

        // given
        UserEntity user1 = createUserEntity("010-1111-1111", "울버린", "123456", 9000);
        userJpaManager.save(user1);

        userPointHistoryJpaManager.save(UserPointHistoryEntity.builder()
                                                              .userId(user1.getId())
                                                              .point(9000)
                                                              .build());

        MenuEntity menu1 = createMenuEntity(1L, "아메리카노", 100, 1500);
        MenuEntity menu2 = createMenuEntity(2L, "아이스티", 100, 3000);
        MenuEntity menu3 = createMenuEntity(3L, "카페라떼", 100, 4500);

        menuJpaManager.saveAll(List.of(menu1, menu2, menu3));
        MENU_ID = menu1.getMenuKey();

    }

    @AfterEach
    void tearUp() throws Exception {
        userPointHistoryJpaManager.deleteAllInBatch();
        userJpaManager.deleteAllInBatch();
        orderMenuJpaManager.deleteAllInBatch();
        menuJpaManager.deleteAllInBatch();
        orderJpaManager.deleteAllInBatch();
    }

    @Nested
    @DisplayName("3. 커피 주문/결제하기")
    class Order {

        @Test
        @DisplayName("정상")
        void withSuccessCase() throws Exception {
            // given

            OrderVo orderVo1 = makeOrderVo(1L, 1);
            OrderVo orderVo2 = makeOrderVo(2L, 1);
            OrderVo orderVo3 = makeOrderVo(3L, 1);

            UserEntity user1 = userJpaReader.findAll().get(0);

            OrderServiceRequest request = OrderServiceRequest.builder()
                                                             .userId(user1.getId())
                                                             .orderVos(List.of(orderVo1,
                                                                 orderVo2,
                                                                 orderVo3))
                                                             .build();

            // when
            OrderResponse orderResponse = orderService.order(request);


            // then
            List<MenuEntity> updatedMenu = menuJpaReader.findAllByMenuIdsGroupByMenuIdPickLatestMenu(
                List.of(orderVo1.getMenuId(), orderVo2.getMenuId(), orderVo3.getMenuId()));

            assertThat(updatedMenu)
                      .extracting("menuId", "inventory")
                      .containsExactlyInAnyOrder(
                          tuple(1L, 99),
                          tuple(2L, 99),
                          tuple(3L, 99)
                      );

            List<OrderMenuEntity> savedOrderMenus = orderMenuJpaReader.findAllByOrderId(
                orderResponse.getOrderId());

            assertThat(savedOrderMenus)
                      .extracting("orderId","menuKey", "quantity")
                      .containsExactlyInAnyOrder(
                          tuple(orderResponse.getOrderId(), updatedMenu.get(0).getMenuKey(), 1),
                          tuple(orderResponse.getOrderId(), updatedMenu.get(1).getMenuKey(), 1),
                          tuple(orderResponse.getOrderId(), updatedMenu.get(2).getMenuKey(), 1)
                      );
        }

        @Test
        @DisplayName("분산락 테스트")
        void testRedissonLock() throws Exception {
            // given
            List<UserEntity> users = createUserWithCount(CONCURRENT_COUNT);

            userJpaManager.saveAllAndFlush(users);

            Integer originQuantity = menuJpaReader.findById(MENU_ID).orElseThrow().getInventory();

            ExecutorService executorService = Executors.newFixedThreadPool(32);
            CountDownLatch latch = new CountDownLatch(CONCURRENT_COUNT);

            for (int i = 0; i < CONCURRENT_COUNT; i++) {
                int finalI = i;
                executorService.submit(() -> {
                    try {
                        orderService.order(
                            getOrderByIdx(users, List.of(makeOrderVo(1L, 1)),
                                finalI)
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();


            // then
            MenuEntity resultMenu = menuJpaReader.findById(MENU_ID).orElseThrow();
            assertThat(resultMenu.getInventory()).isEqualTo(originQuantity - CONCURRENT_COUNT);
            List<UserEntity> resultUser = userJpaReader.findAllById(
                users.stream().map(UserEntity::getId).toList());
            assertThat(resultUser).extracting("point")
                                  .containsExactlyInAnyOrder(8500,8500,8500,8500,8500,
                                      8500,8500,8500,8500,8500);

        }

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

    private OrderServiceRequest getOrderByIdx(List<UserEntity> userList, List<OrderVo> orderVoList,
        int idx) {
        return OrderServiceRequest.builder()
                                  .userId(getUserIdByIndex(userList, idx))
                                  .orderVos(orderVoList)
                                  .build();
    }

    private Long getUserIdByIndex(List<UserEntity> userList, int index){
        return userList.get(index % userList.size()).getId();
    }


    private UserEntity createUserEntity(String phone, String name, String password, Integer point) {
        return UserEntity.builder()
                         .phone(phone)
                         .name(name)
                         .password(password)
                         .point(point)
                         .build();
    }

    private OrderVo makeOrderVo(Long menuId, int quantity) {
        return OrderVo.builder()
                      .menuId(menuId)
                      .quantity(quantity)
                      .build();
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