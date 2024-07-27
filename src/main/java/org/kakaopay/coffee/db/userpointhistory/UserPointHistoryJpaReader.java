package org.kakaopay.coffee.db.userpointhistory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.kakaopay.coffee.db.common.BaseJpaReader;
import org.springframework.stereotype.Component;


@Component
public class UserPointHistoryJpaReader extends BaseJpaReader<UserPointHistoryEntity, Long, UserPointHistoryRepository> {


    public UserPointHistoryJpaReader(UserPointHistoryRepository userPointHistoryRepository,
        JPAQueryFactory jpaQueryFactory) {
        super(userPointHistoryRepository, jpaQueryFactory);
    }
}
