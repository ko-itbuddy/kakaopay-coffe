package org.kakaopay.coffee.db.menu;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.kakaopay.coffee.api.menu.MenuListSort;
import org.kakaopay.coffee.db.common.BaseJpaReader;
import org.springframework.stereotype.Component;


@Component
public class MenuJpaReader extends BaseJpaReader<MenuEntity, Long, MenuRepository> {

    static private final QMenuEntity menu = new QMenuEntity("menu");

    public MenuJpaReader(MenuRepository menuRepository,
        JPAQueryFactory jpaQueryFactory) {
        super(menuRepository, jpaQueryFactory);
    }

    public List<MenuEntity> findAllByMenuIdsGroupByMenuIdPickLatestMenu(List<Long> menuIds) {
        QMenuEntity menuSub = new QMenuEntity("menuSub");
        return getJpaQueryFactory()
            .select(menu)
            .from(menu)
            .where(menu.menuKey.in(
                    JPAExpressions.select(menuSub.menuKey.max())
                                  .from(menuSub)
                                  .where(menuSub.menuId.in(menuIds))
                                  .orderBy(menuSub.menuId.asc())
                )
            )
            .fetch();
    }


    public List<MenuEntity> findAllGroupByMenuIdPickLatestMenu(Long page, Long count, MenuListSort menuListSort) {
        QMenuEntity menuSub = new QMenuEntity("menuSub");
        return getJpaQueryFactory().select(menu)
                           .from(menu)
                           .where(menu.menuKey.in(JPAExpressions.select(menuSub.menuKey.max())
                                                                .from(menuSub)
                                                                .limit(count)
                                                                .offset(page - 1)
                                                                .orderBy(toQueryOrder(menuSub,
                                                                    menuListSort))
                                                                .groupBy(menuSub.menuId)))
                           .fetch();
    }

    OrderSpecifier<?> toQueryOrder(QMenuEntity qMenuEntity, MenuListSort menuListSort) {
        return switch (menuListSort) {
            case NAME_ASC -> qMenuEntity.name.asc();
            case NAME_DESC -> qMenuEntity.name.desc();
            default -> qMenuEntity.name.asc();
        };
    }

    public Long findAllGroupByMenuIdPickLatestMenuTotalCount() {

        return getJpaQueryFactory()
            .select(menu.menuId.countDistinct())
            .from(menu)
            .fetchFirst();
    }

}
