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
    public long addPointByUserId(Long userId, Integer point) {
        UserEntity userEntity = jpaQueryFactory.select(user)
                                               .from(user)
                                               .where(user.id.eq(userId))
                                               .fetchFirst();
        if(userEntity == null) throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        if (point < 0 && userEntity.getPoint() < point)
            throw new IllegalArgumentException("포인트가 부족합니다.");

        return jpaQueryFactory.update(user)
                              .set(user.point, user.point.add(point))
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
