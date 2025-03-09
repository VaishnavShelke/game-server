package com.game.inventory.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.game.inventory.beans.InventoryInfoBean;
import com.game.inventory.beans.ItemInfoBean;
import com.game.inventory.beans.PlayerInfoJson;
import com.game.inventory.dao.ItemInfoDAO;
import com.game.inventory.dao.PlayerInfoDAO;

import jakarta.servlet.http.HttpSession;

@Service
public class LoginSignupHandler {

	private static final Logger logger = LoggerFactory.getLogger(LoginSignupHandler.class);
	
	@Autowired
	PlayerInfoDAO playerInfoDAO;
	
	@Autowired
	ItemInfoDAO itemInfoDAO;
	
	public List<ItemInfoBean> loginOrSignUpUser(HttpSession session, String username, String password, Model model) {
		
		String playerId = (String)session.getAttribute("playerid");
		PlayerInfoJson playerInfo = null;
		if(playerId != null) {
			logger.info("Session does contains PLAYER ID :: {}",playerId);
			playerInfo = playerInfoDAO.getPlayerInfoJsonFromDBByPlayerId(playerId);
			session.setAttribute("playerid", playerInfo.getPlayerId());
		}else {
			logger.info("Session does not contains PLAYER ID :: {}",playerId);
			playerInfo = playerInfoDAO.getUserByUserId(username,password);
			session.setAttribute("playerid", playerInfo.getPlayerId());
		}
		
		HashMap<String,InventoryInfoBean> chainItems =  playerInfo.getItems();
		
		List<ItemInfoBean> allItemsList = new ArrayList<ItemInfoBean>();
		int cnt = 0;
		for(Entry<String,InventoryInfoBean> keyValue : chainItems.entrySet()) {
			ItemInfoBean itminfo =  itemInfoDAO.getItemInfoFromItemIdForGame(keyValue.getKey());
			itminfo.setItemQuantity(keyValue.getValue().getItemQuantity());
			itminfo.setChainId(keyValue.getValue().getItemQuantity());
			itminfo.setRecipientAddress(keyValue.getValue().getRecipientAddress());
			itminfo.setItemIndex(cnt);
			itminfo.setItemStatus(keyValue.getValue().getItemStatus());
			itminfo.setRecipientAddress(keyValue.getValue().getRecipientAddress());
			itminfo.setTransactionRecieptUrl(keyValue.getValue().getTransactionRecieptUrl());
			cnt++;
			allItemsList.add(itminfo);
		}
		
		logger.info("Player Info From DB :: {}",playerInfo);
		return allItemsList;
	}

	
}
