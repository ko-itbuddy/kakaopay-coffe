package org.kakaopay.coffee.api.menu;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl {

    private final JPAQueryFactory queryFactory;

    public List<MenuEntity> findAll(Long page, Long count, MenuListSort menuListSort) {

        QMenuEntity menu = new QMenuEntity("m");
        QMenuEntity iMenu = new QMenuEntity("im");
        menu.menuKey.asc();
        return queryFactory.select(menu)
                           .from(menu)
                           .where(menu.menuKey.in(JPAExpressions.select(iMenu.menuKey.max())
                                                                .from(iMenu)
                                                                .limit(count)
                                                                .offset(page - 1)
                                                                .orderBy(toQueryOrder(iMenu,
                                                                    menuListSort))
                                                                .groupBy(iMenu.menuId)))
                           .fetch();
    }

    public Long findAllTotalCount() {
        QMenuEntity iMenu = new QMenuEntity("im");

        return queryFactory
            .select(iMenu.menuId.countDistinct())
            .from(iMenu)
            .fetchFirst();
    }

    OrderSpecifier<?> toQueryOrder(QMenuEntity qMenuEntity, MenuListSort menuListSort) {
        return switch (menuListSort) {
            case NAME_ASC -> qMenuEntity.name.asc();
            case NAME_DESC -> qMenuEntity.name.desc();
            default -> qMenuEntity.name.asc();
        };
    }
}
