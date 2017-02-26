package com.tranan.webstorage.client_admin.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.place.CreateOrderPlace;
import com.tranan.webstorage.client_admin.place.ItemPlace;
import com.tranan.webstorage.client_admin.place.OrderPlace;
import com.tranan.webstorage.client_admin.place.SalePlace;

public class LeftMenu extends Composite {

	private static LeftMenuUiBinder uiBinder = GWT
			.create(LeftMenuUiBinder.class);

	interface LeftMenuUiBinder extends UiBinder<Widget, LeftMenu> {
	}
	
	@UiField HTMLPanel shopMenuPanel;
	@UiField HTMLPanel saleMenuPanel;
	@UiField HTMLPanel storeMenuPanel;
	@UiField HTMLPanel orderMenuPanel;
	@UiField HTMLPanel chartMenuPanel;
	@UiField HTMLPanel infoMenuPanel;
	@UiField HTMLPanel logoutPanel;

	public LeftMenu() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void clearMenuStyle() {
		shopMenuPanel.setStyleName("LeftMenu_s1");
		saleMenuPanel.setStyleName("LeftMenu_s1");
		storeMenuPanel.setStyleName("LeftMenu_s1");
		orderMenuPanel.setStyleName("LeftMenu_s1");
		chartMenuPanel.setStyleName("LeftMenu_s1");
		infoMenuPanel.setStyleName("LeftMenu_s1");
		logoutPanel.setStyleName("LeftMenu_s1");
	}
	
	public void onStorePlace() {
		clearMenuStyle();
		storeMenuPanel.setStyleName("LeftMenu_s1_active");
	}
	
	public void onOrderPlace() {
		clearMenuStyle();
		orderMenuPanel.setStyleName("LeftMenu_s1_active");
	}
	
	public void onSalePlace() {
		clearMenuStyle();
		saleMenuPanel.setStyleName("LeftMenu_s1_active");
	}
	
	@UiHandler("storeMenu")
	void onStoreMenuClick(ClickEvent e) {
		PrettyGal.placeController.goTo(new ItemPlace());
		onStorePlace();
	}
	
	@UiHandler("orderMenu")
	void onOrderMenuClick(ClickEvent e) {
		PrettyGal.placeController.goTo(new OrderPlace());
		onOrderPlace();
	}
	
	@UiHandler("saleMenu")
	void onSaleMenuClick(ClickEvent e) {
		PrettyGal.placeController.goTo(new SalePlace());
		onSalePlace();
	}

}
