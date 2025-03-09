package com.game.inventory.dao;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.game.inventory.beans.PlayerCredentialsBean;
import com.game.inventory.beans.PlayerInfoEty;
import com.game.inventory.utility.Utility;

@Service
public class PlayerCredentialsDAO {
	private static final Logger logger = LoggerFactory.getLogger(PlayerCredentialsDAO.class);

	@Autowired
	@Qualifier("tokenmintjdbctemplate")
	JdbcTemplate globalJdbcTemplate;
	
	
	public PlayerCredentialsBean getUserByUserCredentials(String playerName,String password) {
		
		PlayerCredentialsBean playerCredentials = getUserByUserIdFromDB(playerName,password);
		if(playerCredentials == null) {
			logger.info("Player Credentials Not Found, Onboardning New Player {}",playerName);
			playerCredentials = generateNewUserAndSave(playerName,password);
		}
		return playerCredentials;
	}
	
	
	private PlayerCredentialsBean generateNewUserAndSave(String playerName, String password) {
		PlayerCredentialsBean plyc = new PlayerCredentialsBean();
		plyc.setPlayerName(playerName);
		plyc.setPlayerPassword(password);
		plyc.setPlayerId(Utility.generateDateTimeId());

		boolean savedtodb = addUserToDB(plyc);
		if(!savedtodb) {
			logger.error("!!!! FATAL !!!! User Credentials Not Saved TO DB!!!!!");
		}
		return plyc;
	}


	public PlayerCredentialsBean getUserByUserIdFromDB(String userName,String password) {
		JdbcTemplate jdbcTemplate = globalJdbcTemplate;
		if(jdbcTemplate != null) {
			String query = "SELECT * FROM player_credentials WHERE player_name=? AND player_password=?";
			try {
				List<PlayerCredentialsBean> playerCredentials= jdbcTemplate.query(query, new BeanPropertyRowMapper<PlayerCredentialsBean>(PlayerCredentialsBean.class),userName,password);
				if(playerCredentials == null || playerCredentials.size()==0) {
					return null;
				}else {
					return playerCredentials.get(0);
				}
			}catch (Exception e) {
				logger.error("Error While fetching playerCredentials for userName {} error :: {}",userName,e.getMessage());
			}
		}
		return null;
	}
	
	
	
	public boolean addUserToDB(PlayerCredentialsBean plyc) {
		JdbcTemplate jdbcTemplate = globalJdbcTemplate;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO ");
			sb.append(" player_credentials ");
			sb.append(" (player_name,player_password,player_id)");
			sb.append(" VALUES ( ");
			sb.append(" ?, ?, ?)");
			

			String insertQueryString = sb.toString();
			Object[] params = {
					plyc.getPlayerName(),plyc.getPlayerPassword(),plyc.getPlayerId()
			};
			
			int row = jdbcTemplate.update(insertQueryString,params);
			if(row == 1) {
				logger.info("player_credentials saved sucessfullly to table {} ","player_info");
				return true;
			}else {
				logger.error("Could not save player_credentials to table {} ","player_info");
				return false;
			}
			
		}catch (Exception e) {
			logger.error("Error while saving player_credentials to the database {}",e.getMessage());
			return false;
		}
	}
}
