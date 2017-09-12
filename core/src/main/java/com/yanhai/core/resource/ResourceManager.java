package com.yanhai.core.resource;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author yanhai
 */
public interface ResourceManager<T, ID extends Serializable> {

    T findOne(ID id);

    Collection<T> findAll();

    T create(T resource);

    T update(ID id, T resource);

    void delete(ID id);
}
