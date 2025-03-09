package com.game.inventory.beans;

import lombok.Data;

@Data
public class TokenmintIssueTransactionBean {
	
	
	
	private String gameTransactionId;
	private String tokenMintTransactionId;
	private String gameLandingPage;
	private String tokenMintRedirectionUrl;
	
	private String recipientAddress;
	private String transferSuccessful;
	private String ethChainId;
	private String contractAddress;
	private String transactionReciept;
	
	private PlayerInfoEty playerEty;
	private PlayerInfoJson palyerInfo;
	private ItemInfoBean itemInfo;
	private String itemStatus = null;
	private String itemStatusDescription;
	
	
	private String viewStatusDescription;
	private String viewStatusCode;

}
