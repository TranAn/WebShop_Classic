package com.tranan.webstorage.client_admin;

import com.tranan.webstorage.client_admin.ui.CreateItem;
import com.tranan.webstorage.client_admin.ui.CreateOrder;
import com.tranan.webstorage.client_admin.ui.CreateSale;
import com.tranan.webstorage.client_admin.ui.ItemTable;
import com.tranan.webstorage.client_admin.ui.OrderTable;
import com.tranan.webstorage.client_admin.ui.SaleTable;
import com.tranan.webstorage.client_admin.ui.StatisticTable;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Order;
import com.tranan.webstorage.shared.Sale;

public class UIController {

	ItemTable itemTable;
	OrderTable orderTable;
	SaleTable saleTable;
	StatisticTable statisticTable;
	CreateItem createItem;
	CreateOrder createOrder;
	CreateSale createSale;

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

	public SaleTable getSaleTable() {
		if (saleTable == null)
			saleTable = new SaleTable();
		return saleTable;
	}
	
	public StatisticTable getStatisticTable() {
		if (statisticTable == null)
			statisticTable = new StatisticTable();
		return statisticTable;
	}

	public CreateItem getCreateItem(Item item) {
		createItem = new CreateItem();
		if (item != null)
			createItem.setItem(item);
		return createItem;
	}

	public CreateOrder getCreateOrder(Order order) {
		createOrder = new CreateOrder();
		if (order != null)
			createOrder.setOrder(order);
		return createOrder;
	}
	
	public CreateSale getCreateSale(Sale sale) {
		createSale = new CreateSale();
		if (sale != null)
			createSale.setSale(sale);
		return createSale;
	}

}
