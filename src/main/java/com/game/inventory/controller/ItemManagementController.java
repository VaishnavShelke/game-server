package com.game.inventory.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.game.inventory.beans.ItemInfoBean;
import com.game.inventory.beans.PlayerInfoEty;
import com.game.inventory.beans.TokenmintIssueTransactionBean;
import com.game.inventory.beans.TransferTokenEventRequest;
import com.game.inventory.beans.TransferTokenEventResponse;
import com.game.inventory.dao.ItemInfoDAO;
import com.game.inventory.dao.PlayerInfoDAO;
import com.game.inventory.service.TokenMintInterfaceHandler;
import com.game.inventory.service.TransferTokenEventHandler;
import com.game.inventory.utility.Utility;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/battlegrounds")
public class ItemManagementController {

	private static Logger logger = LoggerFactory.getLogger(ItemManagementController.class);
	 
	@Autowired
	ItemInfoDAO itemInfoDAO;
	
	@Autowired
	PlayerInfoDAO playerInfoDAO;
	
	@Autowired
	TokenMintInterfaceHandler tokenMintInterfaceHandler;
	
	@Autowired
	TransferTokenEventHandler transferTokenEventHandler;
	
	@PostMapping("/tokenize")
	public String homepage(Model model,@RequestParam String itemId, @RequestParam String itemQuantity) {
		logger.info("itemId :: {}, itemQuantity :: {}",itemId,itemQuantity);
		ItemInfoBean itminfo = itemInfoDAO.getItemInfoFromItemIdForGame(itemId);
		itminfo.setItemQuantity(itemQuantity);
		String otp = Utility.randomNumberSixDigit();
		model.addAttribute("otp", otp);
		model.addAttribute("checkoutitem", itminfo);
		return "checkoutpage.html";
	}
	
	@PostMapping("/redirectToTokenmint")
	public String redirectToTokenmint(HttpServletResponse response,HttpSession session,@RequestParam String itemId, @RequestParam String itemQuantity,@RequestParam String otp) {
		logger.info("itemId :: {},iteQuantity :: {},otp :: {}",itemId,itemQuantity,otp);
		String playerId = (String)session.getAttribute("playerid");
		logger.info("Player Id in Session {}",playerId);
		PlayerInfoEty playerInfo = playerInfoDAO.getPlayerByPlayerIdFromDB(playerId);
		logger.info("Player Info From Session Object :: {}",playerInfo);
		String redirectionUrl = tokenMintInterfaceHandler.initiateTransferToken(playerInfo,itemId,itemQuantity,otp);
		 if(redirectionUrl == null) {
        	return "dummypage.html";
        }else {
        	try {
				response.sendRedirect(redirectionUrl);
			} catch (IOException e) {
				logger.error("Could Not Redirect to {}",redirectionUrl);
			}
        }
		return "dummypage.html";
	}
	
	@GetMapping(path = "/landingPage/{txnId}")
	public String gameLandingPage(@PathVariable String txnId,Model model) {
		
		TokenmintIssueTransactionBean ttbean = tokenMintInterfaceHandler.loadTransactionStatus(txnId);
		logger.info("Token Mint Transaction Bean :: {}",ttbean);
		model.addAttribute("transactioninfo", ttbean);
		return "transactioninfo.html";
	}
	
	
	@PostMapping(path = "/tokenTransferEventListener")
	public TransferTokenEventResponse tokenTransferEventListener(@RequestBody  TransferTokenEventRequest request) {
		logger.debug("************************ Transfer Evelnt Listener Request ************************************");
        logger.info("Request Body  :: {}",Utility.getJsonFromObject(request));
		TransferTokenEventResponse resp = transferTokenEventHandler.handleEvent(request);
		logger.info("Handled tokenTransferEventListener from game server {}",resp);
        logger.debug("************************ Transfer Evelnt Listener Response ************************************");
        return resp;
	}
	
}
