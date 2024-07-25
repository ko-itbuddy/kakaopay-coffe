package org.kakaopay.coffee.api.menu;

import java.util.List;
import org.kakaopay.coffee.config.redisson.RedissonLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

    @Query("SELECT m "
        + "FROM MenuEntity as m "
        + "where m.menuKey in (SELECT max(mi.menuKey) from MenuEntity as mi  WHERE mi.menuId in (:menuIds) group by mi.menuId )"
        + "order by m.menuId")
    List<MenuEntity> findAllGroupByMenuIdPickLatestMenu(@Param("menuIds") List<Long> menuIds);

    @Query("SELECT m "
        + "FROM MenuEntity as m "
        + "WHERE m.menuKey = (SELECT MAX(mi.menuKey) from MenuEntity  as mi where mi.menuId = :menuId group by mi.menuId)")
    MenuEntity findGroupByMenuIdPickLatestMenu(@Param("menuId") Long menuId);

}
