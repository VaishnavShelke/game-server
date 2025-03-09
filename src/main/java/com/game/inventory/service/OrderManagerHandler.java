package com.game.inventory.service;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.inventory.beans.InventoryInfoBean;
import com.game.inventory.beans.ItemInfoBean;
import com.game.inventory.beans.PlayerInfoEty;
import com.game.inventory.beans.PlayerInfoJson;
import com.game.inventory.beans.TokenmintIssueTransactionBean;
import com.game.inventory.dao.PlayerInfoDAO;
import com.game.inventory.utility.Constants.ItemStatus;
import com.game.inventory.utility.Constants.ManageItems;
import com.game.inventory.utility.Utility;


@Service
public class OrderManagerHandler {
	private static Logger logger = LoggerFactory.getLogger(OrderManagerHandler.class);

	@Autowired
	PlayerInfoDAO playerInfoDAO;
	
	public void manageItems(ManageItems action,TokenmintIssueTransactionBean ttbean) {
		
		ItemInfoBean itemInfo = ttbean.getItemInfo();
		String playerId = ttbean.getPlayerEty().getPlayerId();
		String itemId = itemInfo.getItemId();
		String itemQuantity = itemInfo.getItemQuantity();
		PlayerInfoEty plyEty = ttbean.getPlayerEty();
		PlayerInfoJson playerInfo = Utility.parseJsonToObject(plyEty.getPlayerInfo(), PlayerInfoJson.class);
		HashMap<String,InventoryInfoBean> itemMap = playerInfo.getItems();
		
		if(action.equals(ManageItems.LOCK_ITEM)) {
			InventoryInfoBean inv = itemMap.get(itemId);
			inv.setItemStatus(ItemStatus.LOCKED.getValue());
			itemMap.put(itemId,inv);
			ttbean.setItemStatus(ItemStatus.LOCKED.getValue());
			ttbean.setItemStatusDescription("Transaction In Progress please refresh the page after sometime");
		}else if(action.equals(ManageItems.MOVE_ON_CHAIN)) {
			InventoryInfoBean inv = itemMap.get(itemId);
			inv.setItemStatus(ItemStatus.ON_CHAIN.getValue());
			inv.setRecipientAddress(ttbean.getRecipientAddress());
			inv.setTransactionRecieptUrl(ttbean.getTransactionReciept());
			itemMap.put(itemId,inv);
			ttbean.setItemStatus(ItemStatus.ON_CHAIN.getValue());
			ttbean.setItemStatusDescription("Successfully Moved On Chain");
		}else if(action.equals(ManageItems.MOVE_OFF_CHAIN)) {
			InventoryInfoBean inv = itemMap.get(itemId);
			inv.setItemStatus(ItemStatus.OFF_CHAIN.getValue());
			itemMap.put(itemId,inv);
			ttbean.setItemStatus(ItemStatus.OFF_CHAIN.getValue());
			ttbean.setItemStatusDescription("Tokenization Failed");
		}else if(action.equals(ManageItems.UNLOCK_ITEM)) {
			InventoryInfoBean inv = itemMap.get(itemId);
			inv.setItemStatus(ItemStatus.OFF_CHAIN.getValue());
			itemMap.put(itemId,inv);
			ttbean.setItemStatus(ItemStatus.OFF_CHAIN.getValue());
		}
		
		playerInfo.setItems(itemMap);
		boolean updated  = playerInfoDAO.updateUserToDB(plyEty,playerInfo);
		if(!updated) {
			logger.error("player info not updated to DB {}", playerInfo);
		}
		
	}
}
