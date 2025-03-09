package com.game.inventory.dao;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.game.inventory.beans.InventoryInfoBean;
import com.game.inventory.beans.PlayerCredentialsBean;
import com.game.inventory.beans.PlayerInfoEty;
import com.game.inventory.beans.PlayerInfoJson;
import com.game.inventory.utility.Utility;

@Service
public class PlayerInfoDAO {
	

	private static final Logger logger = LoggerFactory.getLogger(PlayerInfoDAO.class);

	@Autowired
	@Qualifier("tokenmintjdbctemplate")
	JdbcTemplate globalJdbcTemplate;

	@Autowired
	PlayerCredentialsDAO playerCredentialsDAO;
	
	
	public PlayerInfoJson getUserByUserId(String playerName,String password) {
		
		PlayerCredentialsBean plyc = playerCredentialsDAO.getUserByUserCredentials(playerName, password);
		logger.info("Player Credentials {}",plyc);
		PlayerInfoEty plyEty = getPlayerByPlayerIdFromDB(plyc.getPlayerId());
		PlayerInfoJson playerInfo = null;
		if(plyEty != null) {
			playerInfo = Utility.parseJsonToObject(plyEty.getPlayerInfo(),PlayerInfoJson.class);
		}
		
		if(playerInfo == null) {
			playerInfo = generateNewUserAndSave(plyc);
		}
		return playerInfo;
	}
	
	public PlayerInfoJson getPlayerInfoJsonFromDBByPlayerId(String playerId) {
		PlayerInfoEty plyEty = getPlayerByPlayerIdFromDB(playerId);
		if(plyEty == null) {
			return null;
		}else {
			return Utility.parseJsonToObject(plyEty.getPlayerInfo(),PlayerInfoJson.class );
		}
	}
	
	private PlayerInfoJson generateNewUserAndSave(PlayerCredentialsBean plyc) {
		
		PlayerInfoJson plyinfo = new PlayerInfoJson();
		plyinfo.setPlayerName(plyc.getPlayerName());
		plyinfo.setPlayerId(plyc.getPlayerId());
		HashMap<String,InventoryInfoBean> items = new HashMap<>();
		
		InventoryInfoBean i1 = new InventoryInfoBean(); i1.setItemQuantity("2"); i1.setItemStatus("OFF_CHAIN");	items.put("1001_10001", i1);
		InventoryInfoBean i2 = new InventoryInfoBean(); i2.setItemQuantity("3"); i2.setItemStatus("OFF_CHAIN");	items.put("1001_10002", i2);
		InventoryInfoBean i3 = new InventoryInfoBean(); i3.setItemQuantity("4"); i3.setItemStatus("OFF_CHAIN"); items.put("1001_10003", i3);
		InventoryInfoBean i4 = new InventoryInfoBean(); i4.setItemQuantity("4"); i4.setItemStatus("OFF_CHAIN");	items.put("1001_10004", i4);
		InventoryInfoBean i5 = new InventoryInfoBean(); i5.setItemQuantity("4"); i5.setItemStatus("OFF_CHAIN");	items.put("1001_10005", i5);
		InventoryInfoBean i6 = new InventoryInfoBean(); i6.setItemQuantity("1"); i6.setItemStatus("ON_CHAIN");	items.put("1001_10006", i6);
		InventoryInfoBean i7 = new InventoryInfoBean(); i7.setItemQuantity("2"); i7.setItemStatus("ON_CHAIN");	items.put("1001_10007", i7);
		
		plyinfo.setItems(items);
		
		boolean savedtodb = addUserToDB(plyc,plyinfo);
		if(!savedtodb) {
			logger.error("!!!! FATAL !!!! User Not Saved TO DB!!!!!");
		}
		
		return plyinfo;
	}


	public PlayerInfoEty getPlayerByPlayerIdFromDB(String playerId) {
		logger.debug("Getting Player Info For Id {}",playerId);
		JdbcTemplate jdbcTemplate = globalJdbcTemplate;
		if(jdbcTemplate != null) {
			String query = "SELECT * FROM player_info WHERE player_id=?";
			try {
				List<PlayerInfoEty> playerInfo= jdbcTemplate.query(query, new BeanPropertyRowMapper<PlayerInfoEty>(PlayerInfoEty.class),playerId);
				if(playerInfo == null || playerInfo.size()==0) {
					return null;
				}else {
					PlayerInfoJson plyjc = Utility.parseJsonToObject(playerInfo.get(0).getPlayerInfo(), PlayerInfoJson.class);
					return playerInfo.get(0);
				}
			}catch (Exception e) {
				logger.error("Error While fetching PlayerInfo for PlayerId {} error :: {}",playerId,e.getMessage());
			}
		}
		return null;
	}
	
	public boolean addUserToDB(PlayerCredentialsBean plyc,PlayerInfoJson plyinfo) {
		JdbcTemplate jdbcTemplate = globalJdbcTemplate;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO ");
			sb.append(" player_info ");
			sb.append(" (player_id,player_name,player_info)");
			sb.append(" VALUES ( ");
			sb.append(" ?, ?, ?)");
			

			String insertQueryString = sb.toString();
			Object[] params = {
					plyc.getPlayerId(),plyc.getPlayerName(),Utility.getJsonFromObject(plyinfo)
			};
			
			int row = jdbcTemplate.update(insertQueryString,params);
			if(row == 1) {
				logger.info("Player Info saved sucessfullly to table {} ","player_info");
				return true;
			}else {
				logger.error("Could not save PlayerInfo to table {} ","player_info");
				return false;
			}
			
		}catch (Exception e) {
			logger.error("Error while saving to the database {}",e.getMessage());
			return false;
		}
	}
	
	
	public boolean updateUserToDB(PlayerInfoEty plyety, PlayerInfoJson playerInfo) {
		JdbcTemplate jdbcTemplate = globalJdbcTemplate;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE  ");
			sb.append(" player_info ");
			sb.append(" SET `player_name`=?,`player_info` = ?");
			sb.append(" WHERE `player_id`=? ");
			

			String insertQueryString = sb.toString();
			Object[] params = {
					plyety.getPlayerName(),Utility.getJsonFromObject(playerInfo),plyety.getPlayerId()
			};
			
			int row = jdbcTemplate.update(insertQueryString,params);
			if(row == 1) {
				logger.info("Player Info updated sucessfullly to table {} ","player_info");
				return true;
			}else {
				logger.error("Could not update PlayerInfo to table {} ","player_info");
				return false;
			}
			
		}catch (Exception e) {
			logger.error("Error while saving to the database {}",e.getMessage());
			return false;
		}
	}
	
}
