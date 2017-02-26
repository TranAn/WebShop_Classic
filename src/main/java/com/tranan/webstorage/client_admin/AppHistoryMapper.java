package com.tranan.webstorage.client_admin;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.tranan.webstorage.client_admin.place.*;

/**
 * PlaceHistoryMapper interface is used to attach all places which the
 * PlaceHistoryHandler should be aware of.
 */
public class AppHistoryMapper implements PlaceHistoryMapper {

	String delimiter = "/";

	/**
	 * Check URL on browser and set corresponding place in App
	 */
	@Override
	public Place getPlace(String token) {
		// Default place
		if (token == null || token.equals(""))
			return new ShopPlace();

		// Get the token
		String[] tokens = new String[2];
		if (!token.contains(delimiter)) {
			tokens[0] = token;
			tokens[1] = null;
		} else
			tokens = token.split(delimiter, 2);

		// Mapping place
		if (tokens[0].equals("store"))
			return new ItemPlace();
		
		if (tokens[0].equals("order"))
			return new OrderPlace();
		
		if (tokens[0].equals("sale"))
			return new SalePlace();

		else if (tokens[0].equals("create_item"))
			return new CreateItemPlace(tokens[1], null);
		
		else if (tokens[0].equals("create_order"))
			return new CreateOrderPlace(tokens[1], null);
		
		else if (tokens[0].equals("create_sale"))
			return new CreateSalePlace(tokens[1], null);

		else
			return new ShopPlace();
	}

	/**
	 * Checks where you are and set corresponding URL on browser
	 */
	@Override
	public String getToken(Place place) {
		if (place instanceof ShopPlace)
			return "shop";

		if (place instanceof ItemPlace)
			return "store";
		
		if (place instanceof OrderPlace)
			return "order";
		
		if (place instanceof SalePlace)
			return "sale";

		if (place instanceof CreateItemPlace)
			return "create_item" + delimiter
					+ ((CreateItemPlace) place).getToken();
		
		if (place instanceof CreateOrderPlace)
			return "create_order" + delimiter
					+ ((CreateOrderPlace) place).getToken();
		
		if (place instanceof CreateSalePlace)
			return "create_sale" + delimiter
					+ ((CreateSalePlace) place).getToken();

		return null;
	}
}
