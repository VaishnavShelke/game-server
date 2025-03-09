package com.game.inventory.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.game.inventory.beans.CreateTokenRequest;
import com.game.inventory.beans.CreateTokenResponse;
import com.game.inventory.beans.ItemInfoBean;
import com.game.inventory.beans.PlayerInfoEty;
import com.game.inventory.beans.PlayerInfoJson;
import com.game.inventory.beans.TokenmintIssueTransactionBean;
import com.game.inventory.dao.ItemInfoDAO;
import com.game.inventory.dao.RedisMockDAO;
import com.game.inventory.utility.Constants.ManageItems;
import com.game.inventory.utility.Utility;

@Service
public class TokenMintInterfaceHandler {

	private static Logger logger = LoggerFactory.getLogger(TokenMintInterfaceHandler.class);
	
	@Autowired
	ItemInfoDAO itmInfoDAO;
	
	@Autowired
	HttpConnectService httpConnectService; 
	
	@Autowired
	OrderManagerHandler orderManagerHandler;
	
	@Autowired
	RedisMockDAO redisMockDAO;
	
	@Value("${tokenmint-transfer-tokens-request-url}")
	private String transferTokensUrl;
	
	@Value("${game-landing-page}")
	private String gameLandingPage;
	
	public String initiateTransferToken(PlayerInfoEty playerInfo, String itemId, String itemQuantity, String otp) {
		
		TokenmintIssueTransactionBean tmbin = new TokenmintIssueTransactionBean();
		CreateTokenRequest createTokenRequest = prepareCreateTokenRequest(playerInfo,itemId,itemQuantity,otp,tmbin);
		populateIssueTransBeanFromCreateTokenRequest(tmbin,createTokenRequest,playerInfo);
		logger.info("Create Token Request :: {}",Utility.getJsonFromObject(createTokenRequest));
		
	
		String createTokenResponseJson = sendCreateTokenRequest(createTokenRequest,tmbin);
		CreateTokenResponse ctrs = Utility.parseJsonToObject(createTokenResponseJson, CreateTokenResponse.class);
		if(ctrs == null || !"000".equals(ctrs.getStatusCode())) {
			logger.error("Error Response From TokenMintRequest :: {}",ctrs);
			return null;
		}
		
		populateTransactionBeanFromCreateTokenResponse(ctrs,tmbin);
		String redisKey = "TXN_KEY_" + tmbin.getGameTransactionId();
		
		logger.info("TokenMint Transaction Bean :: {}",tmbin);
		if("000".equals(ctrs.getStatusCode())) {
			orderManagerHandler.manageItems(ManageItems.LOCK_ITEM, tmbin);
		}else {
			orderManagerHandler.manageItems(ManageItems.UNLOCK_ITEM, tmbin);
		}
		
		boolean savedToRedis  = redisMockDAO.saveInRedis(redisKey, Utility.getJsonFromObject(tmbin));
		if(!savedToRedis) {
			logger.error("!!! FATAL !!! Could Not Save Transaction To Mocked Redis !!!!! in CreateToKEN Request");
		}
		return ctrs.getTokenMintRedirectionUrl();
	}

	private void populateTransactionBeanFromCreateTokenResponse(CreateTokenResponse ctrs,
			TokenmintIssueTransactionBean tmbin) {
		tmbin.setTokenMintRedirectionUrl(ctrs.getTokenMintRedirectionUrl());
		tmbin.setTokenMintTransactionId(ctrs.getTokenMintTransactionId());
	}

	private String sendCreateTokenRequest(CreateTokenRequest createTokenRequest, TokenmintIssueTransactionBean tmbin) {
		Map<String,String> headerMap = new HashMap<>();
		headerMap.put("Accept", "application/json");
		headerMap.put("Content-type", "application/json");
		String createTokenResponseJson = httpConnectService.postRequest(headerMap, Utility.getJsonFromObject(createTokenRequest), transferTokensUrl);
		return createTokenResponseJson;
	}

	private TokenmintIssueTransactionBean populateIssueTransBeanFromCreateTokenRequest(TokenmintIssueTransactionBean tmbin, CreateTokenRequest ctt,
			PlayerInfoEty playerInfo) {
		tmbin.setGameLandingPage(ctt.getGameLandingPage());
	
		return tmbin;
	}

	private CreateTokenRequest prepareCreateTokenRequest(PlayerInfoEty playerEty, String itemId, String itemQuantity,
			String otp, TokenmintIssueTransactionBean tmbin) {
		tmbin.setGameTransactionId(Utility.generateDateTimeId());
		
		CreateTokenRequest cttr = new CreateTokenRequest();
		cttr.setGameId("1001");
		cttr.setEthContractId("1001_50001");
		cttr.setGameTransactionId(tmbin.getGameTransactionId());
		cttr.setGameLandingPage(gameLandingPage + "/" + tmbin.getGameTransactionId());;
		cttr.setOtp(otp);
		
		ItemInfoBean itinf = itmInfoDAO.getItemInfoFromItemIdForGame(itemId);
		ItemInfoBean itemInfoBean = new ItemInfoBean();
		itemInfoBean.setItemCategory(itinf.getItemCategory());
		itemInfoBean.setItemId(itemId);
		itemInfoBean.setItemQuantity(itemQuantity);
		cttr.setGameItemInfo(itemInfoBean);
		
		
		
		PlayerInfoEty plinf = new PlayerInfoEty();
		plinf.setPlayerName(playerEty.getPlayerName());
		plinf.setPlayerId(playerEty.getPlayerId());
		cttr.setPlayerInfo(plinf);
		 
		itinf.setItemQuantity(itemQuantity);
		tmbin.setItemInfo(itinf);
		tmbin.setPlayerEty(playerEty);
		tmbin.setPalyerInfo(Utility.parseJsonToObject(playerEty.getPlayerInfo(), PlayerInfoJson.class));
		
		return cttr;
	}

	public TokenmintIssueTransactionBean loadTransactionStatus(String txnId) {
		String redisKey = "TXN_KEY_" + txnId;
		String tmtransBeanJson =  redisMockDAO.getRedisValue(redisKey);
		TokenmintIssueTransactionBean tmtxn = Utility.parseJsonToObject(tmtransBeanJson, TokenmintIssueTransactionBean.class);
		if(tmtxn == null) {
			tmtxn = new TokenmintIssueTransactionBean();
			tmtxn.setViewStatusCode("001");
			tmtxn.setViewStatusDescription("Could Not Find The Transaction Information");
		}
		return tmtxn;
	}

}
