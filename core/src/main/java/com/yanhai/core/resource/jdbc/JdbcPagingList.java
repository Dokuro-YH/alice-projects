package com.yanhai.core.resource.jdbc;

import java.util.AbstractList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcPagingList<E> extends AbstractList<E> {

    private final int size;

    private final int pageSize;
    private final RowMapper<E> rowMapper;
    private final Map<String, ?> args;
    private final String sql;
    private final NamedParameterJdbcTemplate parameterJdbcTemplate;
    private final LimitSqlAdapter limitSqlAdapter;
    private List<E> current;
    private int start = 0;

    public JdbcPagingList(JdbcTemplate jdbcTemplate, LimitSqlAdapter limitSqlAdapter, String sql, RowMapper<E> rowMapper,
                          int pageSize) {
        this(new NamedParameterJdbcTemplate(jdbcTemplate), limitSqlAdapter, sql, Collections.emptyMap(), rowMapper,
                pageSize);
    }

    public JdbcPagingList(JdbcTemplate jdbcTemplate, LimitSqlAdapter limitSqlAdapter, String sql, Map<String, ?> args,
                          RowMapper<E> rowMapper, int pageSize) {
        this(new NamedParameterJdbcTemplate(jdbcTemplate), limitSqlAdapter, sql, args, rowMapper, pageSize);
    }

    public JdbcPagingList(NamedParameterJdbcTemplate parameterJdbcTemplate, LimitSqlAdapter limitSqlAdapter, String sql,
                          Map<String, ?> args, RowMapper<E> rowMapper, int pageSize) {
        super();
        this.parameterJdbcTemplate = parameterJdbcTemplate;
        this.limitSqlAdapter = limitSqlAdapter;
        this.sql = sql;
        this.args = args;
        this.rowMapper = rowMapper;
        this.pageSize = pageSize;
        this.size = parameterJdbcTemplate.queryForObject(getCountSql(sql), args, Integer.class);
    }

    @Override
    public E get(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (current == null || index - start >= pageSize || index < start) {
            this.current = parameterJdbcTemplate.query(limitSqlAdapter.getLimitSql(sql, index, pageSize), args, rowMapper);
            start = index;
        }
        return current.get(index - start);
    }

    @Override
    public int size() {
        return this.size;
    }

    private String getCountSql(String sql) {
        String result = sql.replaceAll("(?i)select (.+?) from (.+)", "select count(0) from $2");
        int orderByPos = result.toLowerCase().lastIndexOf("order by");
        if (orderByPos >= 0) {
            result = result.substring(0, orderByPos);
        }
        return result;
    }

    @Override
    public Iterator<E> iterator() {
        return new SafeIterator<>(super.iterator());
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException("from " + fromIndex + " to " + toIndex);
        }
        return new SafeIteratorList<>(super.subList(fromIndex, toIndex));
    }

    private static class SafeIteratorList<T> extends AbstractList<T> {

        private final List<T> list;

        public SafeIteratorList(List<T> list) {
            this.list = list;
        }

        @Override
        public Iterator<T> iterator() {
            return new SafeIterator<T>(super.iterator());
        }

        @Override
        public T get(int index) {
            return list.get(index);
        }

        @Override
        public int size() {
            return list.size();
        }
    }

    private static class SafeIterator<T> implements Iterator<T> {

        private final Iterator<T> iterator;

        private boolean polled = false;

        private boolean hasNext = false;

        private T next;

        public SafeIterator(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            if (!polled) {
                polled = true;
                try {
                    next = iterator.next();
                    hasNext = true;
                    return true;
                } catch (NoSuchElementException e) {
                    hasNext = false;
                    return false;
                }
            }
            return hasNext;
        }

        @Override
        public T next() {
            if (hasNext()) {
                polled = false;
                return next;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("不是指的操作: 只读的迭代器");
        }
    }
}
