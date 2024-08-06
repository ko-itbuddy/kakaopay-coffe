package org.kakaopay.coffee.db.menu;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.api.menu.MenuListSort;
import org.kakaopay.coffee.config.distributionlock.DistributedLock;
import org.kakaopay.coffee.db.common.BaseJpaReader;
import org.kakaopay.coffee.db.order.OrderEntity;
import org.kakaopay.coffee.db.ordermenu.OrderMenuEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class MenuJpaReader implements BaseJpaReader<MenuEntity, Long> {

    static private final QMenuEntity menu = new QMenuEntity("menu");

    private final MenuRepository menuRepository;
    private final JPAQueryFactory jpaQueryFactory;


    public List<MenuEntity> findAllByMenuIdsGroupByMenuIdPickLatestMenu(List<Long> menuIds) {
        QMenuEntity menuSub = new QMenuEntity("menuSub");
        return jpaQueryFactory
            .select(menu)
            .from(menu)
            .where(menu.menuKey.in(
                    JPAExpressions.select(menuSub.menuKey.max())
                                  .from(menuSub)
                                  .where(menuSub.menuId.in(menuIds))
                                  .groupBy(menuSub.menuId)
                                  .orderBy(menuSub.menuId.asc())
                )
            )
            .fetch();
    }

    public List<MenuEntity> findAllGroupByMenuIdPickLatestMenu(Long page, Long count, MenuListSort menuListSort) {
        QMenuEntity menuSub = new QMenuEntity("menuSub");
        return jpaQueryFactory.select(menu)
                           .from(menu)
                           .where(menu.menuKey.in(JPAExpressions.select(menuSub.menuKey.max())
                                                                .from(menuSub)
                                                                .limit(count)
                                                                .offset(page - 1)
                                                                .groupBy(menuSub.menuId)
                                                                .orderBy(toQueryOrder(menuSub,
                                                                    menuListSort))))
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

        return jpaQueryFactory
            .select(menu.menuId.countDistinct())
            .from(menu)
            .fetchFirst();
    }

    @Override
    public List<MenuEntity> findAll() throws Exception {
        return menuRepository.findAll();
    }

    @Override
    public List<MenuEntity> findAllById(Iterable<Long> ids) throws Exception {
        return menuRepository.findAllById(ids);
    }

    @Override
    public Optional<MenuEntity> findById(Long id) throws Exception {
        return menuRepository.findById(id);
    }
}
