package com.tranan.webstorage.client_admin.sub_ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmDialog extends DialogBox {

	private static ConfirmDialogUiBinder uiBinder = GWT
			.create(ConfirmDialogUiBinder.class);

	interface ConfirmDialogUiBinder extends UiBinder<Widget, ConfirmDialog> {
	}

	public ConfirmDialog() {
		setWidget(uiBinder.createAndBindUi(this));
		setGlassEnabled(true);
		setAutoHideEnabled(true);
		setStyleName("ConfirmDialog_s1");
	}
	
	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		switch (event.getTypeInt()) {
		case Event.ONKEYDOWN:
			if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
				hide();
			}
			break;
		}
	}

}
