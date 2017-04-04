package com.tranan.webstorage.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataServiceAsync {

	void getItemById(Long id, AsyncCallback<Item> callback);

	void createItem(Item item, String token, AsyncCallback<Item> callback);

	void getItems(String cursor, AsyncCallback<ListItem> callback);
	
	void getItemsInCatalog(String cursor, Long catalog_id, AsyncCallback<ListItem> callback);
	
	void searchItems(String search_string, AsyncCallback<ListItem> callback);

	void deleteItem(Long id, String token, AsyncCallback<Boolean> callback);

	void importItems(List<Item> items, List<Item> orderIn_items, String token, AsyncCallback<Void> callback);

	void getUploadUrl(Long itemId, AsyncCallback<String> callback);

	void getPhoto(Long photoId, AsyncCallback<Photo> callback);

	void createCatalog(Catalog catalog, String token, AsyncCallback<Catalog> callback);

	void getCatalogs(AsyncCallback<List<Catalog>> callback);
	
	void deleteCatalog(Catalog catalog, String token, AsyncCallback<Void> callback);
	
	void createOrder(Order order, String token, AsyncCallback<Order> callback);
	
	void getOrders(String cursor, AsyncCallback<ListOrder> callback);
	
	void getOrdersByStatus(String cursor, int status, AsyncCallback<ListOrder> callback);
	
	void getOrdersByCustomer(Customer customer, AsyncCallback<ListOrder> callback);
	
	void deleteOrder(Long id, String token, AsyncCallback<Boolean> callback);
	
	void deleteOrderIn(Long id, String token, AsyncCallback<Void> callback);
	
	void updateOrderStatus(Long order_id, int status, String token, AsyncCallback<Order> callback);
	
	void getChannels(AsyncCallback<List<OrderChannel>> callback);
	
	void getCustomers(String cursor, AsyncCallback<ListCustomer> callback);
	
	void searchCustomer(String search_string, AsyncCallback<ListCustomer> callback);
	
	void createSale(Sale sale, String token, AsyncCallback<Sale> callback);
	
	void getSales(AsyncCallback<List<Sale>> callback);
	
	void deleteSale(Long id, String token, AsyncCallback<Void> callback);
	
	void getOrderIns(String cursor, AsyncCallback<ListOrderIn> callback);
	
	void getStatisticData(String data_id, AsyncCallback<StatisticData> callback);
	
	void checkAuth(String idToken, AsyncCallback<Boolean> callback);

}
