package org.kakaopay.coffee.db.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.db.common.BaseJpaManager;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class OrderJpaManager implements BaseJpaManager<OrderEntity, Long> {

    private final OrderRepository orderRepository;
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public OrderEntity save(OrderEntity entity) throws Exception {
        return orderRepository.save(entity);
    }

    @Override
    public <S extends OrderEntity> Iterable<S> saveAll(Iterable<S> entities) throws Exception {
        return orderRepository.saveAll(entities);
    }

    @Override
    public <S extends OrderEntity> S saveAndFlush(S entity) throws Exception {
        return orderRepository.saveAndFlush(entity);
    }

    @Override
    public <S extends OrderEntity> Iterable<S> saveAllAndFlush(Iterable<S> entities)
        throws Exception {
        return orderRepository.saveAllAndFlush(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) throws Exception {
        orderRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public void deleteAllInBatch() throws Exception {
        orderRepository.deleteAllInBatch();
    }

    @Override
    public void bulkMerge(List<OrderEntity> userEntities) throws Exception {

    }

}
