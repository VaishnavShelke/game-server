package com.game.inventory.dao;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


import lombok.Data;

@Service
public class RedisMockDAO {
	private static final Logger logger = LoggerFactory.getLogger(RedisMockDAO.class);
	
	@Autowired
	@Qualifier("tokenmintjdbctemplate")
	JdbcTemplate globalJdbcTemplate;
	
	public boolean saveInRedis(String key,String value) {
		logger.info("Saving TransactionInfo to DB with Key {} ",key);
		String latestUpdate = LocalDate.now().toString();
		if(!saveRedisValue( key, value , latestUpdate)) {
			logger.error("Could Not save Transaction to DB");
			return false;
		}else {
			logger.info("Transaction Saved to db successfully");
			return true;
		}
	}

	private boolean saveRedisValue(String key,String value,String latestUpdate) {
		JdbcTemplate jdbcTemplate = globalJdbcTemplate;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO ");
			sb.append(" mock_redis ");
			sb.append(" (redis_key,redis_value,latest_update)");
			sb.append(" VALUES ( ");
			sb.append(" ?, ?, ?)");
			

			String insertQueryString = sb.toString();
			Object[] params = {
					key,value,latestUpdate
			};
			
			int row = jdbcTemplate.update(insertQueryString,params);
			if(row == 1) {
				logger.info("Transaction saved sucessfullly to table Mock Redis ");
				return true;
			}else {
				logger.error("Could not save transaction to table Mock Redis ");
				return false;
			}
			
		}catch (Exception e) {
			logger.error("Error while saving to the database {}",e.getMessage());
			return false;
		}
	}

	public boolean updateRedisValue(String key,String value) {
		logger.info("Updating TransactionInfo to DB Key {}",key);
		String latestUpdate = LocalDate.now().toString();
		if(!updateRedisValue( key, value,latestUpdate)) {
			logger.error("Could Not save Value to Mock Redis");
			return false;
		}else {
			logger.info("Transaction Saved to db successfully");
			return true;
		}
	}

	private boolean updateRedisValue(String key,String value, String latestUpdate) {
		JdbcTemplate jdbcTemplate = globalJdbcTemplate;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE ");
			sb.append(" mock_redis ");
			sb.append(" SET `redis_value` = ?, `latest_update` = ? ");
			sb.append(" WHERE `redis_key` = ? ");
			
			String updateQueryString = sb.toString();
			Object[] params = {
					value,latestUpdate,
					key
			};
			
			int row = jdbcTemplate.update(updateQueryString,params);
			if(row == 1) {
				logger.info("Transaction UPDATED sucessfullly to table Mock Redis ");
				return true;
			}else {
				logger.error("Could not UPDATE to table Mock Redis ");
				return false;
			}
			
		}catch (Exception e) {
			logger.error("Error while UPDATING to the database {}",e.getMessage());
			return false;
		}
	}
	
	
	public String getRedisValue(String key) {
		String query = "SELECT * FROM mock_redis WHERE redis_key=?";
		List<MockRedis> mockRedisList= globalJdbcTemplate.query(query, new BeanPropertyRowMapper<MockRedis>(MockRedis.class),key);
		if(mockRedisList == null || mockRedisList.size()==0) {
			logger.error("Redis Info not present for provider Id {}",key);
			return null;
		}else {
			return mockRedisList.get(0).getRedisValue();
		}
	}	
}


@Data
class MockRedis{
	private String redisKey;
	private String redisValue;
	private String latestUpdate;
}