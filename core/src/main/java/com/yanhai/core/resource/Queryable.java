package com.yanhai.core.resource;

import java.util.List;

/**
 * @author yanhai
 */
public interface Queryable<T> {

    List<T> query(String filter);

    List<T> query(String filter, String sortBy, boolean ascending);

    int deleteByFilter(String filter);

}
