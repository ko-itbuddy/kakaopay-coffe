package org.kakaopay.coffee.db.common;

import java.util.List;

public interface BaseJpaManager<Entity, Id> {

    public <S extends Entity> S save(S entity) throws Exception;


    public <S extends Entity> Iterable<S> saveAll(Iterable<S> entities) throws Exception;

    public <S extends Entity> S saveAndFlush(S entity) throws Exception;


    public <S extends Entity> Iterable<S> saveAllAndFlush(Iterable<S> entities) throws Exception;


    public void deleteAllByIdInBatch(Iterable<Id> ids) throws Exception;

    public void deleteAllInBatch() throws Exception;


    public void bulkMerge(List<Entity> entities) throws Exception;

}
