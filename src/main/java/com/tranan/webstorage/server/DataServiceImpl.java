package com.tranan.webstorage.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import com.tranan.webstorage.shared.Catalog;
import com.tranan.webstorage.shared.Customer;
import com.tranan.webstorage.shared.DataService;
import com.tranan.webstorage.shared.EntitiesSize;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Item.Type;
import com.tranan.webstorage.shared.ListCustomer;
import com.tranan.webstorage.shared.ListItem;
import com.tranan.webstorage.shared.ListOrder;
import com.tranan.webstorage.shared.Order;
import com.tranan.webstorage.shared.OrderChannel;
import com.tranan.webstorage.shared.OrderIn;
import com.tranan.webstorage.shared.Photo;
import com.tranan.webstorage.shared.Sale;

@SuppressWarnings("serial")
public class DataServiceImpl extends RemoteServiceServlet implements
		DataService {

	private BlobstoreService blobStoreService = BlobstoreServiceFactory
			.getBlobstoreService();
	
	private String removeAccent(String s) {		
		String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(temp).replaceAll("");
	}

	private void createItemDocument(Item item) {
		// creates a document with all Post fields
		String item_doc = removeAccent(item.getName().toLowerCase());
		Document document = Document.newBuilder()
				.setId(item.getId().toString())
				.addField(Field.newBuilder().setName("name")
				.setText(item_doc))
				.build();

		// creates an Index and saves the Document
		IndexSpec indexSpec = IndexSpec.newBuilder().setName("ItemDocument").build();
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
		index.put(document);
	}
	
	private void createCustomerDocument(Customer customer) {
		// creates a document with all Post fields
		String customer_doc = removeAccent(customer.getName().toLowerCase());
		Document document = Document.newBuilder()
				.setId(customer.getPhone().toString())
				.addField(Field.newBuilder().setName("name")
				.setText(customer_doc))
				.build();

		// creates an Index and saves the Document
		IndexSpec indexSpec = IndexSpec.newBuilder().setName("CustomerDocument").build();
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
		index.put(document);
	}
	
	private EntitiesSize addOrderStatusSize(EntitiesSize entitiesSize, Order order) {
		if(order.getStatus() == Order.PENDING)
			entitiesSize.setOrder_pending_size(entitiesSize.getOrder_pending_size() + 1);
		else if(order.getStatus() == Order.DELIVERY)
			entitiesSize.setOrder_delivery_size(entitiesSize.getOrder_delivery_size() + 1);
		else if(order.getStatus() == Order.FINISH)
			entitiesSize.setOrder_finish_size(entitiesSize.getOrder_finish_size() + 1);
		
		return entitiesSize;
	}
	
	private EntitiesSize removeOrderStatusSize(EntitiesSize entitiesSize, Order order) {
		if(order.getStatus() == Order.PENDING)
			entitiesSize.setOrder_pending_size(entitiesSize.getOrder_pending_size() - 1);
		else if(order.getStatus() == Order.DELIVERY)
			entitiesSize.setOrder_delivery_size(entitiesSize.getOrder_delivery_size() - 1);
		else if(order.getStatus() == Order.FINISH)
			entitiesSize.setOrder_finish_size(entitiesSize.getOrder_finish_size() - 1);
		
		return entitiesSize;
	}
	
	private void deliveryItem(Order order) {
		for(Item item: order.getOrder_items()) {
			Item item_store = ofy().load().type(Item.class).id(item.getId()).now();
			for(Type type: item_store.getType()) {
				if(type.getName().equals(item.getType().get(0).getName())) {
					int index = item_store.getType().indexOf(type);
					item_store.getType().get(index).setQuantity(
							item_store.getType().get(index).getQuantity() - item.getType().get(0).getQuantity());
					break;
				}
			}
			ofy().save().entity(item_store);
		}
	}
	
	private void returnItem(Order order) {
		for(Item item: order.getOrder_items()) {
			Item item_store = ofy().load().type(Item.class).id(item.getId()).now();
			for(Type type: item_store.getType()) {
				if(type.getName().equals(item.getType().get(0).getName())) {
					int index = item_store.getType().indexOf(type);
					item_store.getType().get(index).setQuantity(
							item_store.getType().get(index).getQuantity() + item.getType().get(0).getQuantity());
					break;
				}
			}
			ofy().save().entity(item_store);
		}
	}
	
	//Current deploy asian-northeast1 UTC/GMT+9
	private Date getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, -2);
		return cal.getTime();
	}

	private void addCustomerOrder(Customer customer, Long order_id) {
		if(!customer.getPhone().equals("1")) {
			Customer cus = ofy().load().type(Customer.class).id(customer.getPhone()).now();
			
			if(cus == null && order_id != null) {
				customer.getOrder_ids().add(order_id);
				
				ofy().save().entity(customer);
				createCustomerDocument(customer);
				
				List<EntitiesSize> entitiesSizes = ofy().load()
						.type(EntitiesSize.class).list();
				EntitiesSize es = entitiesSizes.get(0);
				es.setCustomer_size(es.getCustomer_size() + 1);
				ofy().save().entity(es);
			} 
			else {
				if(order_id != null && !cus.getOrder_ids().contains(order_id))
					cus.getOrder_ids().add(order_id);
			
				ofy().save().entity(cus);
			}
		}
	}

	private void deleteCustomerOrder(String customer_id, Long order_id) {
		Customer customer = ofy().load().type(Customer.class).id(customer_id).now();
		
		if(customer != null) {
			customer.getOrder_ids().remove(order_id);
			ofy().save().entity(customer);
		}
	}
	
	private void activeSaleItems(Sale sale) {
		for(Item item: sale.getSale_items()) {
			Item db_item = ofy().load().type(Item.class).id(item.getId()).now();
			if(db_item != null) {
				db_item.setSale_id(sale.getId());
				db_item.setSale(item.getSale());
				db_item.setSale_price(item.getSale_price());
				
				ofy().save().entity(db_item);
			}
		}
	}
	
	private void deactiveSaleItems(Sale sale) {
		for(Item item: sale.getSale_items()) {
			Item db_item = ofy().load().type(Item.class).id(item.getId()).now();
			if(db_item != null && db_item.getSale_id().equals(sale.getId())) {
				db_item.setSale_id(null);
				db_item.setSale(0);
				db_item.setSale_price(db_item.getPrice());
				
				ofy().save().entity(db_item);
			}
		}
	}
	
	private void removeSaleItem(Item item) {
		Sale sale = ofy().load().type(Sale.class).id(item.getSale_id()).now();
		if(sale != null) {
			for(Item i: sale.getSale_items()) {
				if(i.getId().equals(item.getId())) {
					sale.getSale_items().remove(i);
					break;
				}
			}
			
			ofy().save().entity(sale);
		}
	}
	
	protected void checkSalePlan() {
		List<Sale> list_sales = ofy().load().type(Sale.class).list();
		for(Sale sale: list_sales) {
			if(sale.getTo() != null) {
				Calendar c = Calendar.getInstance(); 
				c.setTime(sale.getTo()); 
				c.set(Calendar.HOUR, 0);
				c.set(Calendar.MINUTE, 0);
				c.add(Calendar.DATE, 1);
				Date expire_date = c.getTime();
				if(getCurrentDate().after(expire_date) && sale.getStatus() == Sale.ACTIVE) {
					sale.setStatus(Sale.DEACTIVE);
					ofy().save().entity(sale);
					deactiveSaleItems(sale);
					continue;
				}
			}
			
			if(sale.getFrom() != null) {
				Calendar c = Calendar.getInstance(); 
				c.setTime(sale.getFrom()); 
				c.set(Calendar.HOUR, 12);
				Date start_date = c.getTime();
				if(getCurrentDate().after(sale.getFrom()) && getCurrentDate().before(start_date) && 
						sale.getStatus() == Sale.DEACTIVE) {
					sale.setStatus(Sale.ACTIVE);
					ofy().save().entity(sale);
					activeSaleItems(sale);
				}
			}
		}
	}

	@Override
	public Item getItemById(Long id) {
		Item item = ofy().load().type(Item.class).id(id).now();
		return item;
	}

	@Override
	public Item createItem(Item item) {
		Item rtn = null;
		Item old_item = null;
		if(item.getId() != null)
			old_item = ofy().load().type(Item.class).id(item.getId()).now();
		
		List<Long> attach_catalog_ids = new ArrayList<Long>();
		List<Long> detach_catalog_ids = new ArrayList<Long>();

		Key<Item> key = ofy().save().entity(item).now();
		rtn = ofy().load().key(key).now();
		
		if (old_item == null) {
			List<EntitiesSize> entitiesSizes = ofy().load()
					.type(EntitiesSize.class).list();
			EntitiesSize es = entitiesSizes.get(0);
			es.setItem_size(es.getItem_size() + 1);
			ofy().save().entity(es);

			attach_catalog_ids.addAll(item.getCatalog_ids());
		} 
		else {
			for (Long id : item.getCatalog_ids())
				if (!old_item.getCatalog_ids().contains(id))
					attach_catalog_ids.add(id);
			for (Long id : old_item.getCatalog_ids())
				if (!item.getCatalog_ids().contains(id))
					detach_catalog_ids.add(id);
		}

		for (Long catalog_id : attach_catalog_ids) {
			Catalog catalog = ofy().load().type(Catalog.class).id(catalog_id)
					.now();
			catalog.getItem_ids().add(rtn.getId());
			ofy().save().entity(catalog);
		}
		for (Long catalog_id : detach_catalog_ids) {
			Catalog catalog = ofy().load().type(Catalog.class).id(catalog_id)
					.now();
			catalog.getItem_ids().remove(rtn.getId());
			ofy().save().entity(catalog);
		}
		
		createItemDocument(rtn);

		return rtn;
	}

	@Override
	public String getUploadUrl(Long itemId) {
		return blobStoreService.createUploadUrl("/photo_upload");
	}

	@Override
	public ListItem getItems(String cursor) {
		List<Item> result = new ArrayList<Item>();

		Query<Item> query = ofy().load().type(Item.class)
				.limit(ListItem.pageSize);
		if (cursor != null)
			query = query.startAt(Cursor.fromWebSafeString(cursor));

		boolean continu = false;
		QueryResultIterator<Item> iterator = query.iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			result.add(item);
			continu = true;
		}

		List<EntitiesSize> entitiesSizes = ofy().load()
				.type(EntitiesSize.class).list();

		if (continu) {
			Cursor cur = iterator.getCursor();
			String nextCur = cur.toWebSafeString();
			ListItem listItem = new ListItem(result, nextCur, entitiesSizes
					.get(0).getItem_size());
			return listItem;
		} else {
			ListItem listItem = new ListItem(result, "\\0", entitiesSizes
					.get(0).getItem_size());
			return listItem;
		}
	}

	/**
	 * first page cursor = 0 end page cursor = -1
	 */
	@Override
	public ListItem getItemsInCatalog(String cursor, Long catalog_id) {
		int pageIndex = Integer.valueOf(cursor);
		Catalog catalog = ofy().load().type(Catalog.class).id(catalog_id).now();

		int fromIndex = pageIndex * ListItem.pageSize;
		int toIndex = fromIndex + ListItem.pageSize;
		if (toIndex >= catalog.getItem_ids().size()) {
			toIndex = catalog.getItem_ids().size();
			cursor = "-1";
		} else
			cursor = String.valueOf(pageIndex + 1);

		List<Long> query_item_ids = catalog.getItem_ids().subList(fromIndex,
				toIndex);
		Collection<Item> listItems = ofy().load().type(Item.class)
				.ids(query_item_ids).values();
		List<Item> listItem = new ArrayList<Item>();
		listItem.addAll(listItems);

		ListItem rtn = new ListItem();
		rtn.setCursorStr(cursor);
		rtn.setListItem(listItem);
		rtn.setTotal(catalog.getItem_ids().size());
		return rtn;
	}

	@Override
	public ListItem searchItems(String search_string) {
		IndexSpec indexSpec = IndexSpec.newBuilder().setName("ItemDocument").build(); 
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
				  				
		String queryString = "name : " + search_string;
        Results<ScoredDocument> results = index.search(queryString);
        
        List<Long> item_matched_id = new ArrayList<Long>();
        for (ScoredDocument document : results) {
            Long id = Long.valueOf(document.getId());
            item_matched_id.add(id);
        }
		
		List<Item> result = new ArrayList<Item>();
		Collection<Item> query = ofy().load().type(Item.class)
				.ids(item_matched_id).values();
		result.addAll(query);
		
		ListItem listItem = new ListItem(result, "", result.size());
		return listItem;
	}

	@Override
	public boolean deleteItem(Long id) {
		Item item = ofy().load().type(Item.class).id(id).now();
		if (item != null) {
			ofy().delete().entity(item);

			List<EntitiesSize> entitiesSizes = ofy().load()
					.type(EntitiesSize.class).list();
			EntitiesSize es = entitiesSizes.get(0);
			es.setItem_size(es.getItem_size() - 1);
			ofy().save().entity(es);

			for (Long catalog_id : item.getCatalog_ids()) {
				Catalog catalog = ofy().load().type(Catalog.class)
						.id(catalog_id).now();
				catalog.getItem_ids().remove(item.getId());
				ofy().save().entity(catalog);
			}

			return true;
		}
		return false;
	}

	@Override
	public void importItems(List<Item> items, List<Item> orderIn_items) {
		ofy().save().entities(items);
		
		OrderIn orderIn = new OrderIn();
		orderIn.setOrder_items(orderIn_items); 
		orderIn.setCreate_date(getCurrentDate());
		ofy().save().entity(orderIn);
		
		List<EntitiesSize> entitiesSizes = ofy().load()
				.type(EntitiesSize.class).list();
		EntitiesSize es = entitiesSizes.get(0);
		es.setOrderin_size(es.getOrderin_size() + 1);
		ofy().save().entity(es);
	}

	@Override
	public Photo getPhoto(Long photoId) {
		Photo photo = ofy().load().type(Photo.class).id(photoId).safe();
		return photo;
	}

	@Override
	public Catalog createCatalog(Catalog catalog) {
		Catalog rtn = null;
		Key<Catalog> key = ofy().save().entity(catalog).now();
		rtn = ofy().load().key(key).now();

		return rtn;
	}

	@Override
	public List<Catalog> getCatalogs() {
		List<Catalog> rtn = new ArrayList<Catalog>();
		List<Catalog> catalogs = ofy().load().type(Catalog.class).list();
		rtn.addAll(catalogs);
		return rtn;
	}

	@Override
	public void deleteCatalog(Catalog catalog) {
		ofy().delete().entity(catalog);
	}

	@Override
	public Order createOrder(Order order) {
		Order rtn = null;
		Order old_order = null;
		if(order.getId() != null)
			old_order = ofy().load().type(Order.class).id(order.getId()).now();
	
		//Add empty customer default id
		if(order.getCustomer().getPhone() == null || order.getCustomer().getPhone().isEmpty())
			order.getCustomer().setPhone("1");

		//Save order
		Key<Order> key = ofy().save().entity(order).now();
		rtn = ofy().load().key(key).now();
		
		//Save new customer
		if(rtn.getCustomer() != null)
			addCustomerOrder(rtn.getCustomer(), rtn.getId());
		
		if (old_order == null) {
			List<EntitiesSize> entitiesSizes = ofy().load()
					.type(EntitiesSize.class).list();
			EntitiesSize es = entitiesSizes.get(0);
			es.setOrder_size(es.getOrder_size() + 1);
			addOrderStatusSize(es, order);
			ofy().save().entity(es);
			
			if(order.getStatus() == Order.DELIVERY || order.getStatus() == Order.FINISH)
				deliveryItem(order);
		} 
		else {
			if(order.getStatus() != old_order.getStatus()) {
				List<EntitiesSize> entitiesSizes = ofy().load()
						.type(EntitiesSize.class).list();
				EntitiesSize es = entitiesSizes.get(0);
				removeOrderStatusSize(es, old_order);
				addOrderStatusSize(es, order);
				ofy().save().entity(es);
				
				if(old_order.getStatus() == Order.PENDING && (order.getStatus() == Order.DELIVERY || order.getStatus() == Order.FINISH))
					deliveryItem(order);
				else if((old_order.getStatus() == Order.DELIVERY || old_order.getStatus() == Order.FINISH) && order.getStatus() == Order.PENDING)
					returnItem(order);
			}
		}
		
		OrderChannel channel = new OrderChannel();
		if(order.getSale_channel() == null || order.getSale_channel().isEmpty())
			channel.setName(OrderChannel.DEFAULT_CHANNEL);
		else
			channel.setName(order.getSale_channel());
		ofy().save().entity(channel);

		return rtn;
	}
	
	@Override
	public ListOrder getOrders(String cursor) {
		List<Order> result = new ArrayList<Order>();

		Query<Order> query = ofy().load().type(Order.class).order("-create_date")
				.limit(ListOrder.pageSize);
		if (cursor != null)
			query = query.startAt(Cursor.fromWebSafeString(cursor));

		boolean continu = false;
		QueryResultIterator<Order> iterator = query.iterator();
		while (iterator.hasNext()) {
			Order order = iterator.next();
			result.add(order);
			continu = true;
		}

		List<EntitiesSize> entitiesSizes = ofy().load()
				.type(EntitiesSize.class).list();

		if (continu) {
			Cursor cur = iterator.getCursor();
			String nextCur = cur.toWebSafeString();
			ListOrder listOrder = new ListOrder(result, nextCur, entitiesSizes
					.get(0).getOrder_size());
			return listOrder;
		} else {
			ListOrder listOrder = new ListOrder(result, "\\0", entitiesSizes
					.get(0).getOrder_size());
			return listOrder;
		}
	}

	@Override
	public ListOrder getOrdersByStatus(String cursor, int status) {
		List<Order> result = new ArrayList<Order>();

		Query<Order> query = ofy().load().type(Order.class).filter("status", status)
				.limit(ListOrder.pageSize);
		if (cursor != null)
			query = query.startAt(Cursor.fromWebSafeString(cursor));

		boolean continu = false;
		QueryResultIterator<Order> iterator = query.iterator();
		while (iterator.hasNext()) {
			Order order = iterator.next();
			result.add(order);
			continu = true;
		}
		
		List<EntitiesSize> entitiesSizes = ofy().load()
				.type(EntitiesSize.class).list();

		if (continu) {
			Cursor cur = iterator.getCursor();
			String nextCur = cur.toWebSafeString();
			ListOrder listOrder = new ListOrder(result, nextCur, 0);
			if(status == Order.PENDING)
				listOrder.setTotal(entitiesSizes.get(0).getOrder_pending_size());
			if(status == Order.DELIVERY)
				listOrder.setTotal(entitiesSizes.get(0).getOrder_delivery_size());
			if(status == Order.FINISH)
				listOrder.setTotal(entitiesSizes.get(0).getOrder_finish_size());
			return listOrder;
		} else {
			ListOrder listOrder = new ListOrder(result, "\\0", 0);
			if(status == Order.PENDING)
				listOrder.setTotal(entitiesSizes.get(0).getOrder_pending_size());
			if(status == Order.DELIVERY)
				listOrder.setTotal(entitiesSizes.get(0).getOrder_delivery_size());
			if(status == Order.FINISH)
				listOrder.setTotal(entitiesSizes.get(0).getOrder_finish_size());
			return listOrder;
		}
	}

	@Override
	public ListOrder getOrdersByCustomer(Customer customer) {
		Collection<Order> orders = ofy().load().type(Order.class)
				.ids(customer.getOrder_ids()).values();
		ListOrder rtn = new ListOrder
				(new ArrayList<Order>(orders), "", orders.size());
		return rtn;
	}

	@Override
	public boolean deleteOrder(Long id) {
		Order order = ofy().load().type(Order.class).id(id).now();
		if (order != null) {
			ofy().delete().entity(order);

			List<EntitiesSize> entitiesSizes = ofy().load()
					.type(EntitiesSize.class).list();
			EntitiesSize es = entitiesSizes.get(0);
			es.setOrder_size(es.getOrder_size() - 1);
			removeOrderStatusSize(es, order);
			ofy().save().entity(es);
			
			deleteCustomerOrder(order.getCustomer().getPhone(), id);
			
			if(order.getStatus() == Order.DELIVERY)
				returnItem(order);

			return true;
		}
		
		return false;
	}

	@Override
	public void updateOrderStatus(Long order_id, int status) {
		Order order = ofy().load().type(Order.class).id(order_id).now();
		if (order != null) {
			int old_status = order.getStatus();
			
			order.setStatus(status);
			if(status == Order.FINISH && order.getFinish_date() == null)
				order.setFinish_date(getCurrentDate());
			ofy().save().entity(order);
			
			List<EntitiesSize> entitiesSizes = ofy().load()
					.type(EntitiesSize.class).list();
			EntitiesSize es = entitiesSizes.get(0);
			Order o = new Order();
			o.setStatus(old_status);
			removeOrderStatusSize(es, o);
			addOrderStatusSize(es, order);
			ofy().save().entity(es);
			
			if(old_status == Order.PENDING && (status == Order.DELIVERY || status == Order.FINISH))
				deliveryItem(order);
			else if((old_status == Order.DELIVERY || old_status == Order.FINISH) && status == Order.PENDING)
				returnItem(order);			
		}
	}

	@Override
	public List<OrderChannel> getChannels() {
		List<OrderChannel> rtn = new ArrayList<OrderChannel>();
		List<OrderChannel> channels = ofy().load().type(OrderChannel.class).list(); 
		rtn.addAll(channels);
		return rtn;
	}
	
	@Override
	public ListCustomer getCustomers(String cursor) {
		List<Customer> result = new ArrayList<Customer>();

		Query<Customer> query = ofy().load().type(Customer.class)
				.limit(ListCustomer.pageSize);
		if (cursor != null)
			query = query.startAt(Cursor.fromWebSafeString(cursor));

		boolean continu = false;
		QueryResultIterator<Customer> iterator = query.iterator();
		while (iterator.hasNext()) {
			Customer customer = iterator.next();
			result.add(customer);
			continu = true;
		}

		List<EntitiesSize> entitiesSizes = ofy().load()
				.type(EntitiesSize.class).list();

		if (continu) {
			Cursor cur = iterator.getCursor();
			String nextCur = cur.toWebSafeString();
			ListCustomer listCustomer = new ListCustomer(result, nextCur, entitiesSizes.get(0).getCustomer_size());
			return listCustomer;
		} else {
			ListCustomer listCustomer = new ListCustomer(result, "\\0", entitiesSizes.get(0).getCustomer_size());
			return listCustomer;
		}
	}

	@Override
	public ListCustomer searchCustomer(String search_string) {
		IndexSpec indexSpec = IndexSpec.newBuilder().setName("CustomerDocument").build(); 
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
				  				
		String queryString = "name : " + search_string;
        Results<ScoredDocument> results = index.search(queryString);
        
        List<Long> item_matched_id = new ArrayList<Long>();
        for (ScoredDocument document : results) {
            Long id = Long.valueOf(document.getId());
            item_matched_id.add(id);
        }
		
		List<Customer> result = new ArrayList<Customer>();
		Collection<Customer> query = ofy().load().type(Customer.class)
				.ids(item_matched_id).values();
		result.addAll(query);
		
		ListCustomer listCustomer = new ListCustomer(result, "", result.size());
		return listCustomer;
	}

	@Override
	public Sale createSale(Sale sale) {
		Sale rtn = null;
		Sale old_sale = null;
		if(sale.getId() != null)
			old_sale = ofy().load().type(Sale.class).id(sale.getId()).now();
		
		if(old_sale == null) {
			Calendar c = Calendar.getInstance(); 
			c.setTime(sale.getTo()); 
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.add(Calendar.DATE, 1);
			Date expire_date = c.getTime();
			if(getCurrentDate().after(sale.getFrom()) && getCurrentDate().before(expire_date))
				sale.setStatus(Sale.ACTIVE);
			else
				sale.setStatus(Sale.DEACTIVE);
		}

		Key<Sale> key = ofy().save().entity(sale).now();
		rtn = ofy().load().key(key).now();

		if(old_sale == null && sale.getStatus() == Sale.ACTIVE) {
			activeSaleItems(rtn);
		}
		else if(old_sale != null && old_sale.getStatus() != sale.getStatus()) {
			if(sale.getStatus() == Sale.ACTIVE)
				activeSaleItems(rtn);
			else
				deactiveSaleItems(rtn);
		}

		return rtn;
	}

	@Override
	public List<Sale> getSales() {
		List<Sale> rtn = new ArrayList<Sale>();
		List<Sale> sales = ofy().load().type(Sale.class).list();
		rtn.addAll(sales);
		return rtn;
	}

	@Override
	public void deleteSale(Long id) {
		Sale sale = ofy().load().type(Sale.class).id(id).now();
		if(sale != null) {
			ofy().delete().entity(sale);
			deactiveSaleItems(sale);
		}
	}

}
