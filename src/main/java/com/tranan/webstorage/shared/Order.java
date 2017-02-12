package com.tranan.webstorage.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity(name = "Order")
@Cache
public class Order implements Serializable, IsSerializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int PENDING = 0;
	public static final int DELIVERY = 1;
	public static final int FINISH = 2;

	@Id
	Long id;
	
	Customer customer;
	List<Item> order_items = new ArrayList<Item>();
	
	@Index
	Date create_date;
	@Index
	Date finish_date;
	@Index
	String sale_channel;
	@Index
	int status;
	
	public Order() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Order(Long id, Customer customer, List<Item> order_items,
			Date create_date, Date finish_date, String sale_channel, int status) {
		super();
		this.id = id;
		this.customer = customer;
		this.order_items = order_items;
		this.create_date = create_date;
		this.finish_date = finish_date;
		this.sale_channel = sale_channel;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Item> getOrder_items() {
		return order_items;
	}

	public void setOrder_items(List<Item> order_items) {
		this.order_items = order_items;
	}

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public Date getFinish_date() {
		return finish_date;
	}

	public void setFinish_date(Date finish_date) {
		this.finish_date = finish_date;
	}

	public String getSale_channel() {
		return sale_channel;
	}

	public void setSale_channel(String sale_channel) {
		this.sale_channel = sale_channel;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", customer=" + customer + ", order_items="
				+ order_items + ", create_date=" + create_date
				+ ", finish_date=" + finish_date + ", sale_channel="
				+ sale_channel + ", status=" + status + "]";
	}

}
