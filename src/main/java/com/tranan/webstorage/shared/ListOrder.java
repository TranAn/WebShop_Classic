package com.tranan.webstorage.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Order> listOrder = new ArrayList<Order>();
	private String cursorStr;
	private int total;

	public static final int pageSize = 6;

	public ListOrder() {
		super();
	}

	public ListOrder(List<Order> listOrder, String cursorStr, int total) {
		super();
		this.listOrder = listOrder;
		this.cursorStr = cursorStr;
		this.total = total;
	}

	public List<Order> getListOrder() {
		return listOrder;
	}

	public void setListOrder(List<Order> listOrder) {
		this.listOrder = listOrder;
	}

	public String getCursorStr() {
		return cursorStr;
	}

	public void setCursorStr(String cursorStr) {
		this.cursorStr = cursorStr;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
