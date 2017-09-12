package com.yanhai.core.resource.jdbc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;

import com.yanhai.core.resource.Queryable;
import com.yanhai.core.resource.jdbc.SearchQueryConverter.ProcessedFilter;

public abstract class AbstractQueryable<T> implements Queryable<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractQueryable.class);

    private NamedParameterJdbcTemplate jdbcTemplate;

    private JdbcPagingListFactory pagingListFactory;

    private RowMapper<T> rowMapper;

    private SearchQueryConverter queryConverter = new SimpleSearchQueryConverter();

    private int pageSize = 200;

    protected AbstractQueryable(JdbcTemplate jdbcTemplate, JdbcPagingListFactory pagingListFactory,
                                RowMapper<T> rowMapper) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.pagingListFactory = pagingListFactory;
        this.rowMapper = rowMapper;
    }

    public void setQueryConverter(SearchQueryConverter queryConverter) {
        this.queryConverter = queryConverter;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public int deleteByFilter(String filter) {
        SearchQueryConverter.ProcessedFilter where = queryConverter.convert(filter, null, false);
        log.debug("Filtering groups with SQL: " + where);
        try {
            String completeSql = "DELETE FROM " + getTableName() + " WHERE " + where.getSql();
            log.debug("delete sql: " + completeSql + ", params: " + where.getParams());
            return jdbcTemplate.update(completeSql, where.getParams());
        } catch (DataAccessException e) {
            log.debug("Filter '" + filter + "' generated invalid SQL", e);
            throw new IllegalArgumentException("错误的删除过滤条件: " + filter);
        }
    }

    @Override
    public List<T> query(String filter) {
        return query(filter, null, true);
    }

    @Override
    public List<T> query(String filter, String sortBy, boolean ascending) {
        validateOrderBy(queryConverter.map(sortBy));
        SearchQueryConverter.ProcessedFilter where = queryConverter.convert(filter, sortBy, ascending);
        log.debug("Filtering groups with SQL: " + where);
        List<T> result;
        try {
            String completeSql = getQuerySQL(filter, where);
            log.debug("complete sql: " + completeSql + ", params: " + where.getParams());
            if (pageSize > 0 && pageSize < Integer.MAX_VALUE) {
                result = pagingListFactory.createJdbcPagingList(completeSql, where.getParams(), rowMapper, pageSize);
            } else {
                result = jdbcTemplate.query(completeSql, where.getParams(), rowMapper);
            }
            return result;
        } catch (DataAccessException e) {
            log.debug("Filter '" + filter + "' generated invalid SQL", e);
            throw new IllegalArgumentException("错误的过滤条件: " + filter);
        }
    }

    protected String getQuerySQL(String filter, ProcessedFilter where) {
        if (filter == null || filter.trim().length() == 0) {
            return getBaseSqlQuery();
        }
        if (where.hasOrderBy()) {
            return getBaseSqlQuery() + " where ("
                    + where.getSql().replace(SearchQueryConverter.ORDER_BY, ")" + SearchQueryConverter.ORDER_BY);
        } else {
            return getBaseSqlQuery() + " where (" + where.getSql() + ")";
        }
    }

    protected abstract String getTableName();

    protected abstract String getBaseSqlQuery();

    protected abstract void validateOrderBy(String orderBy) throws IllegalArgumentException;

    protected void validateOrderBy(String orderBy, String fields) throws IllegalArgumentException {
        if (!StringUtils.hasText(orderBy)) {
            return;
        }
        String[] input = StringUtils.commaDelimitedListToStringArray(orderBy);
        Set<String> compare = new HashSet<>();
        StringUtils.commaDelimitedListToSet(fields).stream().forEach(p -> compare.add(p.toLowerCase().trim()));
        boolean allints = true;
        for (String s : input) {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                allints = false;
                if (!compare.contains(s.toLowerCase().trim())) {
                    throw new IllegalArgumentException("错误的排序字段:" + s);
                }
            }
        }
        if (allints) {
            return;
        }

    }
}
