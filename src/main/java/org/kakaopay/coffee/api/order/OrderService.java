package org.kakaopay.coffee.api.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.api.order.request.OrderServiceRequest;
import org.kakaopay.coffee.api.order.response.OrderResponse;
import org.kakaopay.coffee.config.distributionlock.DistributedLock;
import org.kakaopay.coffee.db.menu.MenuEntity;
import org.kakaopay.coffee.db.menu.MenuJpaManager;
import org.kakaopay.coffee.db.menu.MenuJpaReader;
import org.kakaopay.coffee.db.order.OrderEntity;
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
import org.kakaopay.coffee.db.userpointhistory.UserPointHistoryJpaReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserJpaReader userJpaReader;
    private final UserJpaManager userJpaManager;

    private final UserPointHistoryJpaReader userPointHistoryJpaReader;
    private final UserPointHistoryJpaManager userPointHistoryJpaManager;

    private final MenuJpaReader menuJpaReader;
    private final MenuJpaManager menuJpaManager;

    private final OrderJpaReader orderJpaReader;
    private final OrderJpaManager orderJpaManager;

    private final OrderMenuJpaReader orderMenuJpaReader;
    private final OrderMenuJpaManager orderMenuJpaManager;


    /*
     * 3. 커피 주문/결제 하기 API
     * */
    @Transactional
    @DistributedLock("#request")
    public OrderResponse order(OrderServiceRequest request) throws Exception {
        Optional<UserEntity> user = userJpaReader.findById(request.getUserId());

        // 존재하는 사용자인지 확인
        checkExistUser(user);

        // 재고 확인
        List<Long> menuIds = request.getOrders().stream().map(OrderVo::getMenuId).toList();
        Map<Long, MenuEntity> menuMap = menuJpaReader.getLongMenuEntityMapByMenuIds(menuIds.stream().sorted().toList());
        checkMenuInventory(request, menuMap);

        // 사용자 포인트가 주문 보다 적은지 확인
        int userTotalPoint = user.get().getPoint();
        int orderTotalPoint = getOrderTotalPoint(request, menuMap);
        checkUserPointIsUnderOrderTotalPoint(userTotalPoint, orderTotalPoint);

        // 주문 접수
        OrderEntity orderEntity = saveOrder(request, user, menuMap,
            orderTotalPoint);

        // kafka 인기 메뉴

        return OrderResponse.builder().orderId(orderEntity.getId()).build();
    }

    public int getOrderTotalPoint(OrderServiceRequest request,
        Map<Long, MenuEntity> menuMap) {
        int orderTotalPoint = 0;

        for (OrderVo orderVo : request.getOrders()) {
            orderTotalPoint += orderVo.getQuantity() * menuMap.get(orderVo.getMenuId()).getPrice();
        }
        return orderTotalPoint;
    }

    public void checkMenuInventory(OrderServiceRequest request,
        Map<Long, MenuEntity> menuMap) {
        MenuEntity tmpMenu;
        int inventoryMinusOrderQuantity = 0;
        for (OrderVo orderVo : request.getOrders()) {
            tmpMenu = menuMap.get(orderVo.getMenuId());
            inventoryMinusOrderQuantity = tmpMenu.getInventory() - orderVo.getQuantity();
            if (inventoryMinusOrderQuantity < 0) {
                throw new IllegalArgumentException(
                    String.format("%s:%s 의 재고가 %s만큼 모자랍니다.", orderVo.getMenuId(), tmpMenu.getName(),
                        -inventoryMinusOrderQuantity));
            }
        }
    }

    public OrderEntity saveOrder(OrderServiceRequest request, Optional<UserEntity> user,
        Map<Long, MenuEntity> menuMap, int orderTotalPoint) throws Exception {
        OrderEntity orderEntity = OrderEntity.builder()
                                             .userId(user.get().getId())
                                             .build();
        orderJpaManager.save(orderEntity);

        List<OrderMenuEntity> orderMenuEntities = new ArrayList<>();
        List<MenuEntity> menuEntities = new ArrayList<>();
        OrderVo order;
        MenuEntity tmpMenu;

        for (int idx = 0; idx < request.getOrders().size(); idx++) {
            order = request.getOrders().get(idx);
            tmpMenu = menuMap.get(order.getMenuId());
            orderMenuEntities.add(OrderMenuEntity.builder()
                                                 .userId(user.get().getId())
                                                 .menuKey(tmpMenu.getMenuKey())
                                                 .orderSequence(idx + 1)
                                                 .quantity(order.getQuantity())
                                                 .build());
            tmpMenu.decrease(order.getQuantity());
            menuEntities.add(tmpMenu);
        }
        orderMenuJpaManager.saveAll(orderMenuEntities);
        menuJpaManager.saveAll(menuEntities);
        userPointHistoryJpaManager.save(UserPointHistoryEntity.builder()
                                                              .userId(user.get().getId())
                                                              .point(orderTotalPoint)
                                                              .build());


        return orderEntity;
    }

    public void checkUserPointIsUnderOrderTotalPoint(int userTotalPoint,
        int orderTotalPoint) {
        int userTotalPointMinusOrderTotalPoint = userTotalPoint - orderTotalPoint;
        if (userTotalPointMinusOrderTotalPoint < 0) {
            throw new IllegalArgumentException(
                String.format("사용자의 포인트가 %s 만큼 모자랍니다.", userTotalPointMinusOrderTotalPoint));
        }
    }



    public void checkExistUser(Optional<UserEntity> user) {
        if (user.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
    }
}
