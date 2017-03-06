package com.tranan.webstorage.client_admin.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class DatePickerDialog extends DialogBox {

	private static DatePickerDialogUiBinder uiBinder = GWT
			.create(DatePickerDialogUiBinder.class);

	interface DatePickerDialogUiBinder extends
			UiBinder<Widget, DatePickerDialog> {
	}
	
	@UiField
	HTMLPanel dialog;
	
	float opacity;
	Timer t;
	
	private void faddingShowDialog() {
		opacity = 0f;
		final String width = "70px";
		dialog.getElement().setAttribute("style", "opacity: 0; width: " + width);
		dialog.addStyleName("dialog_show");
		t = new Timer() {

			@Override
			public void run() {
				opacity = opacity + 0.25f;
				dialog.getElement().setAttribute("style",
						"opacity:" + String.valueOf(opacity) + "; width: " + width);

				if (opacity == 1)
					t.cancel();
			}
		};
		t.scheduleRepeating(50);
	}

	public DatePickerDialog() {
		setWidget(uiBinder.createAndBindUi(this));
		
		setGlassEnabled(false);
		setAutoHideEnabled(true);
		setAnimationEnabled(false);
		setModal(false);
		setStyleName("");
		
		this.addAttachHandler(new Handler() {

			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached())
					faddingShowDialog();
			}
		});
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
