package org.kakaopay.coffee.api.menu;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.api.menu.request.MenuListServiceRequest;
import org.kakaopay.coffee.api.menu.response.MenuListResponse;
import org.kakaopay.coffee.api.menu.response.MenuPopularListResponse;
import org.kakaopay.coffee.db.menu.MenuEntity;
import org.kakaopay.coffee.db.menu.MenuJpaReader;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuJpaReader menuJpaReader;

    /*
     * 1. 커피메뉴목록조회
     * */
    public MenuListResponse getMenuList(MenuListServiceRequest request) {

        List<MenuEntity> result = menuJpaReader.findAllGroupByMenuCodePickLatestMenu(request.getPage(),
            request.getCount(), request.getSort());

        Long totalCount = menuJpaReader.findAllGroupByMenuCodePickLatestMenuTotalCount();

        return MenuListResponse.of(result, request.getPage(), totalCount);
    }


    /*
     * 4. 인기메뉴 목록 조회
     * */
    final static String POPULAR_MENU = "POPULAR_MENU";

    @Cacheable(POPULAR_MENU)
    @Transactional(readOnly = true)
    public MenuPopularListResponse getMenuPopularList() {
        return MenuPopularListResponse.of(menuJpaReader.findPopularMenu(LocalDate.now()));
    }

    @Scheduled(cron = "0 0 0 * * *")
    @CacheEvict(POPULAR_MENU)
    public void evictPopularMenuCache() {
    }
}
