package com.yanhai.core.autoconfiguration;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.yanhai.core.resource.jdbc.DefaultLimitSqlAdapter;
import com.yanhai.core.resource.jdbc.JdbcPagingListFactory;
import com.yanhai.core.resource.jdbc.LimitSqlAdapter;

@Configuration
@ConditionalOnClass({DataSource.class, JdbcTemplate.class})
public class JdbcPagingListFactoryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = {"com.mysql.jdbc.Driver"})
    public LimitSqlAdapter mysqlLimitSqlAdapter() {
        return new DefaultLimitSqlAdapter();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({JdbcTemplate.class, LimitSqlAdapter.class})
    public JdbcPagingListFactory pagingListFactory(JdbcTemplate jdbcTemplate, LimitSqlAdapter limitSqlAdapter) {
        return new JdbcPagingListFactory(jdbcTemplate, limitSqlAdapter);
    }

}
