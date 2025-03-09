package com.game.inventory.beans;

import lombok.Data;

@Data
public class CommonTransDTOFields extends CommonDTOFields {
	/*
	 * gameId == uniquely identifies the game
	 */
	private String gameId;
	/*
	 * ethContractId == ethContractId {Contract on which the token will be issued}
	 */
	private String ethContractId;
	/*
	 * gameTransactionId == will track the progress of token generation
	 */
	private String gameTransactionId;
	
	/*
	 * tokenMintTransactioId == tokenMintTransactionId
	 */
	private String tokenMintTransactionId;
	
	private String tokenMintTransactionIdHex;
	
}