package com.tranan.webstorage.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class Shop extends Composite {

	private static ShopUiBinder uiBinder = GWT.create(ShopUiBinder.class);

	interface ShopUiBinder extends UiBinder<Widget, Shop> {
	}
	
	@UiField HTMLPanel item1_view;
	@UiField HTMLPanel item2_view;
	@UiField HTMLPanel item3_view;
	@UiField HTMLPanel item4_view;
	@UiField HTMLPanel item5_view;
	@UiField HTMLPanel item6_view;

	public Shop() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("item1")
	public void onMouseOverItem1(MouseOverEvent event)
	{
		item1_view.addStyleName("Shop_s1_open");
	}
	
	@UiHandler("item1")
	public void onMouseOutItem1(MouseOutEvent event)
	{
		item1_view.removeStyleName("Shop_s1_open");
	}
	
	@UiHandler("item2")
	public void onMouseOverItem2(MouseOverEvent event)
	{
		item2_view.addStyleName("Shop_s1_open");
	}
	
	@UiHandler("item2")
	public void onMouseOutItem2(MouseOutEvent event)
	{
		item2_view.removeStyleName("Shop_s1_open");
	}
	
	@UiHandler("item3")
	public void onMouseOverItem3(MouseOverEvent event)
	{
		item3_view.addStyleName("Shop_s1_open");
	}
	
	@UiHandler("item3")
	public void onMouseOutItem3(MouseOutEvent event)
	{
		item3_view.removeStyleName("Shop_s1_open");
	}
	
	@UiHandler("item4")
	public void onMouseOverItem4(MouseOverEvent event)
	{
		item4_view.addStyleName("Shop_s1_open");
	}
	
	@UiHandler("item4")
	public void onMouseOutItem4(MouseOutEvent event)
	{
		item4_view.removeStyleName("Shop_s1_open");
	}
	
	@UiHandler("item5")
	public void onMouseOverItem5(MouseOverEvent event)
	{
		item5_view.addStyleName("Shop_s1_open");
	}
	
	@UiHandler("item5")
	public void onMouseOutItem5(MouseOutEvent event)
	{
		item5_view.removeStyleName("Shop_s1_open");
	}
	
	@UiHandler("item6")
	public void onMouseOverItem6(MouseOverEvent event)
	{
		item6_view.addStyleName("Shop_s1_open");
	}
	
	@UiHandler("item6")
	public void onMouseOutItem6(MouseOutEvent event)
	{
		item6_view.removeStyleName("Shop_s1_open");
	}
}
