package com.tranan.webstorage.client_admin.dialog;

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
import com.tranan.webstorage.shared.Customer;
import com.tranan.webstorage.shared.ListCustomer;

public class ListCustomerDialog extends DialogBox {

	private static ListCustomerDialogUiBinder uiBinder = GWT
			.create(ListCustomerDialogUiBinder.class);

	interface ListCustomerDialogUiBinder extends
			UiBinder<Widget, ListCustomerDialog> {
	}
	
	@UiField
	HTMLPanel dialog;
	@UiField
	Pager pager;
	@UiField
	ScrollPanel scroll;
	@UiField
	Image loadImg;
	@UiField
	HTMLPanel failNoticePanel;
	@UiField
	HTMLPanel itemTable;
	@UiField 
	TextBox searchTxb;
	@UiField
	HTMLPanel searchButtonPanel;
	@UiField
	HTMLPanel cancelSearchButtonPanel;
	
	float opacity;
	Timer t;
	
	private ListCustomer listCustomer;
	private String search_string;
	private ListCustomer listItemSearch;
	private List<Customer> displayItem;
	
	private CheckBox selectedCB;
	private Customer selectedCustomer;
	
	private ListCustomerDialog_Listener listener;
	
	public interface ListCustomerDialog_Listener {
		void onSelectedCustomer(Customer selectedCustomer);
	}
	
	void getCustomers(String cursor) {
		onLoading();
		PrettyGal.dataService.getCustomers(cursor, new AsyncCallback<ListCustomer>() {
			
			@Override
			public void onSuccess(ListCustomer result) {
				if (listCustomer == null) {
					if (result != null)
						listCustomer = result;
					else {
						listCustomer = new ListCustomer();
						listCustomer.setTotal(0);
					}
					initPager(result);
				} else {
					listCustomer.setCursorStr(result.getCursorStr());
					listCustomer.getListCustomer().addAll(result.getListCustomer());
				}
			
				setCustomerTableView(result.getListCustomer());
				endLoading();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failLoading();
			}
		});
	}
	
	void searchItem(String search_string) {
		onLoading();
		PrettyGal.dataService.searchCustomer(search_string, new AsyncCallback<ListCustomer>() {

			@Override
			public void onSuccess(ListCustomer result) {
				searchButtonPanel.setVisible(false);
				cancelSearchButtonPanel.setVisible(true);
				
				if(result != null)
					listItemSearch = result;
				else {
					listItemSearch = new ListCustomer();
					listItemSearch.setTotal(0);
				}	
				
				initPager(result);
				setCustomerTableView(result.getListCustomer());
				endLoading();
			}

			@Override
			public void onFailure(Throwable caught) {
				failLoading();
			}
		});
	}
	
	void initPager(ListCustomer customer) {
		pager.setPage(customer.getTotal(), ListCustomer.pageSize, new PagerListener() {

			@Override
			public void pageIndex(int index) {
				if(listItemSearch == null) {
					if (index < listCustomer.getListCustomer().size()) {
						List<Customer> displayItem;
						if ((index + ListCustomer.pageSize) <= listCustomer.getListCustomer().size())
							displayItem = listCustomer.getListCustomer().subList(index, index + ListCustomer.pageSize);
						else
							displayItem = listCustomer.getListCustomer().subList(index, listCustomer.getListCustomer().size());
	
						setCustomerTableView(displayItem);
					} else {
						getCustomers(listCustomer.getCursorStr());					
					}
				} 
				else {
					List<Customer> displayItem;
					if ((index + ListCustomer.pageSize) <= listItemSearch.getListCustomer().size())
						displayItem = listItemSearch.getListCustomer().subList(index,
								index + ListCustomer.pageSize);
					else
						displayItem = listItemSearch.getListCustomer().subList(index,
								listItemSearch.getListCustomer().size());

					setCustomerTableView(displayItem);
				}
				scroll.scrollToTop();
			}
		});
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
	
	public void cancelSearch() {
		searchTxb.setWidth("0px");
		searchTxb.setVisible(false);
		searchTxb.setText("");
		searchButtonPanel.setVisible(true);
		searchButtonPanel.removeStyleName("ListItemDialg_s7focus");
		cancelSearchButtonPanel.setVisible(false);
		search_string = "";
		listItemSearch = null;
		
		initPager(listCustomer);
			
		int fromIndex = 0;
		int toIndex = fromIndex + ListCustomer.pageSize;
		if(toIndex >= listCustomer.getListCustomer().size())
			toIndex = listCustomer.getListCustomer().size();
		setCustomerTableView(listCustomer.getListCustomer().subList(fromIndex, toIndex));
	}

	public ListCustomerDialog(ListCustomerDialog_Listener listener) {
		setWidget(uiBinder.createAndBindUi(this));
		
		setGlassEnabled(true);
		setAutoHideEnabled(true);
		setAnimationEnabled(false);
		setStyleName("ConfirmDialog_s1");
		
		this.listener = listener;
		
		this.addAttachHandler(new Handler() {

			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached())
					faddingShowDialog();
			}
		});
		
		String height = Window.getClientHeight() / 2 + "px";
		scroll.getElement().setAttribute("style", "overflow-y:scroll; height:" + height);
		
		searchTxb.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if(searchTxb.getText().isEmpty()) {
					searchTxb.getElement().setAttribute("style", "display: none");
					searchButtonPanel.removeStyleName("ListCustomerDialg_s7focus");
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
		
		getCustomers("");
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
	
	void setCustomerTableView(List<Customer> customers) {
		itemTable.clear();
		for (final Customer customer : customers) {
			HTMLPanel panel1 = new HTMLPanel("");
			panel1.addStyleName("ListCustomerDialog_table");
			
			HTMLPanel panel2 = new HTMLPanel("");
			panel2.addStyleName("ListCustomerDialog_table_col1");
			final CheckBox cb = new CheckBox();
			panel2.add(cb);
			if(selectedCustomer != null && selectedCustomer.getPhone().equals(customer.getPhone())) {
				cb.setValue(true);
				selectedCB = cb;
			}
			
			Label lbName = new Label(customer.getName());
			lbName.setStyleName("ListCustomerDialog_table_col3");
	
			Label lbAddress = new Label(customer.getAddress());
			lbAddress.setStyleName("ListCustomerDialog_table_col4");
			
			Label lbPhone = new Label(PrettyGal.phoneFormat(customer.getPhone()));
			lbPhone.setStyleName("ListCustomerDialog_table_col5");
			
			Label lbEmail = new Label(customer.getEmail());
			lbEmail.setStyleName("ListCustomerDialog_table_col5");
			
			panel1.add(panel2);
			panel1.add(lbName);
			panel1.add(lbAddress);			
			panel1.add(lbPhone);
			panel1.add(lbEmail);
			itemTable.add(panel1);
			
			cb.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(cb.getValue()) {
						if(selectedCB != null)
							selectedCB.setValue(false);
						selectedCB = cb;
						selectedCustomer = customer;
					}
					else {
						selectedCB = null;
						selectedCustomer = null;
					}
				}
			});
		}
		
		displayItem = customers;
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent e) {
		listener.onSelectedCustomer(selectedCustomer);
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
				searchButtonPanel.addStyleName("ListCustomerDialg_s7focus");
			}
			
		};
		t.schedule(100);
	}
	
	@UiHandler("cancelSearchButton")
	void onCancelSearchButtonClick(ClickEvent e) {
		cancelSearch();
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
}
