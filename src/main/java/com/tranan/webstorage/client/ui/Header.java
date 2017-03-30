package com.tranan.webstorage.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class Header extends Composite {

	private static HeaderUiBinder uiBinder = GWT.create(HeaderUiBinder.class);

	interface HeaderUiBinder extends UiBinder<Widget, Header> {
	}
	
	public Header() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("homePage")
	void onHomePageClick(ClickEvent e) {
		RootPanel.get().clear();
		RootPanel.get().add(new Header());
		RootPanel.get().add(new HomeCover());
		RootPanel.get().add(new Shop());
//		RootPanel.get().add(new Item());
//		RootPanel.get().add(new Contact());
//		RootPanel.get().add(new Cart());
		RootPanel.get().add(new Footer());
	}
	
	@UiHandler("shopPage")
	void onShopPageClick(ClickEvent e) {
		RootPanel.get().clear();
		RootPanel.get().add(new Header());
//		RootPanel.get().add(new HomeCover());
//		RootPanel.get().add(new Shop());
		RootPanel.get().add(new Item());
//		RootPanel.get().add(new Contact());
//		RootPanel.get().add(new Cart());
		RootPanel.get().add(new Footer());
	}
	
	@UiHandler("contactPage")
	void onContactPageClick(ClickEvent e) {
		RootPanel.get().clear();
		RootPanel.get().add(new Header());
//		RootPanel.get().add(new HomeCover());
//		RootPanel.get().add(new Shop());
//		RootPanel.get().add(new Item());
		RootPanel.get().add(new Contact());
//		RootPanel.get().add(new Cart());
		RootPanel.get().add(new Footer());
	}
	
	@UiHandler("cartPage")
	void onCartPageClick(ClickEvent e) {
		RootPanel.get().clear();
		RootPanel.get().add(new Header());
//		RootPanel.get().add(new HomeCover());
//		RootPanel.get().add(new Shop());
//		RootPanel.get().add(new Item());
//		RootPanel.get().add(new Contact());
		RootPanel.get().add(new Cart());
		RootPanel.get().add(new Footer());
	}

}
