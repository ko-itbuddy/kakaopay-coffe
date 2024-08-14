package org.kakaopay.coffee.api.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kakaopay.coffee.api.order.request.OrderServiceRequest;
import org.kakaopay.coffee.api.order.response.OrderResponse;
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

@Slf4j
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
    public OrderResponse order(OrderServiceRequest request) throws Exception {

        List<Long> menuCodes = request.getOrders().stream().map(OrderVo::getMenuCode).toList();

        // 재고와 확인 주문자의 포인트 확인
        checkUserPointIsUnderOrderTotalPoint(request.getUserId(), request.getOrders(), menuCodes);

        // 주문 접수
        OrderEntity orderEntity = saveOrder(request.getUserId(), request.getOrders(), menuCodes);

        return OrderResponse.builder().orderId(orderEntity.getId()).build();
    }

    private Map<Long, MenuEntity> getLongMenuEntityMapByMenuCodes(List<Long> menuCodes)
        throws Exception {
        Map<Long, MenuEntity> resultMap = new HashMap<>();
        for (Long menuCode : menuCodes) {
            menuJpaReader.findByMenuCode(menuCode)
                         .ifPresent(menuEntity -> resultMap.put(menuCode, menuEntity));
        }
        return resultMap;
    }

    public void checkUserPointIsUnderOrderTotalPoint(Long userId, List<OrderVo> orders,
        List<Long> menuCodes)
        throws Exception {

        Map<Long, MenuEntity> menuEntityMap = getLongMenuEntityMapByMenuCodes(menuCodes);
        Optional<UserEntity> user = userJpaReader.findById(userId);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        int orderTotalPoint = 0;
        for (OrderVo order : orders) {
            MenuEntity tmpMenu;
            tmpMenu = menuEntityMap.get(order.getMenuCode());
            if (tmpMenu.getInventory() < order.getQuantity()) {
                throw new IllegalArgumentException(
                    String.format("menuId:%s 상품의 재고가 충분하지 않습니다.", order.getMenuCode()));
            }
            orderTotalPoint +=
                menuEntityMap.get(order.getMenuCode()).getPrice() * order.getQuantity();
        }

        int userTotalPointMinusOrderTotalPoint = user.get().getPoint() - orderTotalPoint;
        if (userTotalPointMinusOrderTotalPoint < 0) {
            throw new IllegalArgumentException(
                String.format("사용자의 포인트가 %s 만큼 모자랍니다.", userTotalPointMinusOrderTotalPoint));
        }


    }
    @Transactional
    public OrderEntity saveOrder(Long userId, List<OrderVo> orders, List<Long> menuCodes) throws Exception {
        OrderEntity orderEntity = OrderEntity.builder()
                                             .userId(userId)
                                             .build();
        orderJpaManager.save(orderEntity);

        List<OrderMenuEntity> orderMenuEntities = new ArrayList<>();
        Map<Long, MenuEntity> menuEntityMap = getLongMenuEntityMapByMenuCodes(menuCodes);
        List<MenuEntity> menuEntities = new ArrayList<>();
        OrderVo order;
        int orderTotalPoint = 0;


        for (int idx = 0; idx < orders.size(); idx++) {
            order = orders.get(idx);
            MenuEntity tmpMenu = menuEntityMap.get(order.getMenuCode());
            orderMenuEntities.add(OrderMenuEntity.builder()
                                                 .userId(userId)
                                                 .orderId(orderEntity.getId())
                                                 .menuId(tmpMenu.getId())
                                                 .menuCode(order.getMenuCode())
                                                 .orderSequence(idx + 1)
                                                 .quantity(order.getQuantity())
                                                 .build());

            menuEntities.add(tmpMenu);
            menuJpaManager.addInventoryByMenuCode(tmpMenu.getMenuCode(), -order.getQuantity());
            orderTotalPoint += order.getQuantity() * tmpMenu.getPrice();
        }
        orderMenuJpaManager.saveAll(orderMenuEntities);

        userPointHistoryJpaManager.save(UserPointHistoryEntity.builder()
                                                              .userId(userId)
                                                              .point(orderTotalPoint)
                                                              .build());
        userJpaManager.addPointByUserId(userId, -orderTotalPoint);
        return orderEntity;
    }


}
