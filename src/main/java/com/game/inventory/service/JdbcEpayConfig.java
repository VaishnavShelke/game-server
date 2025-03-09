package com.game.inventory.service;


import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcEpayConfig {

	private static final Logger logger = LoggerFactory.getLogger(JdbcEpayConfig.class);
	
	@Bean(name = "tokenmintdatasource")
	@ConfigurationProperties(prefix = "spring.tokenmintglobal.datasource")
	public DataSource tokenMintDataSource() {
		return DataSourceBuilder.create().build();
	}

	
	@Bean(name = "tokenmintjdbctemplate")
	public JdbcTemplate payuEpayJdbcTemplate(@Qualifier("tokenmintdatasource") DataSource ds) {
		try {
			logger.info("INITIALIZATION :: tokenmintjdbctemplate :: JDBC Template Initiated for tokenmint");
			DataSource dsource = new JdbcTemplate(ds).getDataSource();
			if(dsource == null) {
				throw new Exception("Could not initialize datasource");
			}else {
				dsource.getConnection();
			}
		} catch (Exception e) {
			logger.error("INITIALIZATION :: Unable to intitialize jdbc template for tokenmint");
		}
		return new JdbcTemplate(ds);
	}
}
