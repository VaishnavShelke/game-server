package com.game.inventory.beans;

import java.util.List;

import lombok.Data;

@Data
public class CreateTokenRequest extends CommonTransDTOFields{
	/*
	 * gameItemIds == Item info for game
	 */
	private ItemInfoBean gameItemInfo;
	
	/*
	 * userGameId == uniqueId for user identification
	 */
	private PlayerInfoEty playerInfo;
	
	/*
	 * registeredEthAddress == address that are registered and stored with the game
	 */
	private List<String> registeredEthAddress;
	
	private String gameLandingPage;

	private String otp;
}


