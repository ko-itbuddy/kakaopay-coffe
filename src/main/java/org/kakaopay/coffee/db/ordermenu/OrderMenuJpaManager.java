package org.kakaopay.coffee.db.ordermenu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.kakaopay.coffee.db.common.BaseJpaManager;
import org.springframework.stereotype.Component;


@Component
public class OrderMenuJpaManager extends BaseJpaManager<OrderMenuEntity, Long, OrderMenuRepository> {


    public OrderMenuJpaManager(OrderMenuRepository orderMenuRepository,
        JPAQueryFactory jpaQueryFactory) {
        super(orderMenuRepository, jpaQueryFactory);
    }
}
