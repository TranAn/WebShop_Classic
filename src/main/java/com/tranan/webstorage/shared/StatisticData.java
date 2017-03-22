package com.tranan.webstorage.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StatisticData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Order> listOrder = new ArrayList<Order>();
	private List<OrderIn> listOrderIn = new ArrayList<OrderIn>();

	public StatisticData() {
		super();
	}

	public List<Order> getListOrder() {
		return listOrder;
	}

	public void setListOrder(List<Order> listOrder) {
		this.listOrder = listOrder;
	}

	public List<OrderIn> getListOrderIn() {
		return listOrderIn;
	}

	public void setListOrderIn(List<OrderIn> listOrderIn) {
		this.listOrderIn = listOrderIn;
	}

}
