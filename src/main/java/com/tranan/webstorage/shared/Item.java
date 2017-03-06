package com.tranan.webstorage.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity(name = "Item")
@Cache
public class Item implements Serializable, IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_TYPE = "Default";

	@Id
	Long id;

	List<Long> photo_ids = new ArrayList<Long>();
	List<Long> catalog_ids = new ArrayList<Long>();

	@Index
	String name;
	@Index
	Long cost;
	@Index
	Long price;
	Long sale_id;
	int sale;
	Long sale_price;
	String description;

	List<Type> type = new ArrayList<Type>();

	String avatar_url = "";

	public static class Type implements Serializable {
		String name;
		int quantity;

		public Type() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		public Type(String name, int quantity) {
			super();
			this.name = name;
			this.quantity = quantity;
		}
		
		public Type(Type t) {
			super();
			this.name = t.getName();
			this.quantity = t.getQuantity();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			Type other = (Type) obj;
			
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}

	public Item() {
		super();
	}

	public Item(Long id, List<Long> photo_ids, List<Long> catalog_ids,
			String name, Long cost, Long price, int sale, Long sale_id, Long sale_price, 
			String description, List<Type> type, String avatar_url) {
		super();
		this.id = id;
		this.photo_ids.addAll(photo_ids);
		this.catalog_ids.addAll(catalog_ids);
		this.name = name;
		this.cost = cost;
		this.price = price;
		this.sale = sale;
		this.sale_id = sale_id;
		this.sale_price = sale_price;
		this.description = description;
		this.type.addAll(type);
		this.avatar_url = avatar_url;
	}
	
	//Deep copy an item
	public Item(Item i) {
		super();
		this.id = i.getId();
		this.photo_ids.addAll(i.getPhoto_ids());
		this.catalog_ids.addAll(i.getCatalog_ids());
		this.name = i.getName();
		this.cost = i.getCost();
		this.price = i.getPrice();
		this.sale = i.getSale();
		this.sale_id = i.getSale_id();
		this.sale_price = i.getSale_price();
		this.description = i.getDescription();
		for(Type t: i.getType())
			this.type.add(new Type(t));
		this.avatar_url = i.getAvatar_url();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Long> getPhoto_ids() {
		return photo_ids;
	}

	public void setPhoto_ids(List<Long> photo_ids) {
		this.photo_ids = photo_ids;
	}

	public List<Long> getCatalog_ids() {
		return catalog_ids;
	}

	public void setCatalog_ids(List<Long> catalog_ids) {
		this.catalog_ids = catalog_ids;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCost() {
		return cost;
	}

	public void setCost(Long cost) {
		this.cost = cost;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Long getSale_id() {
		return sale_id;
	}

	public void setSale_id(Long sale_id) {
		this.sale_id = sale_id;
	}

	public int getSale() {
		return sale;
	}

	public void setSale(int sale) {
		this.sale = sale;
	}

	public Long getSale_price() {
		return sale_price;
	}

	public void setSale_price(Long sale_price) {
		this.sale_price = sale_price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Type> getType() {
		return type;
	}

	public void setType(List<Type> type) {
		this.type = type;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", photo_ids=" + photo_ids + ", catalog_ids="
				+ catalog_ids + ", name=" + name + ", cost=" + cost
				+ ", price=" + price + ", sale_id=" + sale_id + ", sale="
				+ sale + ", sale_price=" + sale_price + ", description="
				+ description + ", type=" + type + ", avatar_url=" + avatar_url
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Item other = (Item) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
