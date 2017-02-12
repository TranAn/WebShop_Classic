package com.tranan.webstorage.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListCustomer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Customer> listCustomer = new ArrayList<Customer>();
	private String cursorStr;
	private int total;

	public static final int pageSize = 6;

	public ListCustomer() {
		super();
	}

	public ListCustomer(List<Customer> listCustomer, String cursorStr, int total) {
		super();
		this.listCustomer = listCustomer;
		this.cursorStr = cursorStr;
		this.total = total;
	}

	public List<Customer> getListCustomer() {
		return listCustomer;
	}

	public void setListCustomer(List<Customer> listCustomer) {
		this.listCustomer = listCustomer;
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
