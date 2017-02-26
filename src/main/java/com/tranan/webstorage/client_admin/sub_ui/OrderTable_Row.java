package com.tranan.webstorage.client_admin.sub_ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.sub_ui.ItemTable_Row.ItemTableRowListener;
import com.tranan.webstorage.client_admin.ui.ItemTable;
import com.tranan.webstorage.client_admin.ui.OrderTable;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Order;

public class OrderTable_Row extends Composite {

	private static OrderTable_RowUiBinder uiBinder = GWT
			.create(OrderTable_RowUiBinder.class);

	interface OrderTable_RowUiBinder extends UiBinder<Widget, OrderTable_Row> {
	}
	
	@UiField HTMLPanel col1;
	@UiField Label customerName;
	@UiField Label customerAddress;
	@UiField Label customerPhone;
	@UiField Label customerEmail;
	@UiField HTMLPanel itemsCol;
	@UiField Label orderSum;
	@UiField Label orderCreateDate;
	@UiField ListBox orderStatus;
	
	private Order order;
	private OrderTableRowListener listener;
	
	public interface OrderTableRowListener {
		void onClickItem(Order order);
		void onDeleteItem(Order order);
	}

	public OrderTable_Row(OrderTableRowListener listener) {
		initWidget(uiBinder.createAndBindUi(this));
		
		orderStatus.addItem("Đợi giao hàng");
		orderStatus.addItem("Đang giao hàng");
		orderStatus.addItem("Hoàn thành");
		
		orderStatus.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				NoticePanel.onLoading();
				PrettyGal.dataService.updateOrderStatus(order.getId(), orderStatus.getSelectedIndex(), new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						NoticePanel.endLoading();
						
						if(order.getStatus() == Order.PENDING && (orderStatus.getSelectedIndex() == Order.DELIVERY || orderStatus.getSelectedIndex() == Order.FINISH))
							ItemTable.ClearListItem();
						else if((order.getStatus() == Order.DELIVERY || order.getStatus() == Order.FINISH) && orderStatus.getSelectedIndex() == Order.PENDING)
							ItemTable.ClearListItem();
						
						order.setStatus(orderStatus.getSelectedIndex());
						int index = OrderTable.listOrder.getListOrder().indexOf(order);
						OrderTable.listOrder.getListOrder().get(index).setStatus(orderStatus.getSelectedIndex());
						
						col1.removeStyleName("orderTable_pending");
						col1.removeStyleName("orderTable_delivery");
						col1.removeStyleName("orderTable_finish");
						
						if(order.getStatus() == Order.PENDING)
							col1.addStyleName("orderTable_pending");
						else if (order.getStatus() == Order.DELIVERY)
							col1.addStyleName("orderTable_delivery");
						else if (order.getStatus() == Order.FINISH)
							col1.addStyleName("orderTable_finish");
					}
					
					@Override
					public void onFailure(Throwable caught) {
						NoticePanel.failNotice(PrettyGal.ERROR_MSG);
					}
				});
			}
		});
		
		this.listener = listener;
	}
	
	public void setOrder(Order order) {
		this.order = order;
		
		if(order.getStatus() == Order.PENDING)
			col1.addStyleName("orderTable_pending");
		else if (order.getStatus() == Order.DELIVERY)
			col1.addStyleName("orderTable_delivery");
		else if (order.getStatus() == Order.FINISH)
			col1.addStyleName("orderTable_finish");
		
		if(order.getCustomer() != null) {
			customerName.setText(order.getCustomer().getName());
			customerAddress.setText(order.getCustomer().getAddress());
			customerPhone.setText(PrettyGal.phoneFormat(order.getCustomer().getPhone()));
			customerEmail.setText(order.getCustomer().getEmail());
		}
		
		itemsCol.clear();
		Long sum = 0L;
		for(Item item: order.getOrder_items()) {
			addItemCol(item);
			sum = sum + (item.getPrice() * item.getType().get(0).getQuantity());
		}
		
		orderSum.setText(PrettyGal.integerToPriceString(sum));
		
		if(order.getCreate_date() != null) {
			String dateString = DateTimeFormat.getFormat("dd / MM / yyyy").format(order.getCreate_date());
			orderCreateDate.setText(dateString);
		}
		
		if(order.getStatus() == Order.PENDING)
			orderStatus.setSelectedIndex(0);
		if(order.getStatus() == Order.DELIVERY)
			orderStatus.setSelectedIndex(1);
		if(order.getStatus() == Order.FINISH)
			orderStatus.setSelectedIndex(2);
	}
	
	void addItemCol(Item item) {
		HTMLPanel panel1 = new HTMLPanel("");
		panel1.setStyleName("itemTable_row");
		
		HTMLPanel panel2 = new HTMLPanel("");
		panel2.setStyleName("itemTable_col1");
		Image img = new Image();
		img.setSize("30px", "30px");
		img.setUrl(item.getAvatar_url());
		panel2.add(img);
		
		Label lb1 = new Label(item.getName());
		lb1.setStyleName("itemTable_col2");
		if(!item.getType().get(0).getName().equals(Item.DEFAULT_TYPE))
			lb1.setText(lb1.getText() + " ("+ item.getType().get(0).getName()+ ")");
		
		Label lb2 = new Label();
		lb2.setStyleName("itemTable_col3");
		lb2.setText(item.getType().get(0).getQuantity() + " x "+ PrettyGal.integerToPriceString(item.getPrice()));
		
		panel1.add(panel2);
		panel1.add(lb1);
		panel1.add(lb2);
		itemsCol.add(panel1);
	}
	
	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent e) {
		if(Window.confirm("Bạn muốn hủy đơn hàng này?")) {
			NoticePanel.onLoading();
			PrettyGal.dataService.deleteOrder(order.getId(), new AsyncCallback<Boolean>() {
				
				@Override
				public void onSuccess(Boolean result) {
					if(result) {
						if(order.getStatus() == Order.DELIVERY)
							ItemTable.ClearListItem();
						
						listener.onDeleteItem(order);	
						NoticePanel.successNotice("Đơn hàng đã bị hủy");	
					} else
						NoticePanel.failNotice(PrettyGal.ERROR_MSG);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					NoticePanel.failNotice(PrettyGal.ERROR_MSG);
				}
			});
		}
	}
	
	@UiHandler("orderDetail")
	void onOrderDetailClick(ClickEvent e) {
		listener.onClickItem(order);
	}

}
