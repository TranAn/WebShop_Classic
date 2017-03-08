package com.tranan.webstorage.client_admin.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.Ruler;
import com.tranan.webstorage.client_admin.dialog.ListCustomerDialog;
import com.tranan.webstorage.client_admin.dialog.ListCustomerDialog.ListCustomerDialog_Listener;
import com.tranan.webstorage.client_admin.place.CreateOrderPlace;
import com.tranan.webstorage.client_admin.sub_ui.NoticePanel;
import com.tranan.webstorage.client_admin.sub_ui.OrderTable_Row;
import com.tranan.webstorage.client_admin.sub_ui.OrderTable_Row.OrderTableRowListener;
import com.tranan.webstorage.client_admin.sub_ui.Pager;
import com.tranan.webstorage.client_admin.sub_ui.Pager.PagerListener;
import com.tranan.webstorage.shared.Customer;
import com.tranan.webstorage.shared.ListOrder;
import com.tranan.webstorage.shared.Order;
import com.tranan.webstorage.shared.OrderChannel;

public class OrderTable extends Composite {

	private static OrderTableUiBinder uiBinder = GWT
			.create(OrderTableUiBinder.class);

	interface OrderTableUiBinder extends UiBinder<Widget, OrderTable> {
	}
	
	@UiField
	ScrollPanel scrollTable;
	@UiField
	HTMLPanel orderTable;
	@UiField
	Label orderTableTitle;
	@UiField
	Pager pager;
	@UiField
	HTMLPanel filterTable;
	@UiField
	HTMLPanel filterTableContent;
	@UiField
	ListBox filterListBox;
	@UiField
	CheckBox pendingBox;
	@UiField
	CheckBox deliveryBox;
	@UiField
	CheckBox finishBox;
	@UiField 
	Label pendingBoxText;
	@UiField 
	Label deliveryBoxText;
	@UiField 
	Label finishBoxText;
	@UiField
	HTMLPanel statusFilter;
	@UiField
	HTMLPanel customerFilter;
	
	public static ListOrder listOrder;
	public static List<OrderChannel> channels;
	
	private List<Order> displayItem;
	
	private int filter_status;
	
	private void getListOrder(String cursor) {
		if(OrderTable.listOrder == null || !cursor.equals("")) {
			NoticePanel.onLoading();
			PrettyGal.dataService.getOrders(cursor, new AsyncCallback<ListOrder>() {
	
				@Override
				public void onSuccess(ListOrder result) {
					if (listOrder == null) {
						if(result != null)
							listOrder = result;
						else {
							listOrder = new ListOrder();
							listOrder.setTotal(0);
						}
						initPager(result);
					} else {
						listOrder.setCursorStr(result.getCursorStr());
						listOrder.getListOrder().addAll(result.getListOrder());
					}
					
					setTableView(result.getListOrder());
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
			initPager(listOrder);
			
			List<Order> displayItem;
			if (ListOrder.pageSize >= listOrder.getListOrder().size())
				displayItem = listOrder.getListOrder().subList(0, listOrder.getListOrder().size());
			else
				displayItem = listOrder.getListOrder().subList(0, ListOrder.pageSize);

			setTableView(displayItem);
		}
	}
	
	private void getFilterStatusOrder(String cursor) {
		NoticePanel.onLoading();
		PrettyGal.dataService.getOrdersByStatus(cursor, filter_status, new AsyncCallback<ListOrder>() {

			@Override
			public void onSuccess(ListOrder result) {
				filterTable.setVisible(false);
				filterTableContent.setHeight("0px");
				
				if (listOrder == null) {
					if(result != null)
						listOrder = result;
					else {
						listOrder = new ListOrder();
						listOrder.setTotal(0);
					}
					initPager(result);
				} else {
					listOrder.setCursorStr(result.getCursorStr());
					listOrder.getListOrder().addAll(result.getListOrder());
				}

				setTableView(result.getListOrder());
				NoticePanel.endLoading();
			}

			@Override
			public void onFailure(Throwable caught) {
				NoticePanel.failNotice(PrettyGal.ERROR_MSG);
			}
		});
	}
	
	private void getFilterCustomerOrder(Customer customer) {
		NoticePanel.onLoading();
		PrettyGal.dataService.getOrdersByCustomer(customer, new AsyncCallback<ListOrder>() {

			@Override
			public void onSuccess(ListOrder result) {
				filterTable.setVisible(false);
				filterTableContent.setHeight("0px");
				
				if (listOrder == null) {
					if(result != null)
						listOrder = result;
					else {
						listOrder = new ListOrder();
						listOrder.setTotal(0);
					}
					initPager(result);
				} else {
					listOrder.setCursorStr(result.getCursorStr());
					listOrder.getListOrder().addAll(result.getListOrder());
				}

				setTableView(result.getListOrder());
				NoticePanel.endLoading();
			}

			@Override
			public void onFailure(Throwable caught) {
				NoticePanel.failNotice(PrettyGal.ERROR_MSG);
			}
		});
	}
	
	private void initPager(ListOrder orders) {
		pager.setPage(orders.getTotal(), ListOrder.pageSize, new PagerListener() {

			@Override
			public void pageIndex(int index) {
				if (index < listOrder.getListOrder().size()) {
					List<Order> displayItem;
					if(listOrder.getListOrder().size() != 0) {
						if ((index + ListOrder.pageSize) <= listOrder.getListOrder()
								.size())
							displayItem = listOrder.getListOrder().subList(index,
									index + ListOrder.pageSize);
						else
							displayItem = listOrder.getListOrder().subList(index,
									listOrder.getListOrder().size());
					}
					else
						displayItem = new ArrayList<Order>();

					setTableView(displayItem);
				} else {
					getListOrder(listOrder.getCursorStr());					
				}
				scrollTable.scrollToTop();
			}
		});
	}

	private void setTableView(List<Order> orders) {
		orderTable.clear();
		for (Order order: orders) {
			OrderTable_Row row = new OrderTable_Row(new OrderTableRowListener() {
				
				@Override
				public void onDeleteItem(Order order) {
					listOrder.getListOrder().remove(order);
					if(listOrder.getListOrder().isEmpty())
						listOrder.setTotal(0);
					else
						listOrder.setTotal(listOrder.getTotal() - 1);
					pager.updatePage(listOrder.getTotal(), ListOrder.pageSize);
				}
				
				@Override
				public void onClickItem(Order order) {
					PrettyGal.placeController.goTo(new CreateOrderPlace(String.valueOf(order.getId()), order));
				}
			});
			row.setOrder(order);
			orderTable.add(row);
		}
		
		displayItem = orders;
	}
	
	private void showFilter() {
		filterTable.setVisible(true);
		
		Timer t = new Timer() {

			@Override
			public void run() {
				filterTableContent.setHeight("180px");
			}
			
		};t.schedule(100);
	}
	
	private void cancelFilter() {	
		filterTable.setVisible(false);
		filterTableContent.setHeight("0px");
		listOrder = null;
		getListOrder("");
	}
	
	private void ClearStatus() {
		pendingBox.setValue(false);
		deliveryBox.setValue(false);
		finishBox.setValue(false);
		
		pendingBoxText.removeStyleName("OrderTable_pending");
		deliveryBoxText.removeStyleName("OrderTable_delivery");
		finishBoxText.removeStyleName("OrderTable_finish");
	}

	public OrderTable() {
		initWidget(uiBinder.createAndBindUi(this));
		
		scrollTable.setHeight(Ruler.ItemTable_H + "px");
		
		getListOrder("");
		
		filterListBox.addItem("Tình trạng đơn hàng");
		filterListBox.addItem("Khách hàng");
		
		filterListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				if(filterListBox.getSelectedIndex() == 0) {
					statusFilter.setVisible(true);
					customerFilter.setVisible(false);
				}
				else {
					statusFilter.setVisible(false);
					customerFilter.setVisible(true);
				}
			}
		});
		
		pendingBox.setValue(true);
		pendingBoxText.addStyleName("OrderTable_pending");
		filter_status = Order.PENDING;
	}
	
	public void AddItem(Order order) {
		if(listOrder != null) {
			if(!listOrder.getListOrder().contains(order)) {
				listOrder.getListOrder().add(0, order);
				listOrder.setTotal(listOrder.getTotal() + 1);
				initPager(listOrder);
				if(listOrder.getListOrder().size() > ListOrder.pageSize)
					displayItem = listOrder.getListOrder().subList(0, ListOrder.pageSize);
				else
					displayItem = listOrder.getListOrder().subList(0, listOrder.getListOrder().size());
			}
			else {
				int index = listOrder.getListOrder().indexOf(order);
				listOrder.getListOrder().remove(index);
				listOrder.getListOrder().add(index, order);
				
				index = this.displayItem.indexOf(order);
				displayItem.remove(index);
				displayItem.add(index, order);
			}
			
			setTableView(displayItem);
		}
	}
	
	@UiHandler("addOrderButton")
	void onAddOrderButtonClick(ClickEvent e) {
		PrettyGal.placeController.goTo(new CreateOrderPlace("", null));
	}
	
	@UiHandler("filterButton")
	void onFilterButtonClick(ClickEvent e) {
		showFilter();
	}
	
	@UiHandler("startFilterButton")
	void onStartFilterButtonClick(ClickEvent e) {
		listOrder = null;
		getFilterStatusOrder("");
	}
	
	@UiHandler("cancelFilterButton")
	void onCancelFilterButtonClick(ClickEvent e) {
		cancelFilter();
	}
	
	@UiHandler("pendingBoxAnchor")
	void onPendingBoxAnchorClick(ClickEvent e) {
		ClearStatus();
		pendingBox.setValue(true);
		pendingBoxText.addStyleName("OrderTable_pending");
		filter_status = Order.PENDING;
	}
	
	@UiHandler("deliveryBoxAnchor")
	void onDeliveryBoxAnchorClick(ClickEvent e) {
		ClearStatus();
		deliveryBox.setValue(true);
		deliveryBoxText.addStyleName("OrderTable_delivery");
		filter_status = Order.DELIVERY;
	}
	
	@UiHandler("finishBoxAnchor")
	void onFinishBoxAnchorClick(ClickEvent e) {
		ClearStatus();
		finishBox.setValue(true);
		finishBoxText.addStyleName("OrderTable_finish");
		filter_status = Order.FINISH;
	}
	
	@UiHandler("findCustomer")
	void onFindCustomerClick(ClickEvent e) {
		final ListCustomerDialog dialog = new ListCustomerDialog(new ListCustomerDialog_Listener() {
			
			@Override
			public void onSelectedCustomer(Customer selectedCustomer) {
				listOrder = null;
				getFilterCustomerOrder(selectedCustomer);
			}
		});
		Timer t = new Timer() {

			@Override
			public void run() {
				dialog.center();
			}};
		t.schedule(50);
	}
	
}
