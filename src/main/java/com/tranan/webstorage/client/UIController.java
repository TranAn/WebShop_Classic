package com.tranan.webstorage.client;

import com.tranan.webstorage.client.ui.HomeWall;
import com.tranan.webstorage.client.ui.Shop;

public class UIController {

	HomeWall homeWall;
	Shop shop;

	public UIController() {
		super();
	}

	public HomeWall getHomeWall() {
		if (homeWall == null)
			homeWall = new HomeWall();
		return homeWall;
	}

	public Shop getShop() {
		if (shop == null)
			shop = new Shop();
		return shop;
	}

}
