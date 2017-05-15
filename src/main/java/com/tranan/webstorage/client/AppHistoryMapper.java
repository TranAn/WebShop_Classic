package com.tranan.webstorage.client;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.tranan.webstorage.client.place.CartPlace;
import com.tranan.webstorage.client.place.HomePlace;
import com.tranan.webstorage.client.place.ShopPlace;

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
			return new HomePlace();

		// Get the token
		String[] tokens = new String[2];
		if (!token.contains(delimiter)) {
			tokens[0] = token;
			tokens[1] = null;
		} else
			tokens = token.split(delimiter, 2);

		// Mapping place
		if (tokens[0].equals("shop"))
			return new ShopPlace();
		
		if (tokens[0].equals("cart"))
			return new CartPlace();
			
		else
			return new HomePlace();
	}

	/**
	 * Checks where you are and set corresponding URL on browser
	 */
	@Override
	public String getToken(Place place) {
		if (place instanceof HomePlace)
			return "";
		
		if (place instanceof ShopPlace)
			return "shop";
		
		if (place instanceof CartPlace)
			return "cart";

		return null;
	}
}
