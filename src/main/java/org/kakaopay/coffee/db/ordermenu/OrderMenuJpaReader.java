package org.kakaopay.coffee.db.ordermenu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.db.common.BaseJpaReader;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class OrderMenuJpaReader implements BaseJpaReader<OrderMenuEntity, Long> {

    private static final QOrderMenuEntity orderMenu = new QOrderMenuEntity("qom");
    private final JPAQueryFactory jpaQueryFactory;
    private final OrderMenuRepository orderMenuRepository;

    public List<OrderMenuEntity> findAllByOrderId(Long orderId){
        return jpaQueryFactory
            .select(orderMenu)
            .from(orderMenu)
            .where(orderMenu.orderId.eq(orderId))
            .fetch();
    }

    @Override
    public List<OrderMenuEntity> findAll() throws Exception {
        return orderMenuRepository.findAll();
    }

    @Override
    public List<OrderMenuEntity> findAllById(Iterable<Long> ids) throws Exception {
        return orderMenuRepository.findAllById(ids);
    }

    @Override
    public Optional<OrderMenuEntity> findById(Long id) throws Exception {
        return orderMenuRepository.findById(id);
    }
}
