package com.tranan.webstorage.client_admin.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.place.ItemPlace;
import com.tranan.webstorage.client_admin.place.OrderPlace;
import com.tranan.webstorage.client_admin.place.SalePlace;
import com.tranan.webstorage.client_admin.place.StatisticPlace;

public class LeftMenu extends Composite {

	private static LeftMenuUiBinder uiBinder = GWT
			.create(LeftMenuUiBinder.class);

	interface LeftMenuUiBinder extends UiBinder<Widget, LeftMenu> {
	}
	
	@UiField ScrollPanel scroll;
	@UiField HTMLPanel panel;
	@UiField HTMLPanel shopMenuPanel;
	@UiField HTMLPanel saleMenuPanel;
	@UiField HTMLPanel storeMenuPanel;
	@UiField HTMLPanel orderMenuPanel;
	@UiField HTMLPanel chartMenuPanel;
	@UiField HTMLPanel infoMenuPanel;
	@UiField HTMLPanel logoutPanel;

	public LeftMenu() {
		initWidget(uiBinder.createAndBindUi(this));
		
		setPageSize();
	}
	
	public void setPageSize() {
		scroll.setHeight(Window.getClientHeight()+ "px");
		panel.setHeight(Window.getClientHeight()+ "px");
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
	
	public void onStatisticPlace() {
		clearMenuStyle();
		chartMenuPanel.setStyleName("LeftMenu_s1_active");
	}
	
	@UiHandler("storeMenu")
	void onStoreMenuClick(ClickEvent e) {
		PrettyGal.confirmChangePlace(new ItemPlace());
	}
	
	@UiHandler("orderMenu")
	void onOrderMenuClick(ClickEvent e) {
		PrettyGal.confirmChangePlace(new OrderPlace());
	}
	
	@UiHandler("saleMenu")
	void onSaleMenuClick(ClickEvent e) {
		PrettyGal.confirmChangePlace(new SalePlace());
	}
	
	@UiHandler("chartMenu")
	void onChartMenuClick(ClickEvent e) {
		PrettyGal.confirmChangePlace(new StatisticPlace());
	}
	
	@UiHandler("logout")
	void onLogOutClick(ClickEvent e) {
		LoginPage.handleSignoutClick();
		PrettyGal.onAuthFail();
	}

}
