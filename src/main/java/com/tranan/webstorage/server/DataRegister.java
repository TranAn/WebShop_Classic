package com.tranan.webstorage.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.ObjectifyService;
import com.tranan.webstorage.shared.Catalog;
import com.tranan.webstorage.shared.Customer;
import com.tranan.webstorage.shared.EntitiesSize;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Order;
import com.tranan.webstorage.shared.OrderChannel;
import com.tranan.webstorage.shared.OrderIn;
import com.tranan.webstorage.shared.OrderTrack;
import com.tranan.webstorage.shared.Photo;
import com.tranan.webstorage.shared.Sale;
import com.tranan.webstorage.shared.User;

@SuppressWarnings("serial")
public class DataRegister extends RemoteServiceServlet {
	
	public DataRegister() {
		super();

		ObjectifyService.register(EntitiesSize.class);
		ObjectifyService.register(Item.class);
		ObjectifyService.register(Catalog.class);
		ObjectifyService.register(Customer.class);
		ObjectifyService.register(Order.class);
		ObjectifyService.register(OrderIn.class);
		ObjectifyService.register(OrderChannel.class);
		ObjectifyService.register(Photo.class);
		ObjectifyService.register(Sale.class);
		ObjectifyService.register(OrderTrack.class);
		ObjectifyService.register(User.class);
//		ObjectifyService.register(Shop.class);
		
		if(ofy().load().type(EntitiesSize.class).list().isEmpty()) {	
			EntitiesSize entitiesSize = new EntitiesSize();
			ofy().save().entity(entitiesSize);
		}
		
		User user = new User();
		user.setEmail("antp17290@gmail.com");
		ofy().save().entity(user);
		
		OrderChannel web_channel = new OrderChannel();
		web_channel.setName("Website");
		ofy().save().entity(web_channel);
	}

}
