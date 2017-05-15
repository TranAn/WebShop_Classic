package com.tranan.webstorage.client;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.tranan.webstorage.client.place.CartPlace;
import com.tranan.webstorage.client.place.HomePlace;
import com.tranan.webstorage.client.place.ShopPlace;

public class AppActivityMapper implements ActivityMapper {

	/**
	 * AppActivityMapper associates each Place with its corresponding
	 * {@link Activity}
	 */
	public AppActivityMapper() {
		super();
	}

	/**
	 * Map each Place to its corresponding Activity. This would be a great use
	 * for GIN.
	 */
	@Override
	public Activity getActivity(final Place place) {
		// This is begging for GIN
		if (place instanceof HomePlace)
			return new AbstractActivity() {
				@Override
				public void start(AcceptsOneWidget panel, EventBus eventBus) {
					PrettyGal.controlPage.clear();
					PrettyGal.controlPage.add(PrettyGal.UIC.getHomeWall());
					PrettyGal.controlPage.add(PrettyGal.UIC.getShop());
					PrettyGal.header.clearHeaderActiveMenuStyle();
					PrettyGal.header.setHomePageActiveStyle();
				}

				@Override
				public String mayStop() {

					return null;
				}
			};

		if (place instanceof ShopPlace)
			return new AbstractActivity() {
				@Override
				public void start(AcceptsOneWidget panel, EventBus eventBus) {
					PrettyGal.controlPage.clear();
					PrettyGal.controlPage.add(PrettyGal.UIC.getShop());
					PrettyGal.header.clearHeaderActiveMenuStyle();
					PrettyGal.header.setShopPageActiveStyle();
				}

				@Override
				public String mayStop() {
					return null;
				}
			};

		if (place instanceof CartPlace)
			return new AbstractActivity() {
				@Override
				public void start(AcceptsOneWidget panel, EventBus eventBus) {
					PrettyGal.controlPage.clear();
					PrettyGal.controlPage.add(PrettyGal.UIC.getCart());
					PrettyGal.header.clearHeaderActiveMenuStyle();
					PrettyGal.header.setCartPageActiveStyle();
				}

				@Override
				public String mayStop() {
					return null;
				}
			};

		return null;
	}

}
