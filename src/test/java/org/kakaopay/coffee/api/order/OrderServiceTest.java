package org.kakaopay.coffee.api.order;

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
import org.kakaopay.coffee.db.menu.MenuEntity;
import org.kakaopay.coffee.db.menu.MenuRepository;
import org.kakaopay.coffee.api.order.request.OrderServiceRequest;
import org.kakaopay.coffee.api.order.response.OrderResponse;
import org.kakaopay.coffee.db.ordermenu.OrderMenuEntity;
import org.kakaopay.coffee.db.ordermenu.OrderMenuRepository;
import org.kakaopay.coffee.db.user.UserEntity;
import org.kakaopay.coffee.db.userpointhistory.UserPointHistoryEntity;
import org.kakaopay.coffee.db.userpointhistory.UserPointHistoryRepository;
import org.kakaopay.coffee.db.user.UserRepository;
import org.kakaopay.coffee.db.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderMenuRepository orderMenuRepository;
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    UserPointHistoryRepository userPointHistoryRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void initTest() {

        // given
        UserEntity user1 = createUserEntity("010-1111-1111", "울버린", "123456");
        userRepository.save(user1);

        userPointHistoryRepository.save(UserPointHistoryEntity.builder()
                                                              .userId(user1.getId())
                                                              .point(9000)
                                                              .build());

        MenuEntity menu1 = createMenuEntity(1L, "아메리카노", 100, 1500);
        MenuEntity menu2 = createMenuEntity(2L, "아이스티", 100, 3000);
        MenuEntity menu3 = createMenuEntity(3L, "카페라떼", 100, 4500);

        menuRepository.saveAll(List.of(menu1, menu2, menu3));
        MENU_ID = menu1.getMenuKey();

    }

    @AfterEach
    void tearUp() {
        userPointHistoryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        orderMenuRepository.deleteAllInBatch();
        menuRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
    }

    private Long MENU_ID = null;
    private final Integer CONCURRENT_COUNT = 10;

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

            UserEntity user1 = userRepository.findAll().get(0);

            OrderServiceRequest request = OrderServiceRequest.builder()
                                                             .userId(user1.getId())
                                                             .orderVos(List.of(orderVo1,
                                                                 orderVo2,
                                                                 orderVo3))
                                                             .build();

            // when
            OrderResponse orderResponse = orderService.order(request);

            List<OrderMenuEntity> savedOrderMenus = orderMenuRepository.findAllByOrderId(
                orderResponse.getOrderId());
            // then
//            Assertions.assertThat();
        }

        @Test
        @DisplayName("분산락 테스트")
        void testRedissonLock() throws Exception {
            // given

            UserEntity user1 = userRepository.findAll().get(0);
            userPointHistoryRepository.save(UserPointHistoryEntity.builder()
                                                                  .userId(user1.getId())
                                                                  .point(10000000)
                                                                  .build());

            Integer originQuantity = menuRepository.findById(MENU_ID).orElseThrow().getInventory();

            ExecutorService executorService = Executors.newFixedThreadPool(32);
            CountDownLatch latch = new CountDownLatch(CONCURRENT_COUNT);

            for (int i = 0; i < CONCURRENT_COUNT; i++) {
                OrderVo finalOrderVo = makeOrderVo(1L, i+1);
                executorService.submit(() -> {
                    try {
                        orderService.order(OrderServiceRequest.builder()
                                                              .userId(user1.getId())
                                                              .orderVos(List.of(finalOrderVo))
                                                              .build());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

            MenuEntity menu = menuRepository.findById(MENU_ID).orElseThrow();

            Assertions.assertThat(menu.getInventory()).isEqualTo(originQuantity - CONCURRENT_COUNT);

            // then
        }

    }



    private UserEntity createUserEntity(String phone, String name, String password) {
        return UserEntity.builder()
                         .phone(phone)
                         .name(name)
                         .password(password)
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