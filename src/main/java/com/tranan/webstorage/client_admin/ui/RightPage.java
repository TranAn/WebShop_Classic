package com.tranan.webstorage.client_admin.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.place.ItemPlace;
import com.tranan.webstorage.client_admin.place.OrderPlace;
import com.tranan.webstorage.client_admin.place.SalePlace;

public class RightPage extends Composite {

	private static RightPageUiBinder uiBinder = GWT
			.create(RightPageUiBinder.class);

	interface RightPageUiBinder extends UiBinder<Widget, RightPage> {
	}
	 
	@UiField ScrollPanel scroll;
	@UiField HTMLPanel right_page;
	@UiField HTMLPanel right_page_body;
	
	@UiField HTMLPanel OrderToolbar;
	@UiField HTMLPanel CreateOrderToolbar;
	@UiField HTMLPanel CreateItemToolbar;
	@UiField HTMLPanel CreateSaleToolbar;
	@UiField Label userNameLb;
	@UiField Image userImg;

	public RightPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		setPageSize();
	}
	
	public void setPageSize() {	
		scroll.setWidth(Window.getClientWidth() - 250 + "px");
		scroll.setHeight(Window.getClientHeight()+ "px");
		right_page.setWidth(Window.getClientWidth() - 250 + "px");
//		right_page.setHeight(Window.getClientHeight()+ "px");
		right_page_body.setHeight(Window.getClientHeight() - 65 + "px");	
	}
	
	public void setUser(String userName, String userAvatar) {
		userNameLb.setText(userName);
		userImg.setUrl(userAvatar);
	}
	
	public void HideAllToolbar() {
		OrderToolbar.setVisible(false);
		CreateOrderToolbar.setVisible(false);
		CreateItemToolbar.setVisible(false);
		CreateSaleToolbar.setVisible(false);
	}
	
	public void ShowCreateItemToolbar() {
		HideAllToolbar();
		CreateItemToolbar.setVisible(true);
	}
	
	public void ShowCreateSaleToolbar() {
		HideAllToolbar();
		CreateSaleToolbar.setVisible(true);
	}
	
	public void ShowOrderToolbar() {
		HideAllToolbar();
		OrderToolbar.setVisible(true);
	}
	
	public void ShowCreateOrderToolbar() {
		HideAllToolbar();
		CreateOrderToolbar.setVisible(true);
	}
	
	public void addPage(Widget page) {
		right_page_body.clear();
		right_page_body.add(page);
	}
	
	@UiHandler("createOrderBackBtn")
	void onCreateOrderBackBtnClick(ClickEvent e) {
		PrettyGal.confirmChangePlace(new OrderPlace());
	}
	
	@UiHandler("createItemBackBtn")
	void onCreateItemBackBtnClick(ClickEvent e) {
		PrettyGal.confirmChangePlace(new ItemPlace());
	}
	
	@UiHandler("createSaleBackBtn")
	void onCreateSaleBackBtnClick(ClickEvent e) {
		PrettyGal.confirmChangePlace(new SalePlace());
	}
	
	@UiHandler("orderInBtn")
	void onOrderInBtnClick(ClickEvent e) {
		PrettyGal.UIC.getOrderTable().changeToOrderInTab();
	}
	
	@UiHandler("orderOutBtn")
	void onOrderOutBtnClick(ClickEvent e) {
		PrettyGal.UIC.getOrderTable().changeToOrderTab();
	}
	
}
