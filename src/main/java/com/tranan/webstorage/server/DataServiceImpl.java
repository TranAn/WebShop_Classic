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
import com.tranan.webstorage.shared.EntitiesSize;
import com.tranan.webstorage.shared.DataService;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Item.Type;
import com.tranan.webstorage.shared.ListCustomer;
import com.tranan.webstorage.shared.ListItem;
import com.tranan.webstorage.shared.ListOrder;
import com.tranan.webstorage.shared.Order;
import com.tranan.webstorage.shared.OrderChannel;
import com.tranan.webstorage.shared.OrderIn;
import com.tranan.webstorage.shared.Photo;

@SuppressWarnings("serial")
public class DataServiceImpl extends RemoteServiceServlet implements
		DataService {

	private BlobstoreService blobStoreService = BlobstoreServiceFactory
			.getBlobstoreService();
	
	public static String removeAccent(String s) {		
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
				.setId(customer.getId().toString())
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
	
	private Date getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, 7);
		return cal.getTime();
	}

	@Override
	public Item getItemById(Long id) {
		Item item = ofy().load().type(Item.class).id(id).now();
		return item;
	}

	@Override
	public Item createItem(Item item) {
		Item rtn = null;
		List<Long> attach_catalog_ids = new ArrayList<Long>();
		List<Long> detach_catalog_ids = new ArrayList<Long>();

		if (item.getId() != null) {
			Item i = ofy().load().type(Item.class).id(item.getId()).now();
			if (i == null) {
				List<EntitiesSize> entitiesSizes = ofy().load()
						.type(EntitiesSize.class).list();
				EntitiesSize es = entitiesSizes.get(0);
				es.setItem_size(es.getItem_size() + 1);
				ofy().save().entity(es);

				attach_catalog_ids.addAll(item.getCatalog_ids());
			} else {
				for (Long id : item.getCatalog_ids())
					if (!i.getCatalog_ids().contains(id))
						attach_catalog_ids.add(id);
				for (Long id : i.getCatalog_ids())
					if (!item.getCatalog_ids().contains(id))
						detach_catalog_ids.add(id);
			}
		} else {
			List<EntitiesSize> entitiesSizes = ofy().load()
					.type(EntitiesSize.class).list();
			EntitiesSize es = entitiesSizes.get(0);
			es.setItem_size(es.getItem_size() + 1);
			ofy().save().entity(es);

			attach_catalog_ids.addAll(item.getCatalog_ids());
		}

		Key<Item> key = ofy().save().entity(item).now();
		rtn = ofy().load().key(key).now();

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
		
		if (order.getId() != null) {
			Order o = ofy().load().type(Order.class).id(order.getId()).now();
			if (o == null) {
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
				if(order.getStatus() != o.getStatus()) {
					List<EntitiesSize> entitiesSizes = ofy().load()
							.type(EntitiesSize.class).list();
					EntitiesSize es = entitiesSizes.get(0);
					removeOrderStatusSize(es, o);
					addOrderStatusSize(es, order);
					ofy().save().entity(es);
					
					if(o.getStatus() == Order.PENDING && (order.getStatus() == Order.DELIVERY || order.getStatus() == Order.FINISH))
						deliveryItem(order);
					else if((o.getStatus() == Order.DELIVERY || o.getStatus() == Order.FINISH) && order.getStatus() == Order.PENDING)
						returnItem(order);
				}
			}
		} else {
			List<EntitiesSize> entitiesSizes = ofy().load()
					.type(EntitiesSize.class).list();
			EntitiesSize es = entitiesSizes.get(0);
			es.setOrder_size(es.getOrder_size() + 1);
			addOrderStatusSize(es, order);
			ofy().save().entity(es);
			
			if(order.getStatus() == Order.DELIVERY || order.getStatus() == Order.FINISH)
				deliveryItem(order);
		}
		
		if(order.getCustomer().getId() == null)
			order.getCustomer().setId(createCustomer(order.getCustomer(), null));
		if(order.getCustomer().getId() == null)
			order.setCustomer(null);

		Key<Order> key = ofy().save().entity(order).now();
		rtn = ofy().load().key(key).now();
		
		if(rtn.getCustomer() != null)
			createCustomer(rtn.getCustomer(), rtn.getId());
		
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
			
			Customer customer = ofy().load().type(Customer.class)
					.id(order.getCustomer().getId()).now();
			if(customer != null) {
				customer.getOrder_ids().remove(order.getId());
				ofy().save().entity(customer);
			}
			
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

	public Long createCustomer(Customer customer, Long order_id) {
		if(customer.getId() == null) {
			if(!customer.isEmpty()) {
				if(order_id != null && !customer.getOrder_ids().contains(order_id))
					customer.getOrder_ids().add(order_id);
				Key<Customer> key = ofy().save().entity(customer).now();
				Customer rtn = ofy().load().key(key).now();
				
				createCustomerDocument(rtn);
				
				List<EntitiesSize> entitiesSizes = ofy().load()
						.type(EntitiesSize.class).list();
				EntitiesSize es = entitiesSizes.get(0);
				es.setCustomer_size(es.getCustomer_size() + 1);
				ofy().save().entity(es);
			
				return rtn.getId();
			}
			else
				return null;
		}
		else {
			if(order_id != null && !customer.getOrder_ids().contains(order_id))
				customer.getOrder_ids().add(order_id);
			ofy().save().entity(customer).now();
			
			return customer.getId();
		}
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

}
