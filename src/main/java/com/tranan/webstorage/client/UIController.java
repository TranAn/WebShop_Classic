package com.tranan.webstorage.client;

import com.tranan.webstorage.client.ui.Cart;
import com.tranan.webstorage.client.ui.HomeWall;
import com.tranan.webstorage.client.ui.Shop;

public class UIController {

	HomeWall homeWall;
	Shop shop;
	Cart cart;

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
	
	public Cart getCart() {
		if (cart == null)
			cart = new Cart();
		return cart;
	}

}
