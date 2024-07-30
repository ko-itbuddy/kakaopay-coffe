package org.kakaopay.coffee.db.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.db.common.BaseJpaReader;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class OrderJpaReader implements BaseJpaReader<OrderEntity, Long> {
    private final JPAQueryFactory jpaQueryFactory;
    private final OrderRepository orderRepository;

    @Override
    public List<OrderEntity> findAll() throws Exception {
        return orderRepository.findAll();
    }

    @Override
    public List<OrderEntity> findAllById(Iterable<Long> ids) throws Exception {
        return orderRepository.findAllById(ids);
    }

    @Override
    public Optional<OrderEntity> findById(Long id) throws Exception {
        return orderRepository.findById(id);
    }
}
