package com.tranan.webstorage.client_admin.sub_ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.Ruler;
import com.tranan.webstorage.client_admin.ui.ItemTable;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Item.Type;
import com.tranan.webstorage.shared.Photo;

public class ItemImportTable extends Composite {

	private static ItemImportTableUiBinder uiBinder = GWT
			.create(ItemImportTableUiBinder.class);

	interface ItemImportTableUiBinder extends UiBinder<Widget, ItemImportTable> {
	}
	
	@UiField HTMLPanel itemTable;
	
	public List<Item> selectedItems = new ArrayList<Item>();
	public HashMap<Integer, List<IntegerBox>> itemsCountBox = new HashMap<Integer, List<IntegerBox>>();
	
	private ItemImportTable_Listener listener;
	
	public interface ItemImportTable_Listener {
		void onImportSuccess(List<Item> result);
		void onImportFail();
	}

	public ItemImportTable() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void clearSelectedItems() {
		selectedItems.clear();
	}
	
	public void startImportItems(ItemImportTable_Listener l) {
		this.listener = l;
		NoticePanel.onLoading();
		
		List<Item> orderIn_items = new ArrayList<Item>();
		for(int i=0; i<selectedItems.size(); i++) {
			
			Item item = new Item(selectedItems.get(i).getId(), selectedItems.get(i).getPhoto_ids(), selectedItems.get(i).getCatalog_ids(),
					selectedItems.get(i).getName(), selectedItems.get(i).getCost(), selectedItems.get(i).getPrice(), selectedItems.get(i).getSale(), 
					selectedItems.get(i).getDescription(), new ArrayList<Type>(), selectedItems.get(i).getAvatar_url());
			orderIn_items.add(item);
			
			for(int j=0; j<itemsCountBox.get(i).size(); j++) {
				Type itemType = selectedItems.get(i).getType().get(j);
				if(!itemsCountBox.get(i).get(j).getText().isEmpty()) {
					itemType.setQuantity(itemType.getQuantity() + itemsCountBox.get(i).get(j).getValue());
					orderIn_items.get(i).getType().add(j, new Type(itemType.getName(), itemsCountBox.get(i).get(j).getValue()));
				}
				else {
					orderIn_items.get(i).getType().add(j, new Type(itemType.getName(), 0));
				}
			}
		}
		
		PrettyGal.dataService.importItems(selectedItems, orderIn_items, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				listener.onImportSuccess(selectedItems);
				NoticePanel.successNotice("Sản phẩm đã được thêm vào kho hàng");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				listener.onImportFail();
				NoticePanel.failNotice(PrettyGal.ERROR_MSG);
			}
		});
	}
	
	public void setDisplayItem(List<Item> itemsDisplay) {
		itemTable.clear();
		for(Item item: itemsDisplay) {
			if( (itemsDisplay.indexOf(item)+1) % 3 == 0 )
				addItemView(item, true);
			else
				addItemView(item, false);
		}
	}
	
	public void setImportItems() {
		itemTable.clear();
		itemsCountBox.clear();
		for(Item item: selectedItems) {
			if( (selectedItems.indexOf(item)+1) % 3 == 0 )
				addItemImportView(item, true);
			else
				addItemImportView(item, false);
		}
	}
	
	void addItemImportView(final Item item, boolean isRightItem) {
		final HTMLPanel itemPanel = new HTMLPanel("");
		itemPanel.setWidth("100%");
		if(isRightItem)
			itemPanel.setStyleName("ItemImportTable_s9");
		else
			itemPanel.setStyleName("ItemImportTable_s9");
		
		HTMLPanel imgPanel = new HTMLPanel("");
		imgPanel.setStyleName("ItemImportTable_s3");
		final Image img = new Image();
		img.setSize("50px", "50px");
		imgPanel.add(img);
		
		final int index = ItemTable.listItem.getListItem().indexOf(item);
		final Item i = ItemTable.listItem.getListItem().get(index);
		if(!i.getAvatar_url().isEmpty())
			img.setUrl(i.getAvatar_url());
		else {
			if (!i.getPhoto_ids().isEmpty()) {
				PrettyGal.dataService.getPhoto(i.getPhoto_ids().get(0),
						new AsyncCallback<Photo>() {

							@Override
							public void onSuccess(Photo result) {
								img.setUrl(result.getServeUrl());
								i.setAvatar_url(result.getServeUrl());
							}

							@Override
							public void onFailure(Throwable caught) {
							}
						});
			} else {
				img.setUrl("../Resources/photoDefault.png");
			}
		}
		
		Label lb = new Label(item.getName());
		lb.setStyleName("ItemImportTable_s4");
		
		int indx = selectedItems.indexOf(item);
		itemsCountBox.put(indx, new ArrayList<IntegerBox>());
		HTMLPanel typeNameTable = new HTMLPanel("");
		typeNameTable.setStyleName("ItemImportTable_s13");
		HTMLPanel typeQuantityTable = new HTMLPanel("");
		typeQuantityTable.setStyleName("ItemImportTable_s10");
		for(Type t: item.getType()) {
			Label lbType = new Label();
			lbType.setStyleName("ItemImportTable_s12");
			if(!t.getName().equals(Item.DEFAULT_TYPE))
				lbType.setText(t.getName()+ ": ");
			else
				lbType.setText("");
			IntegerBox txbQuantity = new IntegerBox();
			txbQuantity.setStyleName("ItemImportTable_s6");
			txbQuantity.getElement().setAttribute("type", "number");
			
			typeNameTable.add(lbType);
			typeQuantityTable.add(txbQuantity);
			itemsCountBox.get(indx).add(txbQuantity);
		}
		
		itemPanel.add(imgPanel);
		itemPanel.add(lb);
		itemPanel.add(typeNameTable);
		itemPanel.add(typeQuantityTable);
		
		itemTable.add(itemPanel);
	}
	
	void addItemView(final Item item, boolean isRightItem) {
		final HTMLPanel itemPanel = new HTMLPanel("");
		itemPanel.setWidth(Ruler.ItemImportTable_item_W + "px");
		if(isRightItem)
			itemPanel.setStyleName("ItemImportTable_s1");
		else
			itemPanel.setStyleName("ItemImportTable_s1");
		HTMLPanel imgPanel = new HTMLPanel("");
		imgPanel.setStyleName("ItemImportTable_s3");
		final Image img = new Image();
		img.setSize("50px", "50px");
		imgPanel.add(img);
		
		final int index = ItemTable.listItem.getListItem().indexOf(item);
		final Item i = ItemTable.listItem.getListItem().get(index);
		if(!i.getAvatar_url().isEmpty())
			img.setUrl(i.getAvatar_url());
		else {
			if (!i.getPhoto_ids().isEmpty()) {
				PrettyGal.dataService.getPhoto(i.getPhoto_ids().get(0),
						new AsyncCallback<Photo>() {

							@Override
							public void onSuccess(Photo result) {
								img.setUrl(result.getServeUrl());
								i.setAvatar_url(result.getServeUrl());
							}

							@Override
							public void onFailure(Throwable caught) {
							}
						});
			} else {
				img.setUrl("../Resources/photoDefault.png");
			}
		}
		
		Label lb = new Label(item.getName());
		lb.setStyleName("ItemImportTable_s4");
		final CheckBox cb = new CheckBox();
		cb.setStyleName("ItemImportTable_s5");
		if(selectedItems.contains(item)) {
			cb.setValue(true);
			itemPanel.addStyleName("ItemImportTable_s1_active");
		}
		
		Anchor a = new Anchor();
		a.setStyleName("ItemImportTable_s8");
		
		a.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cb.setValue(!cb.getValue());
				if(cb.getValue()) {
					selectedItems.add(item);
					itemPanel.addStyleName("ItemImportTable_s1_active");
				}
				else {
					selectedItems.remove(item);
					itemPanel.removeStyleName("ItemImportTable_s1_active");
				}
			}
		});
		
		itemPanel.add(imgPanel);
		itemPanel.add(lb);
		itemPanel.add(cb);
		itemPanel.add(a);
		
		itemTable.add(itemPanel);
	}

}
