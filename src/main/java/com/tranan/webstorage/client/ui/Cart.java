package com.tranan.webstorage.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client.PrettyGal;
import com.tranan.webstorage.client.sub_ui.CartItem;
import com.tranan.webstorage.client.sub_ui.CartItem.CartItemListener;
import com.tranan.webstorage.client.sub_ui.NoticeDialog;
import com.tranan.webstorage.shared.Customer;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Item.Type;
import com.tranan.webstorage.shared.Order;

public class Cart extends Composite {

	private static CartUiBinder uiBinder = GWT.create(CartUiBinder.class);

	interface CartUiBinder extends UiBinder<Widget, Cart> {
	}
	
	@UiField HTMLPanel cart_items; 
	@UiField Label cartTitle;
	@UiField Label orderTotalLb;
	@UiField HTMLPanel cartTab1;
	@UiField HTMLPanel cartTab2;
	@UiField SuggestBox nameField;
	@UiField SuggestBox addressField;
	@UiField SuggestBox phoneField;
	@UiField SuggestBox emailField;
	@UiField Label nameFieldTitle;
	@UiField Label addressFieldTitle;
	@UiField Label phoneFieldTitle;
	@UiField Label emailFieldTitle;
	
	private Long order_total = 0L;
	private int total_items;
	
	private Map<Long, List<String>> item_type = new HashMap<Long, List<String>>();
	private List<Item> order_items = new ArrayList<Item>();
	
	private void getOrderItems() {
		order_items.clear();
		cart_items.clear();
		item_type.clear();
		order_total = 0L;
		orderTotalLb.setText("0");
		
		String orderItemsString = Cookies.getCookie(PrettyGal.ORDER_ITEMS);
		if(orderItemsString == null)
			orderItemsString = PrettyGal.COOKIE_DELIMITER;
		String item_count[] = orderItemsString.split(PrettyGal.COOKIE_DELIMITER);
		
		total_items = item_count.length - 1;
		if(item_count.length > 0)
			cartTitle.setText("Giỏ Hàng ("+ total_items + ")");
		else
			cartTitle.setText("Giỏ Hàng (0)");
		
		if(item_count.length - 1 > 0) {		
			List<Long> orderItemIds = new ArrayList<Long>();
			for(int i = 1; i < item_count.length; i++) {				
				String itemCompact[] = item_count[i].split(Item.ITEM_COMPACT_LINK);
				
				if(!orderItemIds.contains(Long.valueOf(itemCompact[0])))
					orderItemIds.add( Long.valueOf(itemCompact[0]) );
				
				if(item_type.get( Long.valueOf(itemCompact[0]) ) == null) {
					List<String> type = new ArrayList<String>();
					type.add(itemCompact[1]);
					item_type.put(Long.valueOf(itemCompact[0]), type);
				}
				else {
					item_type.get(Long.valueOf(itemCompact[0])).add(itemCompact[1]);
				}			
			}
			
			PrettyGal.dataService.getItemByListIds(orderItemIds, new AsyncCallback<List<Item>>() {
				
				@Override
				public void onSuccess(List<Item> result) {
					for(Item i: result) {
						i.getType().clear();
						for(String type: item_type.get(i.getId())) {
							Item ii = new Item(i);
							Type t = new Type();
							t.setName(type);
							ii.getType().add(t);
							order_items.add(ii);
						}
					}
					
					setOrderItemsToView(order_items);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}
			});
		}				
	}
	
	private void setOrderItemsToView(List<Item> items) {
		cart_items.clear();
		for(final Item item: items) {
			if(item.getSale() == 0) {
				order_total = order_total + (item.getPrice() * 1);
			}
			else {
				order_total = order_total + (item.getSale_price() * 1);
			}
			
			final int index = order_items.indexOf(item);
			order_items.get(index).getType().get(0).setQuantity(1);
			
			final CartItem cartItem = new CartItem(item);			
			cartItem.setListener(new CartItemListener() {
				
				@Override
				public void onItemQuantityChange(Long old_price, Long total_price, int quantity) {
					order_total = order_total - old_price + total_price;
					orderTotalLb.setText(PrettyGal.integerToPriceString(order_total));
					order_items.get(index).getType().get(0).setQuantity(quantity);
				}
				
				@Override
				public void onItemDelete(Long total_price) {
					order_total = order_total - total_price;
					orderTotalLb.setText(PrettyGal.integerToPriceString(order_total));
					cart_items.remove(cartItem);
					order_items.remove(index);
					
					long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login - 2 weeks
					Date expires = new Date(System.currentTimeMillis() + DURATION);
					String order_items = Cookies.getCookie(PrettyGal.ORDER_ITEMS);					
					String new_order_item = order_items.replace( (PrettyGal.COOKIE_DELIMITER + item.getId().toString()) + Item.ITEM_COMPACT_LINK + item.getType().get(0).getName(), "");					
					Cookies.setCookie(PrettyGal.ORDER_ITEMS, new_order_item, expires, null, "/", false);
					total_items--;
					cartTitle.setText("Giỏ Hàng ("+ total_items+ ")");
					PrettyGal.header.setOrderItems(String.valueOf(total_items));
				}
			});
			
			cart_items.add(cartItem);
		}
		orderTotalLb.setText(PrettyGal.integerToPriceString(order_total));
	}
	
	private void goToTab2() {
		if(order_items.isEmpty()) {
			final NoticeDialog dialog = new NoticeDialog("Không Có Sản Phầm Nào Trong Giỏ Hàng."); 						
			Timer t = new Timer() {

				@Override
				public void run() {
					dialog.center();			
				}};
			t.schedule(50);
		}
		else {
			cartTab1.setVisible(false);
			cartTab2.setVisible(true);
			Window.scrollTo(0, 0);
		}
	}
	
	private boolean validateField() {
		boolean validate = true;
		nameFieldTitle.removeStyleName("Cart_MissingField");
		addressFieldTitle.removeStyleName("Cart_MissingField");
		phoneFieldTitle.removeStyleName("Cart_MissingField");
		
		if(nameField.getText().isEmpty()) {
			nameFieldTitle.addStyleName("Cart_MissingField");
			validate = false;
		}
		if(addressField.getText().isEmpty()) {
			addressFieldTitle.addStyleName("Cart_MissingField");
			validate = false;
		}
		if(phoneField.getText().isEmpty()) {
			phoneFieldTitle.addStyleName("Cart_MissingField");
			validate = false;
		}
		
		return validate;
	}
	
	private void resetTab2Field() {
		nameFieldTitle.removeStyleName("Cart_MissingField");
		addressFieldTitle.removeStyleName("Cart_MissingField");
		phoneFieldTitle.removeStyleName("Cart_MissingField");
		
		nameField.setText("");
		addressField.setText("");
		phoneField.setText("");
		emailField.setText("");
	}
	
	private void saveUserInfo() {
		String customerName = Cookies.getCookie(PrettyGal.CUSTOMER_NAME);
		if(customerName == null)
			customerName = PrettyGal.COOKIE_DELIMITER;
		String customerAddress = Cookies.getCookie(PrettyGal.CUSTOMER_ADDRESS);
		if(customerAddress == null)
			customerAddress = PrettyGal.COOKIE_DELIMITER;
		String customerPhone = Cookies.getCookie(PrettyGal.CUSTOMER_PHONE);
		if(customerPhone == null)
			customerPhone = PrettyGal.COOKIE_DELIMITER;
		String customerEmail = Cookies.getCookie(PrettyGal.CUSTOMER_EMAIL);
		if(customerEmail == null)
			customerEmail = PrettyGal.COOKIE_DELIMITER;
		
		long DURATION = 1000 * 60 * 60 * 24 * 1000;
		Date expires = new Date(System.currentTimeMillis() + DURATION);
		
		if(!customerName.contains(nameField.getText())) {
			customerName = customerName + PrettyGal.COOKIE_DELIMITER + nameField.getText();
			Cookies.setCookie(PrettyGal.CUSTOMER_NAME, customerName, expires, null, "/", false);
		}
		if(!customerAddress.contains(addressField.getText())) {
			customerAddress = customerAddress + PrettyGal.COOKIE_DELIMITER + addressField.getText();
			Cookies.setCookie(PrettyGal.CUSTOMER_ADDRESS, customerAddress, expires, null, "/", false);
		}
		if(!customerPhone.contains(phoneField.getText())) {
			customerPhone = customerPhone + PrettyGal.COOKIE_DELIMITER + phoneField.getText();
			Cookies.setCookie(PrettyGal.CUSTOMER_PHONE, customerPhone, expires, null, "/", false);
		}
		if(!customerEmail.contains(emailField.getText())) {
			customerEmail = customerEmail + PrettyGal.COOKIE_DELIMITER + emailField.getText();
			Cookies.setCookie(PrettyGal.CUSTOMER_EMAIL, customerEmail, expires, null, "/", false);
		}
	}
	
	private void getUserInfoSuggest() {
		MultiWordSuggestOracle oracle = (MultiWordSuggestOracle)nameField.getSuggestOracle(); 
		List<String> list = new ArrayList<String>();
		String customerName = Cookies.getCookie(PrettyGal.CUSTOMER_NAME);
		if(customerName == null)
			customerName = PrettyGal.COOKIE_DELIMITER;
		String cutomerNames[] = customerName.split(PrettyGal.COOKIE_DELIMITER);
		if(cutomerNames.length > 1) {
			for(int i=1; i<cutomerNames.length; i++) {
				oracle.add(cutomerNames[i]);
		    	list.add(cutomerNames[i]);
			}
		}
	    oracle.setDefaultSuggestionsFromText(list);
	    
	    MultiWordSuggestOracle oracle2 = (MultiWordSuggestOracle)addressField.getSuggestOracle(); 
		List<String> list2 = new ArrayList<String>();
		String customerAddress = Cookies.getCookie(PrettyGal.CUSTOMER_ADDRESS);
		if(customerAddress == null)
			customerAddress = PrettyGal.COOKIE_DELIMITER;
		String customerAddresses[] = customerAddress.split(PrettyGal.COOKIE_DELIMITER);
		if(customerAddresses.length > 1) {
			for(int i=1; i<customerAddresses.length; i++) {
				oracle2.add(customerAddresses[i]);
		    	list2.add(customerAddresses[i]);
			}
		}
	    oracle2.setDefaultSuggestionsFromText(list2);
	    
	    MultiWordSuggestOracle oracle3 = (MultiWordSuggestOracle)phoneField.getSuggestOracle(); 
		List<String> list3 = new ArrayList<String>();
		String customerPhone = Cookies.getCookie(PrettyGal.CUSTOMER_PHONE);
		if(customerPhone == null)
			customerPhone = PrettyGal.COOKIE_DELIMITER;
		String customerPhones[] = customerPhone.split(PrettyGal.COOKIE_DELIMITER);
		if(customerPhones.length > 1) {
			for(int i=1; i<customerPhones.length; i++) {
				oracle3.add(customerPhones[i]);
		    	list3.add(customerPhones[i]);
			}
		}
	    oracle3.setDefaultSuggestionsFromText(list3);
	    
	    MultiWordSuggestOracle oracle4 = (MultiWordSuggestOracle)emailField.getSuggestOracle(); 
		List<String> list4 = new ArrayList<String>();
		String customerEmail = Cookies.getCookie(PrettyGal.CUSTOMER_EMAIL);
		if(customerEmail == null)
			customerEmail = PrettyGal.COOKIE_DELIMITER;
		String customerEmails[] = customerEmail.split(PrettyGal.COOKIE_DELIMITER);
		if(customerEmails.length > 1) {
			for(int i=1; i<customerEmails.length; i++) {
				oracle4.add(customerEmails[i]);
		    	list4.add(customerEmails[i]);
			}
		}
	    oracle4.setDefaultSuggestionsFromText(list4);
	}
	
	private void createOrder() {
		final Order order = new Order();		
		
		//Create customer
		Customer customer = new Customer();
		customer.setName(nameField.getText());		
		customer.setAddress(addressField.getText());			
		customer.setEmail(emailField.getText());
		try {
			customer.setPhone(phoneField.getText().replaceAll("-", "").replaceAll(" ", ""));
		} catch (Exception exception) {
			customer.setPhone("1");
		}
		
		//Create order detail
		order.setCustomer(customer);
		order.setSale_channel(PrettyGal.WEB_SALE_CHANNEL);
		
		for(Item item: order_items) {
			if(item.getSale() != 0)
				item.setPrice(item.getSale_price());
		}
		order.setOrder_items(order_items);
		
		PrettyGal.dataService.submitOrder(order, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {				
				Cookies.setCookie(PrettyGal.ORDER_ITEMS, PrettyGal.COOKIE_DELIMITER, new Date(), null, "/", false);
				PrettyGal.showHeaderItemOrder();
				
				final NoticeDialog dialog = new NoticeDialog("Bạn đã đặt hàng thành công, Chúng tôi sẽ liên hệ với bạn để xác nhận đơn hàng."); 						
				Timer t = new Timer() {

					@Override
					public void run() {
						dialog.center();			
					}};
				t.schedule(50);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	public Cart() {
		initWidget(uiBinder.createAndBindUi(this));		
		
		phoneField.getTextBox().addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				phoneField.setText(PrettyGal.phoneFormat(phoneField.getText()));
			}
		});
		
		phoneField.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char ch = event.getCharCode();
				if(ch != '0' && ch != '1' && ch != '2' && ch != '3' && ch != '4' && ch != '5'
						&& ch != '6' && ch != '7' && ch != '8' && ch != '9') {
					phoneField.getTextBox().cancelKey();
				}
			}
		});
		
		nameField.getTextBox().addFocusHandler(new FocusHandler() {
		    @Override
		    public void onFocus(FocusEvent event) {
		        if(nameField.getTextBox().getText().trim().length()==0) {
		        	nameField.showSuggestionList();
		        }
		    }
		});
		
		addressField.getTextBox().addFocusHandler(new FocusHandler() {
		    @Override
		    public void onFocus(FocusEvent event) {
		        if(addressField.getTextBox().getText().trim().length()==0) {
		        	addressField.showSuggestionList();
		        }
		    }
		});
		
		phoneField.getTextBox().addFocusHandler(new FocusHandler() {
		    @Override
		    public void onFocus(FocusEvent event) {
		        if(phoneField.getTextBox().getText().trim().length()==0) {
		        	phoneField.showSuggestionList();
		        }
		    }
		});
		
		emailField.getTextBox().addFocusHandler(new FocusHandler() {
		    @Override
		    public void onFocus(FocusEvent event) {
		        if(emailField.getTextBox().getText().trim().length()==0) {
		        	emailField.showSuggestionList();
		        }
		    }
		});
		
		this.addAttachHandler(new Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				getOrderItems();
				cartTab1.setVisible(true);
				cartTab2.setVisible(false);
				resetTab2Field();
				getUserInfoSuggest();
				Window.scrollTo(0, 0);
			}
		});
	}
	
	@UiHandler("nextInfoBtn1")
	void onNextInfoBtn1Click(ClickEvent e) {
		goToTab2();
	}
	
	@UiHandler("nextInfoBtn2")
	void onNextInfoBtn2Click(ClickEvent e) {
		goToTab2();
	}
	
	@UiHandler("backBtn")
	void onBackBtnClick(ClickEvent e) {
		cartTab1.setVisible(true);
		cartTab2.setVisible(false);
	}
	
	@UiHandler("finishOrderBtn")
	void onFinishOrderBtnClick(ClickEvent e) {
		if(validateField()) {
			saveUserInfo();
			createOrder();
		}
	}

}
