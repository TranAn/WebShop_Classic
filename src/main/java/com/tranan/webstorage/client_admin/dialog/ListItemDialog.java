package com.tranan.webstorage.client_admin.dialog;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.sub_ui.Pager;
import com.tranan.webstorage.client_admin.sub_ui.Pager.PagerListener;
import com.tranan.webstorage.client_admin.ui.ItemTable;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Item.Type;
import com.tranan.webstorage.shared.ListItem;
import com.tranan.webstorage.shared.Photo;

public class ListItemDialog extends DialogBox {

	private static ListItemDialogUiBinder uiBinder = GWT
			.create(ListItemDialogUiBinder.class);

	interface ListItemDialogUiBinder extends UiBinder<Widget, ListItemDialog> {
	}

	@UiField
	HTMLPanel dialog;
	@UiField
	Pager pager;
	@UiField
	ScrollPanel scroll;
	@UiField
	HTMLPanel itemTable;
	@UiField 
	TextBox searchTxb;
	@UiField
	HTMLPanel searchButtonPanel;
	@UiField
	HTMLPanel cancelSearchButtonPanel;
	@UiField
	Image loadImg;
	@UiField
	HTMLPanel failNoticePanel;

	float opacity;
	Timer t;
	
	public interface ListItemDialog_Listener {
		void onSelectedItem(List<Item> selectedItems);
	}
	
	private ListItemDialog_Listener listener;
	private List<Item> displayItem;
	private List<Item> selectedItem = new ArrayList<Item>();
	
	private ListItem listItemSearch;
	private String search_string;
	
	private boolean showItemType = true;

	void getListItem(String cursor) {
		onLoading();
		if(ItemTable.listItem == null || !cursor.equals("")) {
			PrettyGal.dataService.getItems(cursor, new AsyncCallback<ListItem>() {
	
				@Override
				public void onSuccess(ListItem result) {
					if (ItemTable.listItem == null) {
						if (result != null)
							ItemTable.listItem = result;
						else {
							ItemTable.listItem = new ListItem();
							ItemTable.listItem.setTotal(0);
						}
						initPager(result);
					} else {
						ItemTable.listItem.setCursorStr(result.getCursorStr());
						ItemTable.listItem.getListItem().addAll(result.getListItem());
					}
				
					setItemTableView(result.getListItem());
					endLoading();
				}
	
				@Override
				public void onFailure(Throwable caught) {
					failLoading();
				}
			});
		}
		else {
			initPager(ItemTable.listItem);
			
			List<Item> displayItem;
			if (ListItem.pageSize >= ItemTable.listItem.getListItem().size())
				displayItem = ItemTable.listItem.getListItem().subList(0, ItemTable.listItem.getListItem().size());
			else
				displayItem = ItemTable.listItem.getListItem().subList(0, ListItem.pageSize);

			setItemTableView(displayItem);
			endLoading();
		}
	}
	
	void searchItem(String search_string) {
		onLoading();
		PrettyGal.dataService.searchItems(search_string, new AsyncCallback<ListItem>() {

			@Override
			public void onSuccess(ListItem result) {
				searchButtonPanel.setVisible(false);
				cancelSearchButtonPanel.setVisible(true);
				
				if(result != null)
					listItemSearch = result;
				else {
					listItemSearch = new ListItem();
					listItemSearch.setTotal(0);
				}	
				
				initPager(result);
				setItemTableView(result.getListItem());
				endLoading();
			}

			@Override
			public void onFailure(Throwable caught) {
				failLoading();
			}
		});
	}
	
	void initPager(ListItem items) {
		pager.setPage(items.getTotal(), ListItem.pageSize, new PagerListener() {

			@Override
			public void pageIndex(int index) {
				if(listItemSearch == null) {
					if (index < ItemTable.listItem.getListItem().size()) {
						List<Item> displayItem;
						if ((index + ListItem.pageSize) <= ItemTable.listItem.getListItem()
								.size())
							displayItem = ItemTable.listItem.getListItem().subList(index,
									index + ListItem.pageSize);
						else
							displayItem = ItemTable.listItem.getListItem().subList(index,
									ItemTable.listItem.getListItem().size());
	
						setItemTableView(displayItem);
					} else {
						getListItem(ItemTable.listItem.getCursorStr());					
					}
				} else {
					List<Item> displayItem;
					if ((index + ListItem.pageSize) <= listItemSearch.getListItem().size())
						displayItem = listItemSearch.getListItem().subList(index,
								index + ListItem.pageSize);
					else
						displayItem = listItemSearch.getListItem().subList(index,
								listItemSearch.getListItem().size());

					setItemTableView(displayItem);
				}
				scroll.scrollToTop();
			}
		});
	}
	
	void setItemTableView(List<Item> items) {
		itemTable.clear();
		for (final Item item : items) {
			HTMLPanel panel1 = new HTMLPanel("");
			panel1.addStyleName("ListItemDialog_table");
			
			HTMLPanel panel2 = new HTMLPanel("");
			panel2.addStyleName("ListItemDialog_table_col1");
			
			HTMLPanel panel3 = new HTMLPanel("");
			panel3.setStyleName("ListItemDialog_table_col2");
			final Image img = new Image();
			img.setSize("45px", "45px");
			panel3.add(img);
			
			HTMLPanel panel4 = new HTMLPanel("");
			panel4.setStyleName("ListItemDialog_table_col3");
			
			Label lb2 = new Label(PrettyGal.integerToPriceString(item.getPrice()));
			lb2.setStyleName("ListItemDialog_table_col4");
			Label lb3 = new Label(item.getSale()+"%");
			lb3.setStyleName("ListItemDialog_table_col4");
			
			HTMLPanel panel5 = new HTMLPanel("");
			panel5.setStyleName("ListItemDialog_table_col5");
			
			for(final Type t: item.getType()) {
				final CheckBox cb = new CheckBox();
				panel2.add(cb);
				Item i = new Item(item);
				i.getType().clear();
				Type new_type = new Type(t.getName(), t.getQuantity());
				i.getType().add(new_type);
				if(selectedItem.contains(i))
					cb.setValue(true);
				
				Label lbName = new Label();
				if(!t.getName().equals(Item.DEFAULT_TYPE) && showItemType) 
					lbName.setText(item.getName()+ " ("+ t.getName()+ ")");
				else
					lbName.setText(item.getName());
				panel4.add(lbName);
								
				Label lbQuantity = new Label();
				if(showItemType)
					lbQuantity.setText(String.valueOf(t.getQuantity()));
				else {
					int quantity = 0;
					for(Type tt: item.getType())
						quantity = quantity + tt.getQuantity();
					lbQuantity.setText(String.valueOf(quantity));
				}
				panel5.add(lbQuantity);
				
				if(item.getType().indexOf(t) != item.getType().size() - 1 && showItemType) {
					HTMLPanel div1 = new HTMLPanel("<div style='margin-bottom: 20px;'></div>");
					HTMLPanel div2 = new HTMLPanel("<div style='margin-bottom: 20px;'></div>");
					HTMLPanel div3 = new HTMLPanel("<div style='margin-bottom: 20px;'></div>");
					panel2.add(div1);
					panel4.add(div2);
					panel5.add(div3);
				}
				
				cb.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						Item i = new Item(item);
						if(cb.getValue()) {
							i.getType().clear();
							Type new_type = new Type(t.getName(), t.getQuantity());
							i.getType().add(new_type);
							selectedItem.add(i);
						}
						else {
							i.getType().clear();
							Type new_type = new Type(t.getName(), t.getQuantity());
							i.getType().add(new_type);
							selectedItem.remove(i);
						}
					}
				});
				
				if(!showItemType)
					break;
			}
			
			panel1.add(panel2);
			panel1.add(panel3);
			panel1.add(panel4);			
			panel1.add(lb2);
			panel1.add(lb3);
			panel1.add(panel5);
			
			itemTable.add(panel1);
			
			if(!item.getAvatar_url().isEmpty())
				img.setUrl(item.getAvatar_url());
			else {
				if (!item.getPhoto_ids().isEmpty()) {
					PrettyGal.dataService.getPhoto(item.getPhoto_ids().get(0),
							new AsyncCallback<Photo>() {

								@Override
								public void onSuccess(Photo result) {
									img.setUrl(result.getServeUrl());
									int index = ItemTable.listItem.getListItem().indexOf(item);
									ItemTable.listItem.getListItem().get(index).setAvatar_url(result.getServeUrl());
									item.setAvatar_url(result.getServeUrl());
								}

								@Override
								public void onFailure(Throwable caught) {
								}
							});
				} else {
					img.setUrl("../Resources/photoDefault.png");
				}
			}
		}
		
		displayItem = items;
	}
	
	public void cancelSearch() {
		searchTxb.setWidth("0px");
		searchTxb.setVisible(false);
		searchTxb.setText("");
		searchButtonPanel.setVisible(true);
		searchButtonPanel.removeStyleName("ListItemDialg_s7focus");
		cancelSearchButtonPanel.setVisible(false);
		search_string = "";
		listItemSearch = null;
		
		initPager(ItemTable.listItem);
			
		int fromIndex = 0;
		int toIndex = fromIndex + ListItem.pageSize;
		if(toIndex >= ItemTable.listItem.getListItem().size())
			toIndex = ItemTable.listItem.getListItem().size();
		setItemTableView(ItemTable.listItem.getListItem().subList(fromIndex, toIndex));
	}

	public ListItemDialog(ListItemDialog_Listener listener, boolean showItemType) {
		setWidget(uiBinder.createAndBindUi(this));

		setGlassEnabled(true);
		setAutoHideEnabled(true);
		setStyleName("ConfirmDialog_s1");
		setAnimationEnabled(false);
		
		this.listener = listener;
		this.showItemType = showItemType;

		this.addAttachHandler(new Handler() {

			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached())
					faddingShowDialog();
			}
		});
		
		searchTxb.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if(searchTxb.getText().isEmpty()) {
					searchTxb.getElement().setAttribute("style", "display: none");
					searchButtonPanel.removeStyleName("ListItemDialg_s7focus");
				}
				else {
					if(!searchTxb.getText().equals(search_string)) {
						listItemSearch = null;						
						search_string = searchTxb.getText();
						searchItem(search_string);
					}
				}
			}
		});
		
		searchTxb.addKeyPressHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(KeyPressEvent event_)
            {
                boolean enterPressed = KeyCodes.KEY_ENTER == event_
                        .getNativeEvent().getKeyCode();
                if (enterPressed)
                {
                    searchTxb.setFocus(false);
                }
            }
        });
		
		String height = Window.getClientHeight() / 2 + "px";
		scroll.getElement().setAttribute("style", "overflow-y:scroll; height:" + height);
		getListItem("");
	}
	
	void onLoading() {
		loadImg.setVisible(true);
		failNoticePanel.setVisible(false);
	}
	
	void endLoading() {
		loadImg.setVisible(false);
		failNoticePanel.setVisible(false);
	}
	
	void failLoading() {
		loadImg.setVisible(false);
		failNoticePanel.setVisible(true);
	}

	void faddingShowDialog() {
		opacity = 0f;
		final String width = Window.getClientWidth() / 1.8 + "px";
		dialog.getElement().setAttribute("style", "opacity: 0; width: " + width);
		t = new Timer() {

			@Override
			public void run() {
				opacity = opacity + 0.25f;
				dialog.getElement().setAttribute("style",
						"opacity:" + String.valueOf(opacity) + "; width: " + width);

				if (opacity == 1)
					t.cancel();
			}
		};
		t.scheduleRepeating(50);
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		switch (event.getTypeInt()) {
		case Event.ONKEYDOWN:
			if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
				hide();
			}
			break;
		}
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent e) {
		listener.onSelectedItem(selectedItem);
		hide();
	}
	
	@UiHandler("exitButton")
	void onExitButtonClick(ClickEvent e) {
		hide();
	}
	
	@UiHandler("searchButton")
	void onSearchButtonClick(ClickEvent e) {
		searchTxb.getElement().setAttribute("style", "");
		Timer t = new Timer() {

			@Override
			public void run() {
				searchTxb.setWidth("200px");
				searchTxb.setFocus(true);
				searchButtonPanel.addStyleName("ListItemDialg_s7focus");
			}
			
		};
		t.schedule(100);
	}
	
	@UiHandler("cancelSearchButton")
	void onCancelSearchButtonClick(ClickEvent e) {
		cancelSearch();
	}

}
