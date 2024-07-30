package org.kakaopay.coffee.db.userpointhistory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.db.common.BaseJpaReader;
import org.kakaopay.coffee.db.user.UserEntity;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserPointHistoryJpaReader implements BaseJpaReader<UserPointHistoryEntity, Long> {
    private final UserPointHistoryRepository userPointHistoryRepository;
    private final JPAQueryFactory jpaQueryFactor;

    @Override
    public List<UserPointHistoryEntity> findAll() throws Exception {
        return userPointHistoryRepository.findAll();
    }

    @Override
    public List<UserPointHistoryEntity> findAllById(Iterable<Long> ids) throws Exception {
        return userPointHistoryRepository.findAllById(ids);
    }

    @Override
    public Optional<UserPointHistoryEntity> findById(Long id) throws Exception {
        return userPointHistoryRepository.findById(id);
    }
}
