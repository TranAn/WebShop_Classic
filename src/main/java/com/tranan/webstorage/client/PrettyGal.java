package com.tranan.webstorage.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.tranan.webstorage.client.ui.Cart;
import com.tranan.webstorage.client.ui.Contact;
import com.tranan.webstorage.client.ui.Footer;
import com.tranan.webstorage.client.ui.Header;
import com.tranan.webstorage.client.ui.HomeCover;
import com.tranan.webstorage.client.ui.Item;
import com.tranan.webstorage.client.ui.Shop;

public class PrettyGal implements EntryPoint {
	
	

	@Override
	public void onModuleLoad() {
		RootPanel.get().add(new Header());
		RootPanel.get().add(new HomeCover());
		RootPanel.get().add(new Shop());
//		RootPanel.get().add(new Item());
//		RootPanel.get().add(new Contact());
//		RootPanel.get().add(new Cart());
		RootPanel.get().add(new Footer());
	}

}
