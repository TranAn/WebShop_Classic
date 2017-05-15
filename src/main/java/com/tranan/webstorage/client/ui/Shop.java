package com.tranan.webstorage.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client.PrettyGal;
import com.tranan.webstorage.client.sub_ui.Pager;
import com.tranan.webstorage.client.sub_ui.Pager.PagerListener;
import com.tranan.webstorage.shared.ListItem;
import com.tranan.webstorage.shared.Photo;

public class Shop extends Composite {

	private static ShopUiBinder uiBinder = GWT.create(ShopUiBinder.class);

	interface ShopUiBinder extends UiBinder<Widget, Shop> {
	}
	
	@UiField HTMLPanel itemTable;
	@UiField Pager pager;
	
	private ListItem listItem;
	
	private void getItem(String cursor) {
		int page_size = ListItem.pageSize + 5;
		PrettyGal.dataService.getItems(cursor, page_size, new AsyncCallback<ListItem>() {			
			@Override
			public void onSuccess(ListItem result) {				
				if (listItem == null) {
					if(result != null)
						listItem = result;
					else {
						listItem = new ListItem();
						listItem.setTotal(0);
					}
					initItemPager(result);
				} else {
					listItem.setCursorStr(result.getCursorStr());
					listItem.getListItem().addAll(result.getListItem());
				}
				
				setItemToView(result.getListItem());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}
		});
	}
	
	private void initItemPager(ListItem list_item) {
		final int page_size = ListItem.pageSize + 5;
		pager.setPage(list_item.getTotal(), page_size, new PagerListener() {
			
			@Override
			public void pageIndex(int index) {
				if (index < listItem.getListItem().size()) {
					List<com.tranan.webstorage.shared.Item> displayItem;
					if(listItem.getListItem().size() != 0) {
						if ((index + page_size) <= listItem.getListItem().size())
							displayItem = listItem.getListItem().subList(index, index + page_size);
						else
							displayItem = listItem.getListItem().subList(index, listItem.getListItem().size());
					}
					else
						displayItem = new ArrayList<com.tranan.webstorage.shared.Item>();

					setItemToView(displayItem);
				} else {
					getItem(listItem.getCursorStr());					
				}
				
				Window.scrollTo(0, 0);
			}
		});
	}
	
	private void setItemToView(List<com.tranan.webstorage.shared.Item> list_item) {
		itemTable.clear();
		for(final com.tranan.webstorage.shared.Item item: list_item) {
			HTMLPanel itemPanel = new HTMLPanel("");
			itemPanel.setStyleName("Shop_itemPanel");
			
			HTMLPanel itemImgPanel = new HTMLPanel("");
			itemImgPanel.setStyleName("Shop_itemImgPanel");
			
			Anchor itemAnchor = new Anchor();
			itemAnchor.setStyleName("Shop_itemAnchor");
			final Image itemImg = new Image();
			itemImg.setStyleName("Shop_itemImg");
			final HTMLPanel itemQuickView = new HTMLPanel("Đặt Hàng");
			itemQuickView.setStyleName("Shop_itemQuickViewBtn");
			
			itemAnchor.addMouseOverHandler(new MouseOverHandler() {
				
				@Override
				public void onMouseOver(MouseOverEvent event) {
					itemQuickView.addStyleName("Shop_itemQuickViewBtn_show");
				}
			});
			
			itemAnchor.addMouseOutHandler(new MouseOutHandler() {
				
				@Override
				public void onMouseOut(MouseOutEvent event) {
					itemQuickView.removeStyleName("Shop_itemQuickViewBtn_show");
				}
			});
			
			itemAnchor.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					Window.Location.assign("/item/" + item.getName() + "-" + item.getId());
				}
			});
			
			Label itemName = new Label(item.getName());
			itemName.setStyleName("Shop_itemLb1");
			
			Label itemPrice = new Label(PrettyGal.integerToPriceString(item.getPrice()));
			itemPrice.setStyleName("Shop_itemLb2");
			
			Label itemSalePrice = new Label();
			if(item.getSale() != 0) {
				itemSalePrice.setText(PrettyGal.integerToPriceString(item.getSale_price()) + " (-" + item.getSale() + "%)");
				itemPrice.addStyleName("Shop_itemLb2_LineThrough");
			}
			itemSalePrice.setStyleName("Shop_itemLb3");
			
			itemImgPanel.add(itemAnchor);
			itemImgPanel.add(itemImg);
			itemImgPanel.add(itemQuickView);
			
			itemPanel.add(itemImgPanel);
			itemPanel.add(itemName);
			itemPanel.add(itemPrice);
			itemPanel.add(itemSalePrice);
			
			itemTable.add(itemPanel);
			
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
	}

	public Shop() {
		initWidget(uiBinder.createAndBindUi(this));
		
		getItem("");
	}
}
