package org.kakaopay.coffee.db.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.kakaopay.coffee.db.common.BaseJpaManager;
import org.springframework.stereotype.Component;


@Component
public class OrderJpaManager extends BaseJpaManager<OrderEntity, Long, OrderRepository> {


    public OrderJpaManager(OrderRepository orderRepository, JPAQueryFactory jpaQueryFactory) {
        super(orderRepository, jpaQueryFactory);
    }
}
