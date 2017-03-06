package com.tranan.webstorage.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Item> listItem = new ArrayList<Item>();
	private String cursorStr;
	private int total;

	public static final int pageSize = 15;

	public ListItem() {
		super();
	}

	public ListItem(List<Item> listItem, String cursorStr, int total) {
		super();
		this.listItem.addAll(listItem);
		this.cursorStr = cursorStr;
		this.total = total;
	}

	public List<Item> getListItem() {
		return listItem;
	}

	public void setListItem(List<Item> listItem) {
		this.listItem = listItem;
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
