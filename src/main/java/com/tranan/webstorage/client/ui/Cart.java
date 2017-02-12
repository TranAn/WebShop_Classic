package com.tranan.webstorage.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client.sub_ui.CartItem;

public class Cart extends Composite {

	private static CartUiBinder uiBinder = GWT.create(CartUiBinder.class);

	interface CartUiBinder extends UiBinder<Widget, Cart> {
	}
	
	@UiField HTMLPanel cart_items; 

	public Cart() {
		initWidget(uiBinder.createAndBindUi(this));
		
		CartItem item1 = new CartItem();
		CartItem item2 = new CartItem();
		item1.setItemImg("Resources/6.png");
		item2.setItemImg("Resources/2.png");
		cart_items.add(item1);
		cart_items.add(item2);
	}

}
