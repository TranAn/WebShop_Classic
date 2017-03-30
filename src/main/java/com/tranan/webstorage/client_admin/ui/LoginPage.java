package com.tranan.webstorage.client_admin.ui;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;

public class LoginPage extends Composite {

	private static LoginPageUiBinder uiBinder = GWT
			.create(LoginPageUiBinder.class);

	interface LoginPageUiBinder extends UiBinder<Widget, LoginPage> {
	}

	@UiField 
	HTMLPanel panel;
	@UiField
	Label noticeLabel;
	@UiField
	static
	HTMLPanel authLabelPanel;
	static
	@UiField
	HTMLPanel errorLabelPanel;
	static
	@UiField
	HTMLPanel authErrorLabelPanel;

	Timer t;
	boolean state;
	
	public static String id_token = "";
	public static String user_name = "";
	public static String user_photo = "";
	
	public static boolean isLoginFormOpen = false;

	@SuppressWarnings("unchecked")
	public LoginPage() {
		initWidget(uiBinder.createAndBindUi(this));

		t = new Timer() {

			@Override
			public void run() {
				state = !state;
				if (state)
					noticeLabel.setStyleName("loginPage_s1");
				else
					noticeLabel.setStyleName("loginPage_s2");
			}
		};
		t.scheduleRepeating(750);

		ScriptInjector.fromUrl("https://apis.google.com/js/api.js")
				.setCallback(new Callback() {
					
			@Override
			public void onSuccess(Object result) {
				handleClientLoad();
			}
			
			@Override
			public void onFailure(Object reason) {
				// TODO Auto-generated method stub
			}		
		}).inject();
	}
	
	public void setPageSize() {
		panel.setHeight(Window.getClientHeight() + "px");
	}
	
	public static void clearUser() {
		id_token = "";
		user_name = "";
		user_photo = "";
	}
	
	public static void openLoginForm() {
		if(!isLoginFormOpen) {
			isLoginFormOpen = true;
			openForm();
		}
		authLabelPanel.setVisible(false);
	}
	
	public static void onAuth() {
		authLabelPanel.setVisible(true);
		errorLabelPanel.setVisible(false);
		authErrorLabelPanel.setVisible(false);
	}
	
	public static void updateSigninInfo(String idToken, String displayName, String avatarUrl) {
		id_token = idToken;
		user_name = displayName;
		user_photo = avatarUrl;	
		
		PrettyGal.dataService.checkAuth(id_token, new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				if(result) {
					PrettyGal.onAuthSuccess();
				}
				else {
					authLabelPanel.setVisible(false);
					authErrorLabelPanel.setVisible(true);
					handleSignoutClick();
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				authLabelPanel.setVisible(false);
				errorLabelPanel.setVisible(true);
			}
		});
	}

	public static native void openForm() /*-{
		[].map.call($wnd.document.querySelectorAll('.profile'), function(el) {
			el.classList.toggle('profile--open');
		});
	}-*/;
	
	public static native void handleClientLoad() /*-{
		// Load the API client and auth2 library
        gapi.load('client:auth2', initClient);
        
        function initClient() {
	        gapi.client.init({
	            apiKey: 'AIzaSyADcfVBhkVAAXWKbRhHYcgqj8jzlb332UE',
	            discoveryDocs: ["https://people.googleapis.com/$discovery/rest?version=v1"],
	            clientId: '860697312740-pcj9nst3n3mhjer0klhel44aeploonia.apps.googleusercontent.com',
	            scope: 'profile'
	        }).then(function () {
	          	// Listen for sign-in state changes.
	          	gapi.auth2.getAuthInstance().isSignedIn.listen(updateSigninStatus);
	          	// Handle the initial sign-in state.
	          	updateSigninStatus(gapi.auth2.getAuthInstance().isSignedIn.get());	  
	        });
	    }
	    
	    function updateSigninStatus(isSignedIn) {
	        if (isSignedIn) {
	        	@com.tranan.webstorage.client_admin.ui.LoginPage::onAuth()();
	        	
//	        	console.log(gapi.auth2.getAuthInstance().currentUser.get().getAuthResponse());
	        	var id_token = gapi.auth2.getAuthInstance().currentUser.get().getAuthResponse().id_token;
		        
		        gapi.client.people.people.get({
		        	resourceName: 'people/me'
		        }).then(function(resp) {	
//		        	console.log(resp.result);
		        	var displayName = resp.result.names[0].displayName;	
		        	var photo = resp.result.photos[0].url;
		        	
		        	@com.tranan.webstorage.client_admin.ui.LoginPage::updateSigninInfo(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(id_token,displayName,photo);
		        });
	        } else {
		        @com.tranan.webstorage.client_admin.ui.LoginPage::openLoginForm()();
	        }
      	}
	}-*/;
	
	public static native void handleAuthClick() /*-{
		gapi.auth2.getAuthInstance().signIn();
	}-*/;
	
	public static native void handleSignoutClick() /*-{
		gapi.auth2.getAuthInstance().signOut();
	}-*/;

	@UiHandler("loginBtn")
	void onLoginButtonClick(ClickEvent e) {
		handleAuthClick();
	}
}
