package com.game.inventory.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.game.inventory.beans.ItemInfoBean;


@Service
public class ItemInfoDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(ItemInfoDAO.class);

	@Autowired
	@Qualifier("tokenmintjdbctemplate")
	JdbcTemplate globalJdbcTemplate;
	
	public ItemInfoBean getItemInfoFromItemIdForGame(String itemId) {
		JdbcTemplate jdbcTemplate = globalJdbcTemplate;
		if(jdbcTemplate != null) {
			String query = "SELECT * FROM game_items WHERE item_id=?";
			try {
				List<ItemInfoBean> itemInfoList= jdbcTemplate.query(query, new BeanPropertyRowMapper<ItemInfoBean>(ItemInfoBean.class),itemId);
				if(itemInfoList == null || itemInfoList.size()==0) {
					return null;
				}else {
					return itemInfoList.get(0);
				}
			}catch (Exception e) {
				logger.error("Error While fetching ItemInfo for item id {} error :: {}",itemId,e.getMessage());
			}
		}
		return null;
	}

}
