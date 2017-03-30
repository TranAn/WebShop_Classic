package com.tranan.webstorage.client_admin.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmDialog extends DialogBox {

	private static ConfirmDialogUiBinder uiBinder = GWT
			.create(ConfirmDialogUiBinder.class);

	interface ConfirmDialogUiBinder extends UiBinder<Widget, ConfirmDialog> {
	}

	@UiField
	HTMLPanel dialog;
	@UiField 
	Label confirmLabel;

	float opacity;
	Timer t;
	
	private ConfirmDialog_Listener listener;
	
	public interface ConfirmDialog_Listener {
		void onConfirmClick();
		void onCancelClick();
	}

	public ConfirmDialog(String message, ConfirmDialog_Listener listener) {
		setWidget(uiBinder.createAndBindUi(this));
		
		confirmLabel.setText(message);
		this.listener = listener;

		setGlassEnabled(true);
		setAutoHideEnabled(true);
		setAnimationEnabled(false);
		setStyleName("ConfirmDialog_s1");

		this.addAttachHandler(new Handler() {

			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached())
					faddingShowDialog();
			}
		});
	}

	void faddingShowDialog() {
		opacity = 0f;
		final String width = "550px";
		dialog.getElement()
				.setAttribute("style", "opacity: 0; width: " + width);
		t = new Timer() {

			@Override
			public void run() {
				opacity = opacity + 0.25f;
				dialog.getElement().setAttribute(
						"style",
						"opacity:" + String.valueOf(opacity) + "; width: "
								+ width);

				if (opacity == 1)
					t.cancel();
			}
		};
		t.scheduleRepeating(50);
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent e) {
		listener.onConfirmClick();
		hide();
	}
	
	@UiHandler("exitButton")
	void onExitButtonClick(ClickEvent e) {
		listener.onCancelClick();
		hide();
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
