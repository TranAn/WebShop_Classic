package com.tranan.webstorage.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListOrderIn implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<OrderIn> listOrderIn = new ArrayList<OrderIn>();
	private String cursorStr;
	private int total;

	public static final int pageSize = 10;

	public ListOrderIn() {
		super();
	}

	public ListOrderIn(List<OrderIn> listOrderIn, String cursorStr, int total) {
		super();
		this.listOrderIn = listOrderIn;
		this.cursorStr = cursorStr;
		this.total = total;
	}

	public List<OrderIn> getListOrderIn() {
		return listOrderIn;
	}

	public void setListOrderIn(List<OrderIn> listOrderIn) {
		this.listOrderIn = listOrderIn;
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
