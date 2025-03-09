package com.game.inventory.beans;

import java.util.HashMap;

import com.game.inventory.utility.Utility;

import lombok.Data;

@Data
public class PlayerInfoJson {

	private String playerName;
	private String playerId;
	private HashMap<String,InventoryInfoBean> items;
	
	public PlayerInfoJson parsePlayerInfoJson(String json) {
		PlayerInfoJson ply = Utility.parseJsonToObject(json,PlayerInfoJson.class );
		return ply;
	}
}
