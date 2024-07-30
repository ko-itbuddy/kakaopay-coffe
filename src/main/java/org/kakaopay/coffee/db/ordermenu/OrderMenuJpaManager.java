package org.kakaopay.coffee.db.ordermenu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.config.distributionlock.DistributedLock;
import org.kakaopay.coffee.db.common.BaseJpaManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class OrderMenuJpaManager implements BaseJpaManager<OrderMenuEntity, Long> {

    private final JPAQueryFactory jpaQueryFactory;
    private final OrderMenuRepository orderMenuRepository;


    @Override
    public OrderMenuEntity save(OrderMenuEntity entity) throws Exception {
        return orderMenuRepository.save(entity);
    }

    @Override
    public <S extends OrderMenuEntity> Iterable<S> saveAll(Iterable<S> entities) throws Exception {
        return orderMenuRepository.saveAll(entities);
    }

    @Override
    public <S extends OrderMenuEntity> S saveAndFlush(S entity) throws Exception {
        return orderMenuRepository.saveAndFlush(entity);
    }

    @Override
    public <S extends OrderMenuEntity> Iterable<S> saveAllAndFlush(Iterable<S> entities)
        throws Exception {
        return orderMenuRepository.saveAllAndFlush(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) throws Exception {
        orderMenuRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public void deleteAllInBatch() throws Exception {
        orderMenuRepository.deleteAllInBatch();
    }

    @Override
    public void bulkMerge(List<OrderMenuEntity> userEntities) throws Exception {

    }
}
