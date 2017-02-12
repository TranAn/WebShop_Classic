package com.tranan.webstorage.client_admin.sub_ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CategorieTable_Row extends Composite {

	private static CategorieTable_RowUiBinder uiBinder = GWT
			.create(CategorieTable_RowUiBinder.class);

	interface CategorieTable_RowUiBinder extends
			UiBinder<Widget, CategorieTable_Row> {
	}

	@UiField
	Label text;
	@UiField
	HTMLPanel row;

	public CategorieTable_Row(String name) {
		initWidget(uiBinder.createAndBindUi(this));

		text.setText(name);
	}

	public void setActive() {
		row.setStyleName("CategorieTable_s1_actived");
	}

}
