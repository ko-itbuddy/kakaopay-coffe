package org.kakaopay.coffee.db.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.kakaopay.coffee.config.distributionlock.DistributedLock;
import org.kakaopay.coffee.db.common.BaseJpaManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class UserJpaManager extends BaseJpaManager<UserEntity, Long, UserRepository> {

    static private final QUserEntity user = new QUserEntity("user");

    public UserJpaManager(UserRepository userRepository, JPAQueryFactory jpaQueryFactory) {
        super(userRepository, jpaQueryFactory);
    }

    @Transactional
    @DistributedLock("#userId")
    public long increasePoint(Long userId, Integer point) {
        return getJpaQueryFactory().update(user)
                                   .set(user.point, point)
                                   .where(user.id.eq(userId)).execute();
    }
}
