package com.tranan.webstorage.client.sub_ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client.PrettyGal;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Photo;

public class CartItem extends Composite {

	private static CartItemUiBinder uiBinder = GWT
			.create(CartItemUiBinder.class);

	interface CartItemUiBinder extends UiBinder<Widget, CartItem> {
	}
	
	@UiField
	Label itemName;
	@UiField
	Label itemPrice;
	@UiField
	Label itemSalePrice;
	@UiField
	TextBox txbQuantity;
	@UiField
	Label total;
	@UiField
	Image itemImg;
	@UiField
	Anchor increaseItemBtn;
	@UiField
	Anchor decreaseItemBtn;
	
	private Long current_total_price;
	
	public interface CartItemListener {
		void onItemQuantityChange(Long old_total_price, Long total_price, int quantity);
		void onItemDelete(Long total_price);
	}
	
	private CartItemListener listener;
	
	public void setListener(CartItemListener listener) {
		this.listener = listener;
	}

	public CartItem(final Item item) {
		initWidget(uiBinder.createAndBindUi(this));
		
		if(item.getSale() == 0) {
			current_total_price = item.getPrice() * Integer.valueOf(txbQuantity.getText());
		}
		else {
			current_total_price = item.getSale_price() * Integer.valueOf(txbQuantity.getText());
		}

		txbQuantity.addStyleName("CartItem_s1");
		txbQuantity.getElement().setAttribute("min", "0");
		
		txbQuantity.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				txbQuantity.setSelectionRange(0, txbQuantity.getText().length());
			}
		});
		
		txbQuantity.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				char ch = event.getCharCode();
				if (ch != '0' && ch != '1' && ch != '2' && ch != '3'
						&& ch != '4' && ch != '5' && ch != '6' && ch != '7'
						&& ch != '8' && ch != '9') {
					txbQuantity.cancelKey();
				}
			}
		});
		
		txbQuantity.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				int quantity = Integer.valueOf(txbQuantity.getText());
				Long total_priceLong;
				if(item.getSale() == 0) {
					total_priceLong = item.getPrice() * quantity;
				}
				else {
					total_priceLong = item.getSale_price() * quantity;
				}
				
				total.setText(PrettyGal.integerToPriceString(total_priceLong));			
				listener.onItemQuantityChange(current_total_price, total_priceLong, quantity);
				current_total_price = total_priceLong;
			}
		});
		
		increaseItemBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int quantity = Integer.valueOf(txbQuantity.getText());
				quantity++;
				
				txbQuantity.setText(String.valueOf( quantity ));
				
				Long total_priceLong;
				if(item.getSale() == 0) {
					total_priceLong = item.getPrice() * quantity;
				}
				else {
					total_priceLong = item.getSale_price() * quantity;
				}
				
				total.setText(PrettyGal.integerToPriceString(total_priceLong));				
				listener.onItemQuantityChange(current_total_price, total_priceLong, quantity);
				current_total_price = total_priceLong;
			}
		});
		
		decreaseItemBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int quantity = Integer.valueOf(txbQuantity.getText());
				quantity--;
				
				if(quantity >= 0) {
					txbQuantity.setText(String.valueOf( quantity ));
				
					Long total_priceLong;
					if(item.getSale() == 0) {
						total_priceLong = item.getPrice() * quantity;
					}
					else {
						total_priceLong = item.getSale_price() * quantity;
					}
					
					total.setText(PrettyGal.integerToPriceString(total_priceLong));				
					listener.onItemQuantityChange(current_total_price, total_priceLong, quantity);
					current_total_price = total_priceLong;
				}
			}
		});
		
		if(item.getType().get(0).getName().equals(Item.DEFAULT_TYPE))
			itemName.setText(item.getName());
		else
			itemName.setText(item.getName() + " (" + item.getType().get(0).getName() + ")");
		
		if(item.getSale() == 0) {
			itemPrice.setText(PrettyGal.integerToPriceString(item.getPrice()));
			total.setText(PrettyGal.integerToPriceString(item.getPrice()));
		}
		else {
			itemPrice.setText(PrettyGal.integerToPriceString(item.getPrice()));
			itemPrice.setStyleName("CartItem_SalePrice");
			itemSalePrice.setVisible(true);
			itemSalePrice.setText(PrettyGal.integerToPriceString(item.getSale_price()));
			total.setText(PrettyGal.integerToPriceString(item.getSale_price()));
		}
		
		if(!item.getAvatar_url().isEmpty())
			itemImg.setUrl(item.getAvatar_url());
		else {
			if (!item.getPhoto_ids().isEmpty()) {
				PrettyGal.dataService.getPhoto(item.getPhoto_ids().get(0),
						new AsyncCallback<Photo>() {

							@Override
							public void onSuccess(Photo result) {
								itemImg.setUrl(result.getServeUrl());
								item.setAvatar_url(result.getServeUrl());
							}

							@Override
							public void onFailure(Throwable caught) {
							}
						});
			} else {
				itemImg.setUrl("../Resources/photoDefault.png");
			}
		}
	}
	
	@UiHandler("removeItemBtn")
	void onRemoveItemBtnClick(ClickEvent e) {
		listener.onItemDelete(current_total_price);
	}

}
