package com.game.inventory.beans;
import lombok.Data;

@Data
public class CreateTokenResponse extends CommonTransDTOFields{

	/*
	 * tokenMintRedirectionUrl == createTokenPageUrl
	 */
	private String tokenMintRedirectionUrl;
	
	/*
	 * tokenMintUrlPayload == payload to be sent over the redirectionUrl
	 */
	private String tokenMintUrlPayload;
	
	
	public CreateTokenResponse setStatusCodeDesc(String statusCode,String statusDesc) {
		setStatusCode(statusCode);
		setStatusDescription(statusDesc);
		return this;
	}
}
