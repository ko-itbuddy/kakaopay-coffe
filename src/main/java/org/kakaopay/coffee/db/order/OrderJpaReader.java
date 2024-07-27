package org.kakaopay.coffee.db.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.kakaopay.coffee.db.common.BaseJpaReader;
import org.springframework.stereotype.Component;


@Component
public class OrderJpaReader extends BaseJpaReader<OrderEntity, Long, OrderRepository> {


    public OrderJpaReader(OrderRepository orderRepository, JPAQueryFactory jpaQueryFactory) {
        super(orderRepository, jpaQueryFactory);
    }
}
