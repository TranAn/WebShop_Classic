package com.tranan.webstorage.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dataservice")
public interface DataService extends RemoteService {
	
	public Item getItemById(Long id);

	public Item createItem(Item item);
	
	public ListItem getItems(String cursor);
	
	public ListItem getItemsInCatalog(String cursor, Long catalog_id);
	
	public ListItem searchItems(String search_string);
	
	public boolean deleteItem(Long id);
	
	public void importItems(List<Item> items, List<Item> orderIn_items);
	
	public String getUploadUrl(Long itemId);
	
	public Photo getPhoto(Long photoId);
	
	public Catalog createCatalog(Catalog catalog);
	
	public List<Catalog> getCatalogs();
	
	public void deleteCatalog(Catalog catalog);
	
	public Order createOrder(Order order);
	
	public ListOrder getOrders(String cursor);
	
	public ListOrder getOrdersByStatus(String cursor, int status);
	
	public ListOrder getOrdersByCustomer(Customer customer);
	
	public boolean deleteOrder(Long id);
	
	public void updateOrderStatus(Long order_id, int status);
	
	public List<OrderChannel> getChannels();
	
	public ListCustomer getCustomers(String cursor);
	
	public ListCustomer searchCustomer(String search_string);
	
	public Sale createSale(Sale sale);
	
	public List<Sale> getSales();
	
	public void deleteSale(Long id);
	
}
