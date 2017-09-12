package com.yanhai.core.resource.jdbc;

public interface LimitSqlAdapter {

    public String getLimitSql(String sql, int index, int size);
}
