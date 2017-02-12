package com.tranan.webstorage.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity(name = "EntitiesSize")
@Cache
public class EntitiesSize implements Serializable, IsSerializable {

	@Id
	Long id;

	int item_size = 0;
	int order_size = 0;
	int orderin_size = 0;
	int order_pending_size = 0;
	int order_delivery_size = 0;
	int order_finish_size = 0;
	int customer_size = 0;

	public EntitiesSize() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getItem_size() {
		return item_size;
	}

	public void setItem_size(int item_size) {
		this.item_size = item_size;
	}

	public int getOrder_size() {
		return order_size;
	}

	public void setOrder_size(int order_size) {
		this.order_size = order_size;
	}

	public int getOrderin_size() {
		return orderin_size;
	}

	public void setOrderin_size(int orderin_size) {
		this.orderin_size = orderin_size;
	}

	public int getOrder_pending_size() {
		return order_pending_size;
	}

	public void setOrder_pending_size(int order_pending_size) {
		this.order_pending_size = order_pending_size;
	}

	public int getOrder_delivery_size() {
		return order_delivery_size;
	}

	public void setOrder_delivery_size(int order_delivery_size) {
		this.order_delivery_size = order_delivery_size;
	}

	public int getOrder_finish_size() {
		return order_finish_size;
	}

	public void setOrder_finish_size(int order_finish_size) {
		this.order_finish_size = order_finish_size;
	}

	public int getCustomer_size() {
		return customer_size;
	}

	public void setCustomer_size(int customer_size) {
		this.customer_size = customer_size;
	}

}
