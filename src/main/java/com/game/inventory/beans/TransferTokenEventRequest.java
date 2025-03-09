package com.game.inventory.beans;

import lombok.Data;

@Data
public class TransferTokenEventRequest {

	private String tokenMintTransactionId;
	private String gameTransactionId;
	private String recipientAddress;
	private String transferSuccessful;
	private String ethChainId;
	private String contractAddress;
	private String transactionHash;
	
}
