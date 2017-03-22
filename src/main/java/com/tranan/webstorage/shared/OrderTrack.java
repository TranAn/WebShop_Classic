package com.tranan.webstorage.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity(name = "OrderTrack")
@Cache
public class OrderTrack implements Serializable, IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	String id;

	List<Long> order_ids = new ArrayList<Long>();
	List<Long> order_in_ids = new ArrayList<Long>();

	public OrderTrack() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Long> getOrder_ids() {
		return order_ids;
	}

	public void setOrder_ids(List<Long> order_ids) {
		this.order_ids = order_ids;
	}

	public List<Long> getOrder_in_ids() {
		return order_in_ids;
	}

	public void setOrder_in_ids(List<Long> order_in_ids) {
		this.order_in_ids = order_in_ids;
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
		OrderTrack other = (OrderTrack) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderTrack [id=" + id + ", order_ids=" + order_ids
				+ ", order_in_ids=" + order_in_ids + "]";
	}

}
