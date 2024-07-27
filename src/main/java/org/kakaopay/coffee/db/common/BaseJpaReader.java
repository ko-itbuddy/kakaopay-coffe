package org.kakaopay.coffee.db.common;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Getter
public abstract class BaseJpaReader <Entity, Id, BaseRepository extends JpaRepository<Entity, Id>>{

    private final BaseRepository baseRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional("transactionManager")
    public List<Entity> findAllById(Iterable<Id> ids) throws Exception {
        return this.getBaseRepository().findAllById(ids);
    }

    @Transactional("transactionManager")
    public Optional<Entity> findById(Id id) throws Exception {
        return this.getBaseRepository().findById(id);
    }
}
