package com.tranan.webstorage.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity(name = "Photo")
@Cache
public class Photo implements Serializable, IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	Long id;

	Long itemId;

	String blobkey;
	String serveUrl;

	public Photo() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getBlobkey() {
		return blobkey;
	}

	public void setBlobkey(String blobkey) {
		this.blobkey = blobkey;
	}

	public String getServeUrl() {
		return serveUrl;
	}

	public void setServeUrl(String serveUrl) {
		this.serveUrl = serveUrl;
	}

	@Override
	public String toString() {
		return "Photo [id=" + id + ", itemId=" + itemId + ", blobkey="
				+ blobkey + ", serveUrl=" + serveUrl + "]";
	}

}
