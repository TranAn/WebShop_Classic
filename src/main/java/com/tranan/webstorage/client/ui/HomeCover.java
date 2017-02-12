package com.tranan.webstorage.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class HomeCover extends Composite {

	private static HomeCoverUiBinder uiBinder = GWT
			.create(HomeCoverUiBinder.class);

	interface HomeCoverUiBinder extends UiBinder<Widget, HomeCover> {
	}
	
	@UiField Label mid_box_label;

	public HomeCover() {
		initWidget(uiBinder.createAndBindUi(this));
		
		mid_box_label.setText("FALL & WINTER");
	}

}
