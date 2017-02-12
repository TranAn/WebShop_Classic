package com.tranan.webstorage.client_admin.place;

import com.google.gwt.place.shared.Place;
import com.tranan.webstorage.shared.Item;

public class CreateItemPlace extends Place {

	private String token;
	private Item item;

	public CreateItemPlace(String token, Item item) {
		this.token = token;
		this.item = item;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

}
