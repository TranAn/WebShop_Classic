package com.tranan.webstorage.client_admin.place;

import com.google.gwt.place.shared.Place;
import com.tranan.webstorage.shared.Sale;

public class CreateSalePlace extends Place {

	private String token;
	private Sale sale;

	public CreateSalePlace(String token, Sale sale) {
		this.token = token;
		this.sale = sale;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

}
