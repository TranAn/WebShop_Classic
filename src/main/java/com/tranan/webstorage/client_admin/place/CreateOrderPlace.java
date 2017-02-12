package com.tranan.webstorage.client_admin.place;

import com.google.gwt.place.shared.Place;
import com.tranan.webstorage.shared.Order;

public class CreateOrderPlace extends Place {

	private String token;
	private Order order;

	public CreateOrderPlace(String token, Order order) {
		this.token = token;
		this.order = order;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

}
