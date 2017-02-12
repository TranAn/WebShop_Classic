package com.tranan.webstorage.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.ObjectifyService;
import com.tranan.webstorage.shared.*;

import static com.googlecode.objectify.ObjectifyService.ofy;

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
//		ObjectifyService.register(Shop.class);
//		ObjectifyService.register(User.class);

		if(ofy().load().type(EntitiesSize.class).list().isEmpty()) {	
			EntitiesSize entitiesSize = new EntitiesSize();
			ofy().save().entity(entitiesSize);
		}
	}

}
