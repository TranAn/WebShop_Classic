package com.tranan.webstorage.client_admin.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.Ruler;
import com.tranan.webstorage.client_admin.place.CreateOrderPlace;
import com.tranan.webstorage.client_admin.place.CreateSalePlace;
import com.tranan.webstorage.shared.Order;

public class SaleTable extends Composite {

	private static SaleTableUiBinder uiBinder = GWT
			.create(SaleTableUiBinder.class);

	interface SaleTableUiBinder extends UiBinder<Widget, SaleTable> {
	}
	
	@UiField
	ScrollPanel scrollTable;

	public SaleTable() {
		initWidget(uiBinder.createAndBindUi(this));
		
		scrollTable.setHeight(Ruler.ItemTable_H + "px");
	}
	
	@UiHandler("addSaleButton")
	void onAddSaleButtonClick(ClickEvent e) {
		PrettyGal.placeController.goTo(new CreateSalePlace("", null));
	}

}
