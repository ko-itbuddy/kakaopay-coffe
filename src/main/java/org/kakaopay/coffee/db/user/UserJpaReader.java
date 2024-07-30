package org.kakaopay.coffee.db.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.config.distributionlock.DistributedLock;
import org.kakaopay.coffee.db.common.BaseJpaReader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class UserJpaReader implements BaseJpaReader<UserEntity, Long> {

    static final QUserEntity user = new QUserEntity("user");

    private final JPAQueryFactory jpaQueryFactory;
    private final UserRepository userRepository;

    public Long countByPhone(String phone) {
        return jpaQueryFactory
            .select(user.phone.count())
            .from(user)
            .where(user.phone.eq(phone))
            .fetchFirst();
    }

    public Optional<UserEntity> findByPhoneAndPassword(String phone, String password)
        throws Exception {
        return Optional.ofNullable(jpaQueryFactory
            .select(user)
            .from(user)
            .where(user.phone.eq(phone).and(user.password.eq(password)))
            .fetchFirst());
    }

    @Override
    public List<UserEntity> findAll() throws Exception {
        return userRepository.findAll();
    }

    @Override
    public List<UserEntity> findAllById(Iterable<Long> ids) throws Exception {
        return userRepository.findAllById(ids);
    }

    @Override
    public Optional<UserEntity> findById(Long id) throws Exception {
        return userRepository.findById(id);
    }
}
