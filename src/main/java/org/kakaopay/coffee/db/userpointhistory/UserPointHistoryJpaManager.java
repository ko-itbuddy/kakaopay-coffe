package org.kakaopay.coffee.db.userpointhistory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.kakaopay.coffee.db.common.BaseJpaManager;
import org.springframework.stereotype.Component;


@Component
public class UserPointHistoryJpaManager extends BaseJpaManager<UserPointHistoryEntity, Long, UserPointHistoryRepository> {


    public UserPointHistoryJpaManager(UserPointHistoryRepository userPointHistoryRepository,
        JPAQueryFactory jpaQueryFactory) {
        super(userPointHistoryRepository, jpaQueryFactory);
    }
}
