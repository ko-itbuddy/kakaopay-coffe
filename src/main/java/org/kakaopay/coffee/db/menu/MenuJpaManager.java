package org.kakaopay.coffee.db.menu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.kakaopay.coffee.db.common.BaseJpaManager;
import org.springframework.stereotype.Component;


@Component
public class MenuJpaManager extends BaseJpaManager<MenuEntity, Long, MenuRepository> {

    public MenuJpaManager(MenuRepository menuRepository,
        JPAQueryFactory jpaQueryFactory) {
        super(menuRepository, jpaQueryFactory);
    }


}
