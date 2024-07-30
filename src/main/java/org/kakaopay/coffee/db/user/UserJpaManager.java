package org.kakaopay.coffee.db.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.config.distributionlock.DistributedLock;
import org.kakaopay.coffee.db.common.BaseJpaManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class UserJpaManager implements BaseJpaManager<UserEntity, Long> {

    static private final QUserEntity user = new QUserEntity("user");

    private final JPAQueryFactory jpaQueryFactory;
    private final UserRepository userRepository;


    @Transactional
    @DistributedLock("#userId")
    public long updateUserPointByUserId(Long userId, Integer point) {
        return jpaQueryFactory.update(user)
                                   .set(user.point, point)
                                   .where(user.id.eq(userId)).execute();
    }

    @Override
    public UserEntity save(UserEntity entity) throws Exception {
        return userRepository.save(entity);
    }

    @Override
    public <S extends UserEntity> Iterable<S> saveAll(Iterable<S> entities) throws Exception {
        return userRepository.saveAll(entities);
    }

    @Override
    public <S extends UserEntity> S saveAndFlush(S entity) throws Exception {
        return userRepository.saveAndFlush(entity);
    }

    @Override
    public <S extends UserEntity> Iterable<S> saveAllAndFlush(Iterable<S> entities)
        throws Exception {
        return userRepository.saveAllAndFlush(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) throws Exception {
        userRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public void deleteAllInBatch() throws Exception {
        userRepository.deleteAllInBatch();
    }

    @Override
    public void bulkMerge(List<UserEntity> userEntities) throws Exception {

    }
}
