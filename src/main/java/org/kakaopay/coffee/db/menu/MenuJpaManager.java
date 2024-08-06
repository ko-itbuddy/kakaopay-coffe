package org.kakaopay.coffee.db.menu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.config.distributionlock.DistributedLock;
import org.kakaopay.coffee.db.common.BaseJpaManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class MenuJpaManager implements BaseJpaManager<MenuEntity, Long> {

    final QMenuEntity menu = new QMenuEntity("menu");

    private final MenuRepository menuRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    @DistributedLock("#id")
    public MenuEntity decreaseInventoryById(Long id, int inventory) {
        MenuEntity targetMenu = jpaQueryFactory.select(menu)
                                               .from(menu)
                                               .where(menu.menuKey.eq(id))
                                               .fetchFirst();
        if (targetMenu == null) {
            throw new IllegalArgumentException("존재하지 않는 메뉴 id");
        }
        targetMenu.decreaseInventory(inventory);
        return menuRepository.save(targetMenu);
    }

    @Override
    @Transactional
    @DistributedLock("#entity.id")
    public MenuEntity save(MenuEntity entity) throws Exception {
        return menuRepository.save(entity);
    }

    @Override
    public <S extends MenuEntity> Iterable<S> saveAll(Iterable<S> entities) throws Exception {
        return menuRepository.saveAll(entities);
    }

    @Override
    public <S extends MenuEntity> S saveAndFlush(S entity) throws Exception {
        return menuRepository.saveAndFlush(entity);
    }

    @Override
    public <S extends MenuEntity> Iterable<S> saveAllAndFlush(Iterable<S> entities)
        throws Exception {
        return menuRepository.saveAllAndFlush(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) throws Exception {
        menuRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public void deleteAllInBatch() throws Exception {
        menuRepository.deleteAllInBatch();
    }

    @Override
    public void bulkMerge(List<MenuEntity> userEntities) throws Exception {

    }
}
