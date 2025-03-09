package com.game.inventory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.inventory.beans.TokenmintIssueTransactionBean;
import com.game.inventory.beans.TransferTokenEventRequest;
import com.game.inventory.beans.TransferTokenEventResponse;
import com.game.inventory.dao.RedisMockDAO;
import com.game.inventory.utility.Constants.ManageItems;
import com.game.inventory.utility.Utility;

@Service
public class TransferTokenEventHandler {

	private static Logger logger = LoggerFactory.getLogger(TransferTokenEventHandler.class); 
	
	@Autowired
	RedisMockDAO redisMockDAO;
	
	@Autowired
	OrderManagerHandler orderManagerHandler;
	
	public TransferTokenEventResponse handleEvent(TransferTokenEventRequest request) {
		String redisKey = "TXN_KEY_" + request.getGameTransactionId();
		String tmtransBeanJson =  redisMockDAO.getRedisValue(redisKey);
		TransferTokenEventResponse tres = new TransferTokenEventResponse();
		if(tmtransBeanJson == null) {
			tres.setStatusCode("001");
			tres.setStatusDescription("Could Not Find The Transaction Corresponding to TXN ID");
			return tres;
		}
		TokenmintIssueTransactionBean tmtxn = Utility.parseJsonToObject(tmtransBeanJson, TokenmintIssueTransactionBean.class);
		
		populateTxnBeanFromTransferTokenReq(tmtxn,request);
		
		boolean updatedTxn = redisMockDAO.updateRedisValue(redisKey, Utility.getJsonFromObject(tmtxn));
		if(!updatedTxn) {
			logger.error("!!!FATAL!!! Could not update to mock redis !!!!FATAL!!!!");
		}
		
		orderManagerHandler.manageItems(ManageItems.MOVE_ON_CHAIN, tmtxn);
		 
		tres.setStatusCode("000");
		tres.setStatusDescription("SUCCESS");
		return tres;
	}

	private void populateTxnBeanFromTransferTokenReq(TokenmintIssueTransactionBean tmtxn,
			TransferTokenEventRequest request) {
		tmtxn.setRecipientAddress(request.getRecipientAddress());
		tmtxn.setTransferSuccessful(request.getTransferSuccessful());
		tmtxn.setTransactionReciept(request.getTransactionHash());
		tmtxn.setContractAddress(request.getContractAddress());
		tmtxn.setEthChainId(request.getEthChainId());
	}

}
