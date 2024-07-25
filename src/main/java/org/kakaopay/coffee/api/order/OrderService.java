package org.kakaopay.coffee.api.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kakaopay.coffee.api.menu.MenuEntity;
import org.kakaopay.coffee.api.menu.MenuRepository;
import org.kakaopay.coffee.api.order.request.OrderServiceRequest;
import org.kakaopay.coffee.api.order.response.OrderResponse;
import org.kakaopay.coffee.api.user.UserEntity;
import org.kakaopay.coffee.api.user.UserPointHistoryEntity;
import org.kakaopay.coffee.api.user.UserPointHistoryRepository;
import org.kakaopay.coffee.api.user.UserRepository;
import org.kakaopay.coffee.config.redisson.RedissonLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final MenuRepository menuRepository;
    private final UserPointHistoryRepository userPointHistoryRepository;
    private final UserRepository userRepository;


    /*
     * 3. 커피 주문/결제 하기 API
     * */
    @Transactional
    @RedissonLock("#order")
    public OrderResponse order(OrderServiceRequest request) throws Exception {
        Optional<UserEntity> user = userRepository.findById(request.getUserId());

        // 존재하는 사용자인지 확인
        checkExistUser(user);

        // 재고 확인
        List<Long> menuIds = request.getOrders().stream().map(OrderVo::getMenuId).toList();
        Map<Long, MenuEntity> menuMap = getLongMenuEntityMap(menuIds);

        checkMenuInventory(request, menuMap);

        // 사용자 포인트가 주문 보다 적은지 확인
        int userTotalPoint = user.get().getPointSum();
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

    public Map<Long, MenuEntity> getLongMenuEntityMap(List<Long> menuIds) {
        List<MenuEntity> menus = menuRepository.findAllGroupByMenuIdPickLatestMenu(menuIds);
        return menus.stream()
                    .collect(Collectors.toMap(MenuEntity::getMenuId,
                        Function.identity()));
    }

    public OrderEntity saveOrder(OrderServiceRequest request, Optional<UserEntity> user,
        Map<Long, MenuEntity> menuMap, int orderTotalPoint) {
        OrderEntity orderEntity = OrderEntity.builder()
                                             .userId(user.get().getId())
                                             .build();
        orderRepository.save(orderEntity);

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
        orderMenuRepository.saveAll(orderMenuEntities);
        menuRepository.saveAll(menuEntities);
        userPointHistoryRepository.save(UserPointHistoryEntity.builder()
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

    public void checkExistUser(Optional<UserEntity> user) {
        if (user.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
    }
}
