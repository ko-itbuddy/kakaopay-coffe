package org.kakaopay.coffee.db.ordermenu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.kakaopay.coffee.db.common.BaseJpaReader;
import org.springframework.stereotype.Component;


@Component
public class OrderMenuJpaReader extends BaseJpaReader<OrderMenuEntity, Long, OrderMenuRepository> {


    public OrderMenuJpaReader(OrderMenuRepository orderMenuRepository,
        JPAQueryFactory jpaQueryFactory) {
        super(orderMenuRepository, jpaQueryFactory);
    }
}
