package com.tranan.webstorage.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Header extends Composite {

	private static HeaderUiBinder uiBinder = GWT.create(HeaderUiBinder.class);

	interface HeaderUiBinder extends UiBinder<Widget, Header> {
	}
	
	@UiField Label itemCount;
	@UiField Label homePage;
	@UiField Label shopPage;
	@UiField Label contactPage;
	@UiField HTMLPanel cartPage;
	
	public Header() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void clearHeaderActiveMenuStyle() {
		homePage.removeStyleName("Header_ActiveMenu");
		shopPage.removeStyleName("Header_ActiveMenu");
		contactPage.removeStyleName("Header_ActiveMenu");
		cartPage.removeStyleName("Header_ActiveMenu");
	}
	
	public void setHomePageActiveStyle () {
		homePage.addStyleName("Header_ActiveMenu");
	}
	
	public void setShopPageActiveStyle () {
		shopPage.addStyleName("Header_ActiveMenu");
	}
	
	public void setContactPageActiveStyle () {
		contactPage.addStyleName("Header_ActiveMenu");
	}
	
	public void setCartPageActiveStyle () {
		cartPage.addStyleName("Header_ActiveMenu");
	}
	
	public void setOrderItems(String item_count) {
		if(item_count == "0")
			itemCount.setVisible(false);
		else {
			itemCount.setVisible(true);
			itemCount.setText(item_count);
		}
	}
	
	@UiHandler("homePage")
	void onHomePageClick(ClickEvent e) {
		Window.Location.assign("/#");
	}
	
	@UiHandler("shopPage")
	void onShopPageClick(ClickEvent e) {
		Window.Location.assign("/#shop");
	}
	
	@UiHandler("contactPage")
	void onContactPageClick(ClickEvent e) {
		
	}
	
	@UiHandler("cartPageBtn")
	void onCartPageClick(ClickEvent e) {
		Window.Location.assign("/#cart");
	}

}
