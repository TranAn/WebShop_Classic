package com.tranan.webstorage.client_admin;

import com.tranan.webstorage.client_admin.ui.CreateItem;
import com.tranan.webstorage.client_admin.ui.CreateOrder;
import com.tranan.webstorage.client_admin.ui.ItemTable;
import com.tranan.webstorage.client_admin.ui.OrderTable;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Order;

public class UIController {

	ItemTable itemTable;
	OrderTable orderTable;
	CreateItem createItem;
	CreateOrder createOrder;

	public UIController() {
		super();
	}

	public ItemTable getItemTable() {
		if (itemTable == null)
			itemTable = new ItemTable();
		return itemTable;
	}
	
	public OrderTable getOrderTable() {
		if (orderTable == null)
			orderTable = new OrderTable();
		return orderTable;
	}

	public CreateItem getCreateItem(Item item) {
		createItem = new CreateItem();
		if(item != null)
			createItem.setItem(item);
		return createItem;
	}
	
	public CreateOrder getCreateOrder(Order order) {
		createOrder = new CreateOrder();
		if(order != null)
			createOrder.setOrder(order);
		return createOrder;
	}

}
