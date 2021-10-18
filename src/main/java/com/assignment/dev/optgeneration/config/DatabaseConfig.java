package com.assignment.dev.optgeneration.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
public class DatabaseConfig {

	@Autowired
	private Environment env;

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.driver-class-name}")
	private String driverClass;

	@Bean
	public DataSource dataSource() throws SQLException {
		return DataSourceBuilder
				.create()
				.url(env.getProperty("spring.datasource.url"))
				.username(env.getProperty("spring.datasource.username"))
				.password(env.getProperty("spring.datasource.password"))
				.driverClassName(
						env.getProperty("spring.datasource.driver-class-name"))
				.build();
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	@Bean
	public PlatformTransactionManager txManager(DataSource dataSource) {
	    return new DataSourceTransactionManager(dataSource); 
	}
}
