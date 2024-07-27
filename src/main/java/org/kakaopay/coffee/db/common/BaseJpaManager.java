package org.kakaopay.coffee.db.common;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Getter
public abstract class BaseJpaManager<Entity, Id, BaseRepository extends JpaRepository<Entity, Id>>{

    private final BaseRepository baseRepository;
    private final JPAQueryFactory jpaQueryFactory;


    public <S extends Entity> S save(S entity) throws Exception {
        return this.getBaseRepository().save(entity);
    }


    public <S extends Entity> Iterable<S> saveAll(Iterable<S> entities) throws Exception {
        return this.getBaseRepository().saveAll(entities);
    }


    public <S extends Entity> S saveAndFlush(S entity) throws Exception {
        return this.getBaseRepository().saveAndFlush(entity);
    }


    public <S extends Entity> Iterable<S> saveAllAndFlush(Iterable<S> entities) throws Exception {
        return this.getBaseRepository().saveAllAndFlush(entities);
    }


    public void deleteAllByIdInBatch(Iterable<Id> ids) throws Exception {
        this.getBaseRepository().deleteAllByIdInBatch(ids);
    }

    public void deleteAllInBatch() throws Exception {
        this.getBaseRepository().deleteAllInBatch();
    }


    public void bulkMerge(List<Entity> entities) throws Exception {}

}
