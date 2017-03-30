package com.tranan.webstorage.client_admin;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.tranan.webstorage.client_admin.dialog.ConfirmDialog;
import com.tranan.webstorage.client_admin.dialog.ConfirmDialog.ConfirmDialog_Listener;
import com.tranan.webstorage.client_admin.place.CreateItemPlace;
import com.tranan.webstorage.client_admin.place.CreateOrderPlace;
import com.tranan.webstorage.client_admin.place.CreateSalePlace;
import com.tranan.webstorage.client_admin.place.ItemPlace;
import com.tranan.webstorage.client_admin.ui.LeftMenu;
import com.tranan.webstorage.client_admin.ui.LoginPage;
import com.tranan.webstorage.client_admin.ui.RightPage;
import com.tranan.webstorage.shared.DataService;
import com.tranan.webstorage.shared.DataServiceAsync;

public class PrettyGal implements EntryPoint {

	public static final DataServiceAsync dataService = GWT
			.create(DataService.class);
	
	public static PlaceController placeController;
	public static LoginPage loginPage = new LoginPage();
	public static LeftMenu slideMenu = new LeftMenu();
	public static RightPage controlPage = new RightPage();
	public static UIController UIC = new UIController();
	
	public static final String ERROR_MSG = "Kết nối đến server thất bại, vui lòng kiểm tra lại đường truyền";
	public static final String AUTHORIZE_ERROR_MSG = "Tài khoản hết hạn";

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
		historyHandler.register(placeController, eventBus, new ItemPlace());
		historyHandler.handleCurrentHistory();
	}

	@Override
	public void onModuleLoad() {
		RootPanel.get().setHeight(Window.getClientHeight() + "px");
		
		RootPanel.get().add(loginPage);
//		RootPanel.get().add(slideMenu);
//		RootPanel.get().add(controlPage);
		
		handlerHistory();
	}
	
	public static void confirmChangePlace(final Place place) {
		boolean isConfirm = false;
		if (placeController.getWhere() instanceof CreateItemPlace) {
			if(AppActivityMapper.current_createitem_ui.isItemChange())
				isConfirm = true;
		}
		else if (placeController.getWhere() instanceof CreateOrderPlace) {
			if(AppActivityMapper.current_createorder_ui.isItemChange())
				isConfirm = true;
		}
		else if (placeController.getWhere() instanceof CreateSalePlace) {
			if(AppActivityMapper.current_createsale_ui.isItemChange())
				isConfirm = true;
		}
		
		if(isConfirm) {
			final ConfirmDialog dialog = new ConfirmDialog("Bạn muốn thoát khi chưa lưu thay đổi?", 
					new ConfirmDialog_Listener() {
				
				@Override
				public void onConfirmClick() {
					placeController.goTo(place);
				}
				
				@Override
				public void onCancelClick() {
					// TODO Auto-generated method stub
					
				}
			});
			Timer t = new Timer() {

				@Override
				public void run() {
					dialog.center();					
				}};
			t.schedule(50);
		}
		else
			placeController.goTo(place);
	}
	
	public static void onAuthSuccess() {
		RootPanel.get().clear();
		RootPanel.get().add(slideMenu);
		RootPanel.get().add(controlPage);
	}
	
	public static void onAuthFail() {
		RootPanel.get().clear();
		LoginPage.clearUser();
		RootPanel.get().add(loginPage);
		Timer t = new Timer(){
			@Override
			public void run() {
				LoginPage.openLoginForm();
			}};
		t.schedule(600);
	}
	
	public static void setPriceLongBox(final LongBox ltxb) {
		ltxb.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(!event.isLeftArrow() && !event.isRightArrow() && event.getNativeKeyCode() != 8) {
					Long value = Long.valueOf(ltxb.getText().replaceAll("[.]", ""));
					ltxb.setText(PrettyGal.integerToPriceString(value));
				}
			}
		});
		
		ltxb.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char ch = event.getCharCode();
				if(ch != '0' && ch != '1' && ch != '2' && ch != '3' && ch != '4' && ch != '5'
						&& ch != '6' && ch != '7' && ch != '8' && ch != '9') {
					ltxb.cancelKey();
				}
			}
		});
		ltxb.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				Long value = Long.valueOf(ltxb.getText().replaceAll("[.]", ""));
				ltxb.setText(PrettyGal.integerToPriceString(value));
			}
		});
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
	
	public static String phoneFormat(String phone) {
		if(phone.equals("1"))
			return "";
		else
			return phone.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "$1 - $2 - $3");
	}

}
