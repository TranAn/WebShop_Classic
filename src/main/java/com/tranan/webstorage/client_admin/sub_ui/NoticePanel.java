package com.tranan.webstorage.client_admin.sub_ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class NoticePanel extends Composite {

	private static NoticePanelUiBinder uiBinder = GWT
			.create(NoticePanelUiBinder.class);

	interface NoticePanelUiBinder extends UiBinder<Widget, NoticePanel> {
	}

	@UiField
	static Image loadImg;
	@UiField
	static HTMLPanel successImg;
	@UiField
	static HTMLPanel failImg;
	@UiField
	static Label noticeLabel;

	static boolean state;
	static Timer t;

	public NoticePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	static void clearState() {
		loadImg.setVisible(false);
		successImg.setVisible(false);
		failImg.setVisible(false);
		noticeLabel.setText("");
		if (t != null)
			t.cancel();
	}

	public static void onLoading() {
		clearState();
		loadImg.setVisible(true);
		noticeLabel.setText("Đang tải...");
		noticeLabel.setStyleName("NoticePanel_loading1");
		state = false;
		t = new Timer() {

			@Override
			public void run() {
				state = !state;
				if (state)
					noticeLabel.setStyleName("NoticePanel_loading2");
				else
					noticeLabel.setStyleName("NoticePanel_loading1");
			}
		};
		t.scheduleRepeating(750);
	}

	public static void endLoading() {
		clearState();
	}

	public static void successNotice(String notice) {
		clearState();
		successImg.setVisible(true);
		noticeLabel.setText(notice);
		noticeLabel.setStyleName("NoticePanel_successNotice");
		t = new Timer() {

			@Override
			public void run() {
				clearState();
			}
		};
		t.schedule(4000);
	}
	
	public static void failNotice(String notice) {
		clearState();
		failImg.setVisible(true);
		noticeLabel.setText(notice);
		noticeLabel.setStyleName("NoticePanel_failNotice");
		t = new Timer() {

			@Override
			public void run() {
				clearState();
			}
		};
		t.schedule(4000);
	}

}
