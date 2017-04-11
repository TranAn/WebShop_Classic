package com.tranan.webstorage.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.tranan.webstorage.client.place.HomePlace;
import com.tranan.webstorage.client.ui.Footer;
import com.tranan.webstorage.client.ui.Header;
import com.tranan.webstorage.shared.DataService;
import com.tranan.webstorage.shared.DataServiceAsync;

public class PrettyGal implements EntryPoint {
	
	public static final DataServiceAsync dataService = GWT
			.create(DataService.class);
	
	public static PlaceController placeController;
	public static UIController UIC = new UIController();
	public static HTMLPanel controlPage = new HTMLPanel("");
	
	private void handlerHistory() {
		EventBus eventBus = new SimpleEventBus();
		placeController = new PlaceController(eventBus);
	
		// Start ActivityManager for the main widget with our ActivityMapper
		ActivityMapper activityMapper = new AppActivityMapper();
		ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		activityManager.setDisplay(new SimplePanel());
	
		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		AppHistoryMapper historyMapper = GWT.create(AppHistoryMapper.class);
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(placeController, eventBus, new HomePlace());
		historyHandler.handleCurrentHistory();
	}

	@Override
	public void onModuleLoad() {
		controlPage.getElement().setAttribute("style", "overflow: hidden; margin-top: 75px;");
		
		RootPanel.get().add(new Header());
		RootPanel.get().add(controlPage);		
		RootPanel.get().add(new Footer());
		
		handlerHistory();
	}
	
	public static String integerToPriceString(Long number) {
		boolean isZeroLess = false;
		
		if(number == 0)
			return "0";
		if(number < 0) {
			number = -number;
			isZeroLess = true;
		}
		
		String rtn = "";
		List<String> p = new ArrayList<String>();
		while (number > 0) {
			String s = String.valueOf(number % 1000);
			number = number / 1000;
			if (number > 0)
				while (s.length() < 3)
					s = "0" + s;
			p.add(s);
		}
		for (int i = 0; i < p.size(); i++) {
			if (i == 0)
				rtn = p.get(i);
			else
				rtn = p.get(i) + "." + rtn;
		}
		
		if(isZeroLess)
			return "- " + rtn;
		else
			return rtn;
	}

}
