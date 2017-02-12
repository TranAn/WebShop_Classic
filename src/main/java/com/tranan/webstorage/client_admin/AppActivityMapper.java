package com.tranan.webstorage.client_admin;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.tranan.webstorage.client_admin.place.CreateItemPlace;
import com.tranan.webstorage.client_admin.place.CreateOrderPlace;
import com.tranan.webstorage.client_admin.place.ItemPlace;
import com.tranan.webstorage.client_admin.place.OrderPlace;
import com.tranan.webstorage.client_admin.ui.CreateItem;
import com.tranan.webstorage.client_admin.ui.CreateOrder;
import com.tranan.webstorage.client_admin.ui.ItemTable;

public class AppActivityMapper implements ActivityMapper {

	CreateItem current_createitem_ui;
	CreateOrder current_createorder_ui;

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
		if (place instanceof ItemPlace)
			return new AbstractActivity() {
				@Override
				public void start(AcceptsOneWidget panel, EventBus eventBus) {
					PrettyGal.slideMenu.onStorePlace();
					PrettyGal.controlPage.HideAllToolbar();
					
					PrettyGal.controlPage.addPage(PrettyGal.UIC.getItemTable());
					
					PrettyGal.UIC.getItemTable().getListItem("");
				}

				@Override
				public String mayStop() {
					PrettyGal.UIC.getItemTable().cancelImportItem();
					PrettyGal.UIC.getItemTable().cancelFilter();
					PrettyGal.UIC.getItemTable().cancelSearch();
					return null;
				}
			};

		if (place instanceof OrderPlace)
			return new AbstractActivity() {
				@Override
				public void start(AcceptsOneWidget panel, EventBus eventBus) {
					PrettyGal.slideMenu.onOrderPlace();
					PrettyGal.controlPage.ShowOrderToolbar();
					
					PrettyGal.controlPage
							.addPage(PrettyGal.UIC.getOrderTable());
				}

				@Override
				public String mayStop() {
					return null;
				}
			};

		if (place instanceof CreateItemPlace)
			return new AbstractActivity() {
				@Override
				public void start(AcceptsOneWidget panel, EventBus eventBus) {
					PrettyGal.slideMenu.onStorePlace();
					PrettyGal.controlPage.ShowCreateItemToolbar();
					
					CreateItemPlace createItemPlace = (CreateItemPlace) place;
					current_createitem_ui = PrettyGal.UIC
							.getCreateItem(createItemPlace.getItem());
					PrettyGal.controlPage.addPage(current_createitem_ui);
				}

				@Override
				public String mayStop() {
					if (current_createitem_ui.isItemChange())
						return "Bạn muốn thoát khi chưa lưu thay đổi?";
					else
						return null;
				}
			};

		if (place instanceof CreateOrderPlace)
			return new AbstractActivity() {
				@Override
				public void start(AcceptsOneWidget panel, EventBus eventBus) {
					PrettyGal.slideMenu.onOrderPlace();
					PrettyGal.controlPage.ShowCreateOrderToolbar();
					
					CreateOrderPlace createOrderPlace = (CreateOrderPlace) place;
					current_createorder_ui = PrettyGal.UIC
							.getCreateOrder(createOrderPlace.getOrder());
					PrettyGal.controlPage.addPage(current_createorder_ui);
				}

				@Override
				public String mayStop() {
					if (current_createorder_ui.isItemChange())
						return "Bạn muốn thoát khi chưa lưu thay đổi?";
					else
						return null;
				}
			};

		return null;
	}

}
