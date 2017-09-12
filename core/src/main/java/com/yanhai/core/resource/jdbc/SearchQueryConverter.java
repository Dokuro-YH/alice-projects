package com.yanhai.core.resource.jdbc;

import java.util.Map;

import com.yanhai.core.resource.AttributeNameMapper;

public interface SearchQueryConverter {

    String ORDER_BY_NO_SPACE = "ORDER BY";
    String ORDER_BY = " " + ORDER_BY_NO_SPACE + " ";

    ProcessedFilter convert(String filter, String sortBy, boolean ascending);

    ProcessedFilter convert(String filter, String sortBy, boolean ascending, AttributeNameMapper mapper);

    String map(String attribute);

    final class ProcessedFilter {

        private final String sql;
        private final Map<String, Object> params;
        private final String paramPrefix;
        private final boolean hasOrderBy;

        public ProcessedFilter(String sql, Map<String, Object> params, String paramPrefix, boolean hasOrderBy) {
            this.sql = sql;
            this.params = params;
            this.paramPrefix = paramPrefix;
            this.hasOrderBy = hasOrderBy;
        }

        public String getSql() {
            return sql;
        }

        public boolean hasOrderBy() {
            return hasOrderBy;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public String getParamPrefix() {
            return paramPrefix;
        }

        @Override
        public String toString() {
            return String.format("sql: %s, params: %s", sql, params);
        }
    }

}
