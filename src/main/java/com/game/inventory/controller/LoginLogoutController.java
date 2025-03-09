package com.game.inventory.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.game.inventory.beans.ItemInfoBean;
import com.game.inventory.service.LoginSignupHandler;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/battlegrounds")
public class LoginLogoutController {
	
	private static Logger logger = LoggerFactory.getLogger(LoginLogoutController.class);
	 
	@Autowired
	LoginSignupHandler loginSignupHandler;
	
	@GetMapping("")
	public String homepage(HttpSession session) {
		session.removeAttribute("playerid");
		return "loginsignup.html";
	}
	
	@PostMapping("/login")
	public String loginSignup(HttpSession session,Model model,@RequestParam String username, @RequestParam String password) {
		logger.info("Username :: {}, Password :: {}",username,password);
		List<ItemInfoBean> allItemsList= loginSignupHandler.loginOrSignUpUser(session,username,password,model);
		model.addAttribute("allItems", allItemsList);
		
		return "inGameItems.html";
	}

}
