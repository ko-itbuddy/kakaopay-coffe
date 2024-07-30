package org.kakaopay.coffee.db.common;

import java.util.List;
import java.util.Optional;


public interface BaseJpaReader<T, Id> {


    public List<T> findAll() throws Exception;

    public List<T> findAllById(Iterable<Id> ids) throws Exception;

    public Optional<T> findById(Id id) throws Exception;
}
