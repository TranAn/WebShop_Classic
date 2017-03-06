package com.tranan.webstorage.client_admin.ui;

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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.Ruler;
import com.tranan.webstorage.client_admin.place.CreateItemPlace;
import com.tranan.webstorage.client_admin.sub_ui.ItemImportTable;
import com.tranan.webstorage.client_admin.sub_ui.ItemImportTable.ItemImportTable_Listener;
import com.tranan.webstorage.client_admin.sub_ui.ItemTable_Row;
import com.tranan.webstorage.client_admin.sub_ui.ItemTable_Row.ItemTableRowListener;
import com.tranan.webstorage.client_admin.sub_ui.NoticePanel;
import com.tranan.webstorage.client_admin.sub_ui.Pager;
import com.tranan.webstorage.client_admin.sub_ui.Pager.PagerListener;
import com.tranan.webstorage.shared.Catalog;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.ListItem;

public class ItemTable extends Composite {

	private static ItemTableUiBinder uiBinder = GWT
			.create(ItemTableUiBinder.class);

	interface ItemTableUiBinder extends UiBinder<Widget, ItemTable> {
	}

	@UiField
	ScrollPanel scrollTable;
	@UiField
	HTMLPanel itemTable;
	@UiField
	HTMLPanel itemImportTable;
	@UiField
	HTMLPanel tableHeader;
	@UiField
	HTMLPanel importTableControlBox;
//	@UiField
//	HTMLPanel categorieTable;
//	@UiField
//	HTMLPanel table;
	@UiField
	HTMLPanel nameColumn;
	@UiField
	TextBox searchTxb;
	@UiField
	Pager itemsPager;
	@UiField
	HTMLPanel nextButtonPanel;
	@UiField
	Anchor nextButton;
	@UiField
	HTMLPanel cancelButtonPanel;
	@UiField
	Anchor cancelButton;
	@UiField
	HTMLPanel applyButtonPanel;
	@UiField
	Anchor applyButton;
	@UiField
	HTMLPanel backButtonPanel;
	@UiField
	Label itemTableTitle;
	@UiField
	HTMLPanel filterButtonPanel;
	@UiField
	HTMLPanel cancelFilterButtonPanel;
	@UiField
	HTMLPanel filterTable;
	@UiField
	HTMLPanel filterTableContent;
	@UiField
	HTMLPanel searchButtonPanel;
	@UiField
	HTMLPanel cancelSearchButtonPanel;

	public static ListItem listItem;
	public static List<Catalog> listCatalog;
	
//	private String next_cursor;
	private ListItem listItemFilterBuffer;
	private ListItem listItemSearchBuffer;
	private String search_string;
	private Long catalog_id;
	private String catalog_name;
	private ItemImportTable importTable;
	private List<Item> displayItem;
	
	void getListCatalog() {
		if(ItemTable.listCatalog == null) {
			NoticePanel.onLoading();
			
			ItemTable.listCatalog = new ArrayList<Catalog>();
			PrettyGal.dataService.getCatalogs(new AsyncCallback<List<Catalog>>() {
				
				@Override
				public void onSuccess(List<Catalog> result) {
					ItemTable.listCatalog.addAll(result);
					NoticePanel.endLoading();
					showFilter(result);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					NoticePanel.endLoading();
				}
			});
		}
	}
	
	void showFilter(final List<Catalog> catalogs) {
		filterTableContent.clear();
		filterTable.setVisible(true);
		
//		HTMLPanel tableHeader = new HTMLPanel("");
//		Label tableHeaderTitle = new Label("Lọc sản phẩm theo Catalog");
//		
//		tableHeader.add(tableHeaderTitle);
//		filterTableContent.add(tableHeader);
		
		for(final Catalog catalog: catalogs) {
			HTMLPanel panel1 = new HTMLPanel("");
			panel1.setStyleName("ItemTable_s3");
			HTMLPanel panel2 = new HTMLPanel("<i class='material-icons ItemTable_s1'>&#xE152;</i>");
			panel2.setStyleName("ItemTable_s4");
			Label lb1 = new Label(catalog.getName());
			lb1.setStyleName("ItemTable_s5");
			
			panel1.add(panel2);
			panel1.add(lb1);
			filterTableContent.add(panel1);
			
			lb1.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					catalog_id = catalog.getId();
					catalog_name = catalog.getName();
					listItemFilterBuffer = new ListItem(listItem.getListItem(), listItem.getCursorStr(), listItem.getTotal());
					listItem = null;
					getListItemInCatalog("0", catalog_id, catalog_name);
					if(importTable != null)
						importTable.clearSelectedItems();
				}
			});
		}
		
		Timer t = new Timer() {

			@Override
			public void run() {
				int i = catalogs.size() / 3;
				if(catalogs.size() % 3 != 0)
					i++;
				filterTableContent.setHeight(40 * i + "px");
			}
			
		};t.schedule(100);
	}

	public void getListItem(String cursor) {
		if(ItemTable.listItem == null || !cursor.equals("")) {
			NoticePanel.onLoading();
			PrettyGal.dataService.getItems(cursor, new AsyncCallback<ListItem>() {
	
				@Override
				public void onSuccess(ListItem result) {
					if (listItem == null) {
						if(result != null)
							listItem = result;
						else {
							listItem = new ListItem();
							listItem.setTotal(0);
						}
						initPager(result);
					} else {
						listItem.setCursorStr(result.getCursorStr());
						listItem.getListItem().addAll(result.getListItem());
					}
					
	//				next_cursor = result.getCursorStr();
					setItemTableView(result.getListItem());
					NoticePanel.endLoading();
				}
	
				@Override
				public void onFailure(Throwable caught) {
					NoticePanel
							.failNotice("Kết nối đến server thất bại, vui lòng kiểm tra lại đường truyền");
				}
			});
		}
		else {
			initPager(listItem);
			
			List<Item> displayItem;
			if (ListItem.pageSize >= listItem.getListItem().size())
				displayItem = listItem.getListItem().subList(0, listItem.getListItem().size());
			else
				displayItem = listItem.getListItem().subList(0, ListItem.pageSize);

			setItemTableView(displayItem);
		}
	}
	
	void getListItemInCatalog(String cursor, Long catalog_id, final String catalog_name) {
		NoticePanel.onLoading();
		PrettyGal.dataService.getItemsInCatalog(cursor, catalog_id, new AsyncCallback<ListItem>() {

			@Override
			public void onSuccess(ListItem result) {
				filterTable.setVisible(false);
				itemTableTitle.setText(catalog_name);
				
				if (listItem == null) {
					if(result != null)
						listItem = result;
					else {
						listItem = new ListItem();
						listItem.setTotal(0);
					}
					initPager(result);
				} else {
					listItem.setCursorStr(result.getCursorStr());
					listItem.getListItem().addAll(result.getListItem());
				}
//				next_cursor = result.getCursorStr();

				setItemTableView(result.getListItem());

				NoticePanel.endLoading();
			}

			@Override
			public void onFailure(Throwable caught) {
				NoticePanel
						.failNotice("Kết nối đến server thất bại, vui lòng kiểm tra lại đường truyền");
			}
		});
	}
	
	void searchItem(String search_string) {
		NoticePanel.onLoading();
		PrettyGal.dataService.searchItems(search_string, new AsyncCallback<ListItem>() {

			@Override
			public void onSuccess(ListItem result) {
				searchButtonPanel.setVisible(false);
				cancelSearchButtonPanel.setVisible(true);
				
				if (listItem == null) {
					if(result != null)
						listItem = result;
					else {
						listItem = new ListItem();
						listItem.setTotal(0);
					}
					initPager(result);
				} else {
					listItem.getListItem().addAll(result.getListItem());
				}
//				next_cursor = result.getCursorStr();

				setItemTableView(result.getListItem());

				NoticePanel.endLoading();
			}

			@Override
			public void onFailure(Throwable caught) {
				NoticePanel.failNotice(PrettyGal.ERROR_MSG);
			}
		});
	}

	void initPager(ListItem items) {
		itemsPager.setPage(items.getTotal(), ListItem.pageSize, new PagerListener() {

			@Override
			public void pageIndex(int index) {
				if (index < listItem.getListItem().size()) {
					List<Item> displayItem;
					if ((index + ListItem.pageSize) <= listItem.getListItem()
							.size())
						displayItem = listItem.getListItem().subList(index,
								index + ListItem.pageSize);
					else
						displayItem = listItem.getListItem().subList(index,
								listItem.getListItem().size());

					setItemTableView(displayItem);
				} else {
					if(listItemFilterBuffer == null && listItemSearchBuffer == null)
						getListItem(listItem.getCursorStr());
					else if (listItemFilterBuffer != null && listItemSearchBuffer == null)
						getListItemInCatalog(listItem.getCursorStr(), catalog_id, catalog_name);
					else if (listItemFilterBuffer == null && listItemSearchBuffer != null)
						searchItem(search_string);
				}
				scrollTable.scrollToTop();
			}
		});
	}
	
	public static void ClearListItem() {
		listItem = null;
	}

	public void AddItem(Item newItem, boolean isUpdate) {
		if(listItem != null) {
			if(!isUpdate) {
				listItem.getListItem().add(0, newItem);
				listItem.setTotal(listItem.getTotal() + 1);
				initPager(listItem);
				if(listItem.getListItem().size() > ListItem.pageSize)
					displayItem = listItem.getListItem().subList(0, ListItem.pageSize);
				else
					displayItem = listItem.getListItem().subList(0, listItem.getListItem().size());
			}
			else {
//				int index = listItem.getListItem().indexOf(newItem);
//				listItem.getListItem().remove(index);
//				listItem.getListItem().add(index, newItem);
				for(Item item: listItem.getListItem()) {
					if(item.getId().equals(newItem.getId())) {
						int index = listItem.getListItem().indexOf(item);
						listItem.getListItem().remove(index);
						listItem.getListItem().add(index, newItem);
					}
				}
				
//				index = this.displayItem.indexOf(newItem);
//				displayItem.remove(index);
//				displayItem.add(index, newItem);
				for(Item item: displayItem) {
					if(item.getId().equals(newItem.getId())) {
						int index = displayItem.indexOf(item);
						displayItem.remove(index);
						displayItem.add(index, newItem);
					}
				}
			}
				
			setItemTableView(displayItem);
		}
	}

	public ItemTable() {
		initWidget(uiBinder.createAndBindUi(this));

		scrollTable.setHeight(Ruler.ItemTable_H + "px");
//		categorieTable.setWidth(Ruler.ItemTable_Categories_W);
		nameColumn.setWidth(Ruler.ItemTableRow_itemname_W + "px");

//		searchTxb.getElement().setAttribute("placeholder", "Search ...");

//		CategorieTable_Row categorie1 = new CategorieTable_Row("General");
//		CategorieTable_Row categorie2 = new CategorieTable_Row("Categorie 1");
//		CategorieTable_Row categorie3 = new CategorieTable_Row("Categorie 2");
//		categorie1.setActive();

//		table.add(categorie1);
//		table.add(categorie2);
//		table.add(categorie3);

//		getListItem("");
//		itemTable.setVisible(false);
//		itemImportTable.setVisible(true);
//		itemImportTable.add(new ItemImportTable());
		
		searchTxb.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if(searchTxb.getText().isEmpty()) {
					searchTxb.setWidth("0px");
					searchTxb.setVisible(false);
				}
				else {
					if(!searchTxb.getText().equals(search_string)) {
						if(listItemSearchBuffer == null)
							listItemSearchBuffer = new ListItem(listItem.getListItem(), listItem.getCursorStr(), listItem.getTotal());
						listItem = null;
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
	}
	
	public void cancelImportItem() {
		if(listItemFilterBuffer == null)
			itemTableTitle.setText("Danh Sách Sản Phấm");
		else
			itemTableTitle.setText(catalog_name);
		
		tableHeader.setVisible(true);
		itemTable.setVisible(true);
		itemImportTable.setVisible(false);
		importTableControlBox.setVisible(false);
		itemsPager.setVisible(true);
	}
	
	public void cancelFilter() {
		filterButtonPanel.setVisible(true);
		cancelFilterButtonPanel.setVisible(false);
		
		filterTable.setVisible(false);
		filterTableContent.setHeight("0px");
		itemTableTitle.setText("Danh Sách Sản Phấm");
		
		if(listItemFilterBuffer != null) {
			for(Item item: listItem.getListItem()) {
				if(listItemFilterBuffer.getListItem().contains(item)) {
					int index = listItemFilterBuffer.getListItem().indexOf(item);
					listItemFilterBuffer.getListItem().remove(index);
					listItemFilterBuffer.getListItem().add(index, item);
				}
			}
			
			listItem = new ListItem(listItemFilterBuffer.getListItem(), listItemFilterBuffer.getCursorStr(), listItemFilterBuffer.getTotal());
			listItemFilterBuffer = null;
			initPager(listItem);
			
			int fromIndex = 0;
			int toIndex = fromIndex + ListItem.pageSize;
			if(toIndex >= listItem.getListItem().size())
				toIndex = listItem.getListItem().size();
			setItemTableView(listItem.getListItem().subList(fromIndex, toIndex));
		}
	}
	
	public void cancelSearch() {
		searchTxb.setWidth("0px");
		searchTxb.setVisible(false);
		searchTxb.setText("");
		searchButtonPanel.setVisible(true);
		cancelSearchButtonPanel.setVisible(false);
		search_string = "";
		
		if(listItemSearchBuffer != null) {
			for(Item item: listItem.getListItem()) {
				if(listItemSearchBuffer.getListItem().contains(item)) {
					int index = listItemSearchBuffer.getListItem().indexOf(item);
					listItemSearchBuffer.getListItem().remove(index);
					listItemSearchBuffer.getListItem().add(index, item);
				}
			}
			
			listItem = new ListItem(listItemSearchBuffer.getListItem(), listItemSearchBuffer.getCursorStr(), listItemSearchBuffer.getTotal());
			listItemSearchBuffer = null;
			initPager(listItem);
			
			int fromIndex = 0;
			int toIndex = fromIndex + ListItem.pageSize;
			if(toIndex >= listItem.getListItem().size())
				toIndex = listItem.getListItem().size();
			setItemTableView(listItem.getListItem().subList(fromIndex, toIndex));
		}
	}

	void setItemTableView(List<Item> items) {
		itemTable.clear();
		for (Item item : items) {
			ItemTable_Row itemRow = new ItemTable_Row(new ItemTableRowListener() {
				
				@Override
				public void onDeleteItem(Item item) {
					listItem.getListItem().remove(item);
					listItem.setTotal(listItem.getTotal() - 1);
					itemsPager.updatePage(listItem.getTotal(), ListItem.pageSize);
				}
				
				@Override
				public void onClickItem(Item item) {
					PrettyGal.placeController.goTo(new CreateItemPlace(item.getName(), item));
				}
			});
			itemRow.setItem(item);
			itemTable.add(itemRow);
		}
		
		displayItem = items;
		if(importTable != null)
			importTable.setDisplayItem(items);
	}

	@UiHandler("addItemButton")
	void onAddItemButtonClick(ClickEvent e) {
		PrettyGal.placeController.goTo(new CreateItemPlace("", null));
	}
	
	@UiHandler("importItemButton")
	void onImportItemButtonClick(ClickEvent e) {
		itemTableTitle.setText("Chọn Sản Phẩm Nhập Kho");
		
		tableHeader.setVisible(false);
		itemTable.setVisible(false);
		itemImportTable.setVisible(true);
		itemsPager.setVisible(true);
		importTableControlBox.setVisible(true);
		nextButtonPanel.setVisible(true);
		cancelButtonPanel.setVisible(true);
		backButtonPanel.setVisible(false);
		applyButtonPanel.setVisible(false);
		
		importTable = new ItemImportTable();
		importTable.setDisplayItem(displayItem);
		itemImportTable.clear();
		itemImportTable.add(importTable);
	}
	
	@UiHandler("cancelButton")
	void onCancelImportButtonClick(ClickEvent e) {
		cancelImportItem();
	}
	
	@UiHandler("nextButton")
	void onNextButtonClick(ClickEvent e) {
		if(importTable != null && !importTable.selectedItems.isEmpty()) {
			itemTableTitle.setText("Nhập Số Lượng Sản Phẩm");
			
			itemsPager.setVisible(false);
			importTable.setImportItems();
			
			nextButtonPanel.setVisible(false);
			cancelButtonPanel.setVisible(false);
			backButtonPanel.setVisible(true);
			applyButtonPanel.setVisible(true);
		}
	}
	
	@UiHandler("backButton")
	void onBackButtonClick(ClickEvent e) {
		itemTableTitle.setText("Chọn Sản Phẩm Nhập Kho");
		
		itemsPager.setVisible(true);
		importTable.setDisplayItem(displayItem);
		
		nextButtonPanel.setVisible(true);
		cancelButtonPanel.setVisible(true);
		backButtonPanel.setVisible(false);
		applyButtonPanel.setVisible(false);
	}
	
	@UiHandler("applyButton")
	void onApplyButtonClick(ClickEvent e) {
		if(importTable != null)
			importTable.startImportItems(new ItemImportTable_Listener() {
				
				@Override
				public void onImportSuccess(List<Item> result) {
					for(Item item: result) {
						int index = listItem.getListItem().indexOf(item);
						listItem.getListItem().remove(index);
						listItem.getListItem().add(index, item);
					}
					cancelImportItem();
					setItemTableView(displayItem);
				}
				
				@Override
				public void onImportFail() {}
			});
	}
	
	@UiHandler("filterButton")
	void onFilterButtonClick(ClickEvent e) {
		filterButtonPanel.setVisible(false);
		cancelFilterButtonPanel.setVisible(true);
		
		if(listCatalog == null)
			getListCatalog();
		else
			showFilter(listCatalog);
	}
	
	@UiHandler("cancelFilterButton")
	void onCancelFilterButtonClick(ClickEvent e) {
		cancelFilter();
	}
	
	@UiHandler("searchButton")
	void onSearchButtonClick(ClickEvent e) {
		searchTxb.setVisible(true);
		Timer t = new Timer() {

			@Override
			public void run() {
				searchTxb.setWidth("220px");
				searchTxb.setFocus(true);
			}
			
		};
		t.schedule(100);
	}
	
	@UiHandler("cancelSearchButton")
	void onCancelSearchButtonClick(ClickEvent e) {
		cancelSearch();
	}

}
