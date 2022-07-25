package com.xdmy.conf;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.xdmy.datasource.DynamicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

	// 主数据源
	@Bean(name = "primaryDataSource")
	@Qualifier("primaryDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.primary")
	public DataSource primaryDataSource() {
		return DruidDataSourceBuilder.create().build();
	}

	// 从数据源
	@Bean(name = "secondaryDataSource")
	@Qualifier("secondaryDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.secondary")
	public DataSource secondaryDataSource() {
		return DruidDataSourceBuilder.create().build();
	}

	// 设置动态数据源
	@Primary
	@Bean(name = "dynamicDataSource")
	public AbstractRoutingDataSource dynamicDataSource(@Qualifier("primaryDataSource") DataSource primaryDataSource,
                                                       @Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put("primary", primaryDataSource);
		targetDataSources.put("secondary", secondaryDataSource);

		AbstractRoutingDataSource routingDataSource = new DynamicDataSource();
		routingDataSource.setTargetDataSources(targetDataSources);
		return routingDataSource;
	}

	// 使用JDBCTemplate
	@Bean(name="jdbcTemplate")
	public JdbcTemplate jdbcTemplate(@Qualifier("dynamicDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}
