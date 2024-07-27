package org.kakaopay.coffee.db.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.kakaopay.coffee.db.common.BaseJpaReader;
import org.springframework.stereotype.Component;


@Component
public class UserJpaReader extends BaseJpaReader<UserEntity, Long, UserRepository> {

    static final QUserEntity user = new QUserEntity("user");

    public UserJpaReader(UserRepository userRepository, JPAQueryFactory jpaQueryFactory) {
        super(userRepository, jpaQueryFactory);
    }

    public Long countByPhone(String phone) {
        return getJpaQueryFactory()
            .select(user.phone.count())
            .from(user)
            .where(user.phone.eq(phone))
            .fetchFirst();
    }

    public Optional<UserEntity> findByPhoneAndPassword(String phone, String password)
        throws Exception {
        return Optional.ofNullable(getJpaQueryFactory()
            .select(user)
            .from(user)
            .where(user.phone.eq(phone).and(user.password.eq(password)))
            .fetchFirst());
    }

}
