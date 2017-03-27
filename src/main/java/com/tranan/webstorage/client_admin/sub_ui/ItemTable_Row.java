package com.tranan.webstorage.client_admin.sub_ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.Ruler;
import com.tranan.webstorage.client_admin.ui.ItemTable;
import com.tranan.webstorage.client_admin.ui.LoginPage;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Item.Type;
import com.tranan.webstorage.shared.Photo;

public class ItemTable_Row extends Composite {

	private static ItemTable_RowUiBinder uiBinder = GWT
			.create(ItemTable_RowUiBinder.class);

	interface ItemTable_RowUiBinder extends UiBinder<Widget, ItemTable_Row> {
	}

	@UiField
	HTMLPanel itemRow;
	@UiField
	HTMLPanel itemName;
	@UiField
	Image itemImg;
	@UiField
	Label nameText;
	@UiField
	Label costText;
	@UiField
	Label priceText;
	@UiField
	HTMLPanel typeTable;
	@UiField
	Label saleText;
	
	private Item item;
	private ItemTableRowListener listener;
	
	public interface ItemTableRowListener {
		void onClickItem(Item item);
		void onDeleteItem(Item item);
	}

	public ItemTable_Row(ItemTableRowListener listener) {
		initWidget(uiBinder.createAndBindUi(this));

		itemName.setWidth(Ruler.ItemTableRow_itemname_W + "px");
		
		this.listener = listener;
	}

	public void setItem(final Item item) {
		this.item = item;
		nameText.setText(item.getName());
		costText.setText(PrettyGal.integerToPriceString(item.getCost()));
		priceText.setText(PrettyGal.integerToPriceString(item.getPrice()));
		typeTable.clear();
		for (Type type : item.getType()) {
			HTMLPanel panel = new HTMLPanel("");
			panel.setStyleName("ItemTableRow_s2");
			Label lb1 = new Label();
			lb1.setStyleName("ItemTableRow_s3");
			if (type.getName().equals(Item.DEFAULT_TYPE))
				lb1.setText(type.getQuantity() + "");
			else
				lb1.setText(type.getName() + ": " + type.getQuantity());
			// Label lb2 = new Label();
			// lb2.setStyleName("ItemTableRow_s3");
			// lb2.setText(type.getQuantity()+ "");

			panel.add(lb1);
			// panel.add(lb2);
			typeTable.add(panel);
		}
		saleText.setText(String.valueOf(item.getSale()) + " %");
		
		final int index = ItemTable.listItem.getListItem().indexOf(item);
		final Item i = ItemTable.listItem.getListItem().get(index);
		if(!i.getAvatar_url().isEmpty())
			itemImg.setUrl(i.getAvatar_url());
		else {
			if (!i.getPhoto_ids().isEmpty()) {
				PrettyGal.dataService.getPhoto(i.getPhoto_ids().get(0),
						new AsyncCallback<Photo>() {

							@Override
							public void onSuccess(Photo result) {
								itemImg.setUrl(result.getServeUrl());
								i.setAvatar_url(result.getServeUrl());
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

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if(Window.confirm("Bạn muốn xóa sản phẩm này?")) {
			NoticePanel.onLoading();
			PrettyGal.dataService.deleteItem(item.getId(), LoginPage.id_token, new AsyncCallback<Boolean>() {
				
				@Override
				public void onSuccess(Boolean result) {
					if(result) {
						listener.onDeleteItem(item);	
						NoticePanel.successNotice("Sản phẩm đã bị xóa khỏi danh sách");
					} else
						NoticePanel.failNotice("");
				}
				
				@Override
				public void onFailure(Throwable caught) {
					NoticePanel.failNotice(caught.getMessage());
				}
			});
		}
	}
	
	@UiHandler("itemViewButton")
	void onItemViewButtonClick(ClickEvent event) {
		listener.onClickItem(item);
	}
}
