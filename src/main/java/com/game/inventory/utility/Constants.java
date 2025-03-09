package com.game.inventory.utility;

public class Constants {

	public static enum ManageItems{
		MOVE_ON_CHAIN("MOVE_ON_CHAIN"),MOVE_OFF_CHAIN("MOVE_OFF_CHAIN"),LOCK_ITEM("LOCK_ITEM"),UNLOCK_ITEM("UNLOCK_ITEM");
		String val = null;
		ManageItems(String val) {
			this.val = val;
		}
		public String getValue() {
			return val;
		}
	};
	public static enum ItemStatus{
		ON_CHAIN("ON_CHAIN"),OFF_CHAIN("OFF_CHAIN"),LOCKED("LOCKED");
		String val = null;
		ItemStatus(String val){
			this.val = val;
		}
		public String getValue() {
			return val;
		}
	}
}
