package com.game.inventory.beans;

import lombok.Data;

@Data
public class InventoryInfoBean {
	
	private String itemQuantity;
	private String chainId;
	private String recipientAddress;
	private String itemStatus;
	private String transactionRecieptUrl;
}
