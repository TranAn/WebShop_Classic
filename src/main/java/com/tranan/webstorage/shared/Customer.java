package com.tranan.webstorage.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity(name = "Customer")
@Cache
public class Customer implements Serializable, IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	String phone;

	List<Long> order_ids = new ArrayList<Long>();

	String name;
	String address;
	String email;

	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public List<Long> getOrder_ids() {
		return order_ids;
	}

	public void setOrder_ids(List<Long> order_ids) {
		this.order_ids = order_ids;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}
	
	public boolean isEmpty() {
		if((name == null || name.isEmpty())
			&& (address == null || address.isEmpty())
			&& (phone == null)
			&& (email == null || email.isEmpty()))
				return true;
		return false;
	}

}
