package com.tranan.webstorage.client.sub_ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CartItem extends Composite {

	private static CartItemUiBinder uiBinder = GWT
			.create(CartItemUiBinder.class);

	interface CartItemUiBinder extends UiBinder<Widget, CartItem> {
	}

	@UiField
	TextBox txbQuantity;
	@UiField
	Image itemImg;

	public CartItem() {
		initWidget(uiBinder.createAndBindUi(this));

		txbQuantity.addStyleName("CartItem_s1");
	}

	public void setItemImg(String uri) {
		itemImg.setUrl(uri);
	}

}
