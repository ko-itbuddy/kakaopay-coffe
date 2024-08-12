package org.kakaopay.coffee.db.menu;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.api.menu.MenuListSort;
import org.kakaopay.coffee.db.common.BaseJpaReader;
import org.kakaopay.coffee.db.ordermenu.OrderMenuEntity;
import org.kakaopay.coffee.db.ordermenu.QOrderMenuEntity;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class MenuJpaReader implements BaseJpaReader<MenuEntity, Long> {

    static private final QMenuEntity menu = new QMenuEntity("menu");
    static private final QOrderMenuEntity orderMenu = new QOrderMenuEntity("orderMenu");

    private final MenuRepository menuRepository;
    private final JPAQueryFactory jpaQueryFactory;


    public List<MenuEntity> findAllByMenuCodesGroupByMenuCodePickLatestMenu(List<Long> menuCodes) {
        QMenuEntity menuSub = new QMenuEntity("menuSub");
        return jpaQueryFactory
            .select(menu)
            .from(menu)
            .where(menu.id.in(
                    JPAExpressions.select(menuSub.id.max())
                                  .from(menuSub)
                                  .where(menuSub.menuCode.in(menuCodes))
                                  .groupBy(menuSub.menuCode)
                                  .orderBy(menuSub.menuCode.asc())
                )
            )
            .fetch();
    }

    public List<MenuEntity> findAllGroupByMenuCodePickLatestMenu(Long page, Long count, MenuListSort menuListSort) {
        QMenuEntity menuSub = new QMenuEntity("menuSub");
        return jpaQueryFactory.select(menu)
                           .from(menu)
                           .where(menu.id.in(JPAExpressions.select(menuSub.id.max())
                                                                .from(menuSub)
                                                                .limit(count)
                                                                .offset(page - 1)
                                                                .groupBy(menuSub.menuCode)
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

    public Long findAllGroupByMenuCodePickLatestMenuTotalCount() {

        return jpaQueryFactory
            .select(menu.menuCode.countDistinct())
            .from(menu)
            .fetchFirst();
    }

    public List<MenuEntity> findPopularMenu(LocalDate today) {
        return jpaQueryFactory
            .select(menu)
            .from(menu)
            .where(
                menu.menuCode.in(
                    JPAExpressions.select(orderMenu.menuCode)
                                  .from(orderMenu)
                                  .where(new BooleanBuilder()
                                      .and(orderMenu.createdAt.goe(
                                          today.minusDays(8).atStartOfDay()))
                                      .and(orderMenu.createdAt.lt(today.atStartOfDay())))
                                  .groupBy(orderMenu.menuCode)
                                  .orderBy(orderMenu.menuCode.count().desc())
                                  .limit(3)
                )
            )
            .fetch();
    }

    public Optional<MenuEntity> findByMenuCode(Long menuCode) throws Exception {
        return Optional.ofNullable(jpaQueryFactory
            .select(menu)
            .from(menu)
            .where(
                menu.menuCode.eq(menuCode)
            )
            .fetchFirst());
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
