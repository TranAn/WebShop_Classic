package com.tranan.webstorage.client_admin.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.Ruler;
import com.tranan.webstorage.client_admin.dialog.ListCustomerDialog;
import com.tranan.webstorage.client_admin.dialog.ListCustomerDialog.ListCustomerDialog_Listener;
import com.tranan.webstorage.client_admin.dialog.ListItemDialog;
import com.tranan.webstorage.client_admin.dialog.ListItemDialog.ListItemDialog_Listener;
import com.tranan.webstorage.client_admin.place.OrderPlace;
import com.tranan.webstorage.client_admin.sub_ui.NoticePanel;
import com.tranan.webstorage.shared.Customer;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Order;
import com.tranan.webstorage.shared.OrderChannel;

public class CreateOrder extends Composite {

	private static CreateOrderUiBinder uiBinder = GWT
			.create(CreateOrderUiBinder.class);

	interface CreateOrderUiBinder extends UiBinder<Widget, CreateOrder> {
	}
	
	@UiField ScrollPanel scroll;
	@UiField HTMLPanel itemTable;
	@UiField Label txbSum;
	@UiField DateBox createDateBox;
	@UiField SuggestBox saleChannelBox;
	@UiField CheckBox pendingBox;
	@UiField CheckBox deliveryBox;
	@UiField CheckBox finishBox;
	@UiField Label pendingBoxText;
	@UiField Label deliveryBoxText;
	@UiField Label finishBoxText;
	@UiField TextBox nameCustomer;
	@UiField TextBox addressCustomer;
	@UiField TextBox phoneCustomer;
	@UiField TextBox emailCustomer;
	@UiField HTMLPanel finishDatePanel;
	@UiField DateBox finishDateBox;
	@UiField HTMLPanel newCustomerPanel;
	
	private Order order;
	private int old_order_status = -1;
	private Customer customer;
	private List<Item> orderItems = new ArrayList<Item>();
	private int order_status;
	
	void getChannels() {
		if(OrderTable.channels == null) {
			NoticePanel.onLoading();
			PrettyGal.dataService.getChannels(new AsyncCallback<List<OrderChannel>>() {
				
				@Override
				public void onSuccess(List<OrderChannel> result) {
					if(result != null)
						OrderTable.channels = result;
					else
						OrderTable.channels = new ArrayList<OrderChannel>();
					
					NoticePanel.endLoading();
					
					MultiWordSuggestOracle oracle = (MultiWordSuggestOracle)saleChannelBox.getSuggestOracle(); 
					List<String> list = new ArrayList<String>();
				    for(OrderChannel channel: OrderTable.channels)	{
				    	if(channel.getName().equals(OrderChannel.DEFAULT_CHANNEL)) {
//				    		oracle.add("Kênh Khác");
//					    	list.add("Kênh Khác");
				    	}
				    	else {
					    	oracle.add(channel.getName());
					    	list.add(channel.getName());
				    	}
				    }
				    oracle.setDefaultSuggestionsFromText(list);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					NoticePanel.endLoading();
				}
			});
		}
		else {
			MultiWordSuggestOracle oracle = (MultiWordSuggestOracle)saleChannelBox.getSuggestOracle(); 
			List<String> list = new ArrayList<String>();
		    for(OrderChannel channel: OrderTable.channels)	{
		    	if(channel.getName().equals(OrderChannel.DEFAULT_CHANNEL)) {
//		    		oracle.add("Kênh Khác");
//			    	list.add("Kênh Khác");
		    	}
		    	else {
			    	oracle.add(channel.getName());
			    	list.add(channel.getName());
		    	}
		    }
		    oracle.setDefaultSuggestionsFromText(list);
		}
	}

	public CreateOrder() {
		initWidget(uiBinder.createAndBindUi(this));
		
		scroll.setHeight(Ruler.ItemTable_H + "px");
		
		createDateBox.setFormat(new DateBox.DefaultFormat 
				 (DateTimeFormat.getFormat("dd - MM - yyyy"))); 
		finishDateBox.setFormat(new DateBox.DefaultFormat 
				 (DateTimeFormat.getFormat("dd - MM - yyyy"))); 
		
		pendingBox.setValue(true);
		pendingBoxText.addStyleName("CreateOrder_pending");
		order_status = 0;
		
		saleChannelBox.getTextBox().addFocusHandler(new FocusHandler() {
		    @Override
		    public void onFocus(FocusEvent event) {
		        if(saleChannelBox.getTextBox().getText().trim().length()==0) {
		        	saleChannelBox.showSuggestionList();
		        }
		    }
		});
		
		phoneCustomer.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				phoneCustomer.setText(PrettyGal.phoneFormat(phoneCustomer.getText()));
			}
		});
		
		phoneCustomer.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char ch = event.getCharCode();
				if(ch != '0' && ch != '1' && ch != '2' && ch != '3' && ch != '4' && ch != '5'
						&& ch != '6' && ch != '7' && ch != '8' && ch != '9') {
					phoneCustomer.cancelKey();
				}
			}
		});
		
		getChannels();
	}
	
	public void setOrder(Order order) {
		this.order = order;
		this.customer = order.getCustomer();
		
		if(order.getCustomer() != null) {
			nameCustomer.setText(order.getCustomer().getName());
			addressCustomer.setText(order.getCustomer().getAddress());
			phoneCustomer.setText(PrettyGal.phoneFormat(order.getCustomer().getPhone()));
			emailCustomer.setText(order.getCustomer().getEmail());
		}
		
		for(Item i: order.getOrder_items()) {
			orderItems.add(new Item(i));
		}
		addSelectedItemToView(orderItems);
		
		createDateBox.setValue(order.getCreate_date());
		saleChannelBox.setText(order.getSale_channel());
		
		if(order.getStatus() == Order.PENDING) {
			ClearStatus();
			pendingBox.setValue(true);
			pendingBoxText.addStyleName("CreateOrder_pending");
			order_status = 0;
			finishDatePanel.setVisible(false);
		}
		else if (order.getStatus() == Order.DELIVERY) {
			ClearStatus();
			deliveryBox.setValue(true);
			deliveryBoxText.addStyleName("CreateOrder_delivery");
			order_status = 1;
			finishDatePanel.setVisible(false);
		}
		else if (order.getStatus() == Order.FINISH) {
			ClearStatus();
			finishBox.setValue(true);
			finishBoxText.addStyleName("CreateOrder_finish");
			order_status = 2;
			finishDatePanel.setVisible(true);
			finishDateBox.setValue(order.getFinish_date());
		}
	}
	
	public boolean isItemChange() {
		return false;
	}
	
	void ClearStatus() {
		pendingBox.setValue(false);
		deliveryBox.setValue(false);
		finishBox.setValue(false);
		
		pendingBoxText.removeStyleName("CreateOrder_pending");
		deliveryBoxText.removeStyleName("CreateOrder_delivery");
		finishBoxText.removeStyleName("CreateOrder_finish");
	}
	
	void addSelectedItemToView(List<Item> selectedItems) {
		for(final Item item: selectedItems) {
			final HTMLPanel panel1 = new HTMLPanel("");
			panel1.setStyleName("CreateOrder_s2");
			
			HTMLPanel panel2 = new HTMLPanel("");
			panel2.setStyleName("CreateOrder_s3");
			Image img = new Image();
			img.setUrl(item.getAvatar_url());
			img.setSize("50px", "50px");
			panel2.add(img);
			
			Label lb1 = new Label();
			lb1.setWidth("70%");
			lb1.setStyleName("CreateOrder_s4_left");
			if(item.getType().get(0).getName().equals(Item.DEFAULT_TYPE))
				lb1.setText(item.getName());
			else
				lb1.setText(item.getName()+ " ("+ item.getType().get(0).getName()+ ")");
			if(item.getSale() != 0)
				lb1.setText(lb1.getText()+ " - khuyến mại "+ item.getSale()+ "%");
			
			HTMLPanel panel3 = new HTMLPanel("");
			panel3.setStyleName("CreateOrder_s4");
			final LongBox lbx1 = new LongBox();
			PrettyGal.setPriceLongBox(lbx1);
			lbx1.setText(PrettyGal.integerToPriceString(item.getPrice()));
			Anchor anchor1 = new Anchor();
			anchor1.setStyleName("anchor");
			panel3.add(anchor1);
			panel3.add(lbx1);
			anchor1.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					lbx1.setFocus(true);
				}
			});
			
			HTMLPanel panel4 = new HTMLPanel("");
			panel4.setStyleName("CreateOrder_s5");
			final IntegerBox intbx1 = new IntegerBox();
			intbx1.setValue(item.getType().get(0).getQuantity());
			intbx1.getElement().setAttribute("type", "number");
			intbx1.getElement().setAttribute("min", "0");
			Anchor anchor2 = new Anchor();
			anchor2.setStyleName("anchor");
			panel4.add(anchor2);
			panel4.add(intbx1);
			anchor2.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					intbx1.setFocus(true);
				}
			});
			
			final Label lbSum = new Label();
			lbSum.setText(PrettyGal.integerToPriceString(item.getPrice() * item.getType().get(0).getQuantity()));
			lbSum.setStyleName("CreateOrder_s4");
			
			HTMLPanel panel5 = new HTMLPanel("<i class='material-icons'>&#xE872;</i>");
			panel5.setStyleName("CreateOrder_s9");
			Anchor deleteItem = new Anchor();
			deleteItem.setStyleName("anchor");
			panel5.add(deleteItem);
			
			lbx1.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					Long price = Long.valueOf(lbx1.getText().replaceAll("[.]", ""));
					lbx1.setText(PrettyGal.integerToPriceString(price));
					lbSum.setText(PrettyGal.integerToPriceString(price * intbx1.getValue()));
					
					item.setPrice(price);
					CheckSum();
				}
			});
			
			lbx1.addKeyPressHandler(new KeyPressHandler()
            {
                @Override
                public void onKeyPress(KeyPressEvent event_)
                {
                    boolean enterPressed = KeyCodes.KEY_ENTER == event_
                            .getNativeEvent().getKeyCode();
                    if (enterPressed)
                    {
                    	lbx1.setFocus(false);
                    }
                }
            });
			
			intbx1.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					Long price = Long.valueOf(lbx1.getText().replaceAll("[.]", ""));
					lbSum.setText(PrettyGal.integerToPriceString(price * intbx1.getValue()));
					
					item.getType().get(0).setQuantity(intbx1.getValue());
					CheckSum();
				}
			});
			
			deleteItem.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					itemTable.remove(panel1);
					orderItems.remove(item);
					CheckSum();
				}
			});
			
			panel1.add(panel2);
			panel1.add(lb1);
			panel1.add(panel3);
			panel1.add(panel4);
			panel1.add(lbSum);
			panel1.add(panel5);
			itemTable.add(panel1);
		}
		CheckSum();
	}
	
	void CheckSum() {
		Long sum = 0L;
		for(Item item: orderItems) {
			sum = sum + item.getType().get(0).getQuantity() * item.getPrice();
		}
		txbSum.setText(PrettyGal.integerToPriceString(sum));
	}
	
	@UiHandler("addItemButton") 
	void onAddItemButtonClick(ClickEvent e) {
		final ListItemDialog listItemDialog = new ListItemDialog(new ListItemDialog_Listener() {
			
			@Override
			public void onSelectedItem(List<Item> selectedItems) {
				for(Item item: selectedItems) {
					item.getType().get(0).setQuantity(0);
					if(item.getSale() != 0)
						item.setPrice(item.getSale_price());
					orderItems.add(item);
				}
				addSelectedItemToView(selectedItems);
			}
		}, true);
		Timer t = new Timer() {

			@Override
			public void run() {
				listItemDialog.center();
			}};
		t.schedule(50);
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent e) {
		final Order order = new Order();
		if(this.order != null) {
			order.setId(this.order.getId());
			old_order_status = this.order.getStatus();
		}
		
		//Create customer
		Customer customer = new Customer();
		if(this.customer != null)
			customer.setPhone(this.customer.getPhone());
		customer.setName(nameCustomer.getText());
		customer.setAddress(addressCustomer.getText());
		try {
			customer.setPhone(phoneCustomer.getText().replaceAll("-", "").replaceAll(" ", ""));
		} catch (Exception exception) {
			customer.setPhone("1");
		}
		customer.setEmail(emailCustomer.getText());
		//Create order detail
		order.setCustomer(customer);
		order.setCreate_date(createDateBox.getValue());
		order.setSale_channel(saleChannelBox.getText());
		order.setStatus(order_status);
		order.setOrder_items(orderItems);
		if(order.getStatus() == Order.FINISH)
			order.setFinish_date(finishDateBox.getValue());
		
		NoticePanel.onLoading();
		PrettyGal.dataService.createOrder(order, new AsyncCallback<Order>() {
			
			@Override
			public void onSuccess(Order result) {
				if(order.getId() == null) {
					NoticePanel.successNotice("Tạo đơn hàng thành công");
					
					if(order.getStatus() == Order.DELIVERY || order.getStatus() == Order.FINISH)
						ItemTable.ClearListItem();
				}
				else {
					NoticePanel.successNotice("Thay đổi đơn hàng thành công");
					
					if(old_order_status != -1 && order.getStatus() != old_order_status) {
						if(old_order_status == Order.PENDING && (order.getStatus() == Order.DELIVERY || order.getStatus() == Order.FINISH))
							ItemTable.ClearListItem();
						else if((old_order_status == Order.DELIVERY || old_order_status == Order.FINISH) && order.getStatus() == Order.PENDING)
							ItemTable.ClearListItem();
					}
				}
				
				OrderChannel channel = new OrderChannel(result.getSale_channel());
				if(OrderTable.channels != null && !OrderTable.channels.contains(channel))
					OrderTable.channels.add(channel);
					
				PrettyGal.UIC.getOrderTable().AddItem(result);
				PrettyGal.placeController.goTo(new OrderPlace());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				NoticePanel.failNotice(PrettyGal.ERROR_MSG);
			}
		});
	}
	
	@UiHandler("pendingBoxAnchor")
	void onPendingBoxAnchorClick(ClickEvent e) {
		ClearStatus();
		pendingBox.setValue(true);
		pendingBoxText.addStyleName("CreateOrder_pending");
		order_status = 0;
		finishDatePanel.setVisible(false);
	}
	
	@UiHandler("deliveryBoxAnchor")
	void onDeliveryBoxAnchorClick(ClickEvent e) {
		ClearStatus();
		deliveryBox.setValue(true);
		deliveryBoxText.addStyleName("CreateOrder_delivery");
		order_status = 1;
		finishDatePanel.setVisible(false);
	}
	
	@UiHandler("finishBoxAnchor")
	void onFinishBoxAnchorClick(ClickEvent e) {
		ClearStatus();
		finishBox.setValue(true);
		finishBoxText.addStyleName("CreateOrder_finish");
		order_status = 2;
		finishDatePanel.setVisible(true);
		finishDateBox.setValue(new Date(System.currentTimeMillis()));
	}
	
	@UiHandler("exitButton")
	void onExitButtonClick(ClickEvent e) {
		PrettyGal.placeController.goTo(new OrderPlace());
	}
	
	@UiHandler("findCustomer")
	void onFindCustomerClick(ClickEvent e) {
		final ListCustomerDialog dialog = new ListCustomerDialog(new ListCustomerDialog_Listener() {
			
			@Override
			public void onSelectedCustomer(Customer selectedCustomer) {
				customer = selectedCustomer;
				if(selectedCustomer != null)
					setCustomerToView(selectedCustomer);
			}
		});
		Timer t = new Timer() {

			@Override
			public void run() {
				dialog.center();
			}};
		t.schedule(50);
	}
	
	void setCustomerToView(Customer customer) {
		nameCustomer.setText(customer.getName());
		addressCustomer.setText(customer.getAddress());
		phoneCustomer.setText(PrettyGal.phoneFormat(customer.getPhone()));
		emailCustomer.setText(customer.getEmail());
		newCustomerPanel.setVisible(true);
	}
	
	@UiHandler("newCustomer")
	void onNewCustomerClick(ClickEvent e) {
		customer = null;
		nameCustomer.setText("");
		addressCustomer.setText("");
		phoneCustomer.setText("");
		emailCustomer.setText("");
		newCustomerPanel.setVisible(false);
		nameCustomer.setFocus(true);
	}
	
}
