package org.kakaopay.coffee.db.userpointhistory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.config.distributionlock.DistributedLock;
import org.kakaopay.coffee.db.common.BaseJpaManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class UserPointHistoryJpaManager implements BaseJpaManager<UserPointHistoryEntity, Long> {

    private final UserPointHistoryRepository userPointHistoryRepository;
    private final JPAQueryFactory jpaQueryFactor;


    @Override
    @Transactional
    public UserPointHistoryEntity save(UserPointHistoryEntity entity) throws Exception {
        return userPointHistoryRepository.save(entity);
    }

    @Override
    public <S extends UserPointHistoryEntity> Iterable<S> saveAll(Iterable<S> entities)
        throws Exception {
        return userPointHistoryRepository.saveAll(entities);
    }

    @Override
    public <S extends UserPointHistoryEntity> S saveAndFlush(S entity) throws Exception {
        return userPointHistoryRepository.saveAndFlush(entity);
    }

    @Override
    public <S extends UserPointHistoryEntity> Iterable<S> saveAllAndFlush(Iterable<S> entities)
        throws Exception {
        return userPointHistoryRepository.saveAllAndFlush(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) throws Exception {
        userPointHistoryRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public void deleteAllInBatch() throws Exception {
        userPointHistoryRepository.deleteAllInBatch();
    }

    @Override
    public void bulkMerge(List<UserPointHistoryEntity> userEntities) throws Exception {

    }
}
