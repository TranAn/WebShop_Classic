package com.tranan.webstorage.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dataservice")
public interface DataService extends RemoteService {
	
	public Item getItemById(Long id);

	public Item createItem(Item item, String token) throws Exception;
	
	public ListItem getItems(String cursor, int pageSize);
	
	public ListItem getItemsInCatalog(String cursor, Long catalog_id);
	
	public ListItem searchItems(String search_string);
	
	public boolean deleteItem(Long id, String token) throws Exception;
	
	public void importItems(List<Item> items, List<Item> orderIn_items, String token) throws Exception;
	
	public String getUploadUrl(Long itemId);
	
	public Photo getPhoto(Long photoId);
	
	public Catalog createCatalog(Catalog catalog, String token) throws Exception;
	
	public List<Catalog> getCatalogs();
	
	public void deleteCatalog(Catalog catalog, String token) throws Exception;
	
	public Order createOrder(Order order, String token) throws Exception;
	
	public ListOrder getOrders(String cursor);
	
	public ListOrder getOrdersByStatus(String cursor, int status);
	
	public ListOrder getOrdersByCustomer(Customer customer);
	
	public boolean deleteOrder(Long id, String token) throws Exception;
	
	public void deleteOrderIn(Long id, String token) throws Exception;
	
	public Order updateOrderStatus(Long order_id, int status, String token) throws Exception;
	
	public List<OrderChannel> getChannels();
	
	public ListCustomer getCustomers(String cursor);
	
	public ListCustomer searchCustomer(String search_string);
	
	public Sale createSale(Sale sale, String token) throws Exception;
	
	public List<Sale> getSales();
	
	public void deleteSale(Long id, String token) throws Exception;
	
	public ListOrderIn getOrderIns(String cursor);
	
	public StatisticData getStatisticData(String data_id);
	
	public boolean checkAuth(String idToken);
	
}
