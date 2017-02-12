package com.tranan.webstorage.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class Item extends Composite {

	private static ItemUiBinder uiBinder = GWT.create(ItemUiBinder.class);

	interface ItemUiBinder extends UiBinder<Widget, Item> {
	}
	
	@UiField ListBox quantity;

	public Item() {
		initWidget(uiBinder.createAndBindUi(this));
		
		quantity.addItem("1");
		quantity.addItem("2");
		quantity.addItem("3");
		quantity.addItem("4");
		quantity.addItem("5");
	}

}
