package com.tranan.webstorage.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client.PrettyGal;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Item.Type;
import com.tranan.webstorage.shared.Photo;

public class ItemUI extends Composite {

	private static ItemUiBinder uiBinder = GWT.create(ItemUiBinder.class);

	interface ItemUiBinder extends UiBinder<Widget, ItemUI> {
	}

	/* @UiField ListBox quantity; */
	@UiField
	Label itemNameLb;
	@UiField
	HTMLPanel photoThumbnailPanel;
	@UiField
	Image itemPhoto;
	@UiField
	HTML itemDescription;
	@UiField
	HTMLPanel typeTable;
	@UiField
	Label itemPriceText;
	@UiField
	Label itemSalePriceText;
	@UiField
	Label itemPriceTitle;
	
	Item item;
	
	List<String> photo_urls = new ArrayList<String>();
	List<HTMLPanel> list_thumbnail = new ArrayList<HTMLPanel>();
	
	List<Type> selectedType = new ArrayList<Type>();
	List<Item> itemTypes = new ArrayList<Item>();
	
	private void saveOrderItems() {
		if(selectedType.isEmpty()) {
				
		}
		else {
			item.getType().clear();
			for(Type t: selectedType) {
				Item new_item = new Item(item);
				new_item.getType().add(t);
				itemTypes.add(new_item);
			}
			
			long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login - 2 weeks
			Date expires = new Date(System.currentTimeMillis() + DURATION);
			
			String order_items = Cookies.getCookie(PrettyGal.ORDER_ITEMS);
			
			if(order_items == null || order_items.isEmpty())
				order_items = "";
			
			for(Item item: itemTypes) {		
				if( !order_items.contains(item.compactItem()) )
					order_items = order_items + PrettyGal.COOKIE_DELIMITER + item.compactItem();			
			}
			Cookies.setCookie(PrettyGal.ORDER_ITEMS, order_items, expires, null, "/", false);
			
			PrettyGal.showHeaderItemOrder();
		}		
	}

	public ItemUI() {
		initWidget(uiBinder.createAndBindUi(this));

		getItem();
	}

	public void getItem() {
		String item_json = RootPanel.get("item").getElement().getInnerHTML();
		Item item = new Item().fromJson(item_json);
		this.item = item;

		itemNameLb.setText(item.getName());
		
		itemPriceText.setText(PrettyGal.integerToPriceString(item.getPrice()));
		if(item.getSale() != 0) {
			itemPriceText.addStyleName("ItemUi_PriceLineThrough");
			itemSalePriceText.setVisible(true);
			itemSalePriceText.setText(PrettyGal.integerToPriceString(item.getSale_price()));
			itemPriceTitle.setVisible(true);
			itemPriceTitle.setText("(" + PrettyGal.ITEM_SALE_TITLE + " " + item.getSale() + "%)");
		}
			
		
		itemDescription.setHTML(item.getDescription());
		
		typeTable.clear();
		for(final Type t: item.getType()) {
			if(t.getName().equals(Item.DEFAULT_TYPE)) {
				Label type_status = new Label();
				type_status.setStyleName("ItemUi_TypeName");
				if(t.getQuantity() > 0)
					type_status.setText(PrettyGal.ITEM_AVAILABLE);
				else
					type_status.setText(PrettyGal.ITEM_UNAVAILABLE);
				
				selectedType.add(t);
				typeTable.add(type_status);
			}
			else {
				HorizontalPanel panel = new HorizontalPanel();
				panel.setStyleName("ItemUi_TypeRow");
				final CheckBox type_check = new CheckBox();
				type_check.setStyleName("ItemUi_TypeCheckBox");
				Label type_Name = new Label();
				type_Name.setStyleName("ItemUi_TypeName");
				if(t.getQuantity() > 0)
					type_Name.setText(t.getName() + " " + PrettyGal.ITEM_AVAILABLE);
				else
					type_Name.setText(t.getName() + " " + PrettyGal.ITEM_UNAVAILABLE);
				
				type_check.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						if(type_check.getValue())
							selectedType.add(t);
						else
							selectedType.remove(t);
					}
				});
				
				panel.add(type_check);
				panel.add(type_Name);
				typeTable.add(panel);
			}
		}
		
		photoThumbnailPanel.clear();
		getPhotos(item.getPhoto_ids());
	}

	public void getPhotos(final List<Long> photo_ids) {
		list_thumbnail.clear();
		for (final Long photo_id : photo_ids) {
			PrettyGal.dataService.getPhoto(photo_id,
					new AsyncCallback<Photo>() {

						@Override
						public void onSuccess(final Photo result) {
							final HTMLPanel imgPanel = new HTMLPanel("");
							list_thumbnail.add(imgPanel);
							if(photo_ids.indexOf(photo_id) == 0) {
								imgPanel.setStyleName("ItemUi_PhotoThumbNail_active");
								itemPhoto.setUrl(result.getServeUrl());
							}
							else {
								imgPanel.setStyleName("ItemUi_PhotoThumbNail");
							}
							Image img = new Image();
							img.setSize("100%", "100%");
							img.setUrl(result.getServeUrl());
							imgPanel.add(img);
							
							Anchor anchor = new Anchor();
							anchor.setStyleName("anchor");
							imgPanel.add(anchor);
							
							anchor.addClickHandler(new ClickHandler() {
								
								@Override
								public void onClick(ClickEvent event) {
									itemPhoto.setUrl(result.getServeUrl());
									for(HTMLPanel thumbnail: list_thumbnail) {
										thumbnail.setStyleName("ItemUi_PhotoThumbNail");
									}
									imgPanel.setStyleName("ItemUi_PhotoThumbNail_active");
									
								}
							});
							
							photoThumbnailPanel.add(imgPanel);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		}
	}
	
	@UiHandler("addItemBtn")
	void onAddItemButtonClick(ClickEvent e) {
		saveOrderItems();
	}
	
	@UiHandler("orderItemBtn")
	void onOrderItemButtonClick(ClickEvent e) {
		saveOrderItems();
		Window.Location.assign("/#cart");
	}

}
