package com.tranan.webstorage.client_admin.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.Ruler;
import com.tranan.webstorage.shared.Sale;

public class CreateSale extends Composite {

	private static CreateSaleUiBinder uiBinder = GWT
			.create(CreateSaleUiBinder.class);

	interface CreateSaleUiBinder extends UiBinder<Widget, CreateSale> {
	}
	
	@UiField ScrollPanel scroll;
	@UiField DateBox saleFrom;
	@UiField DateBox saleTo;
	@UiField CheckBox activeBox;
	@UiField CheckBox deactiveBox;
	@UiField Label activeBoxText;
	@UiField Label deactiveBoxText;
	
	private int sale_status;

	public CreateSale() {
		initWidget(uiBinder.createAndBindUi(this));
		
		scroll.setHeight(Ruler.ItemTable_H + "px");
		
		saleFrom.setFormat(new DateBox.DefaultFormat 
				 (DateTimeFormat.getFormat("dd - MM - yyyy"))); 
		saleTo.setFormat(new DateBox.DefaultFormat 
				 (DateTimeFormat.getFormat("dd - MM - yyyy"))); 
		
		activeBox.setValue(true);
		activeBoxText.addStyleName("CreateSale_active");
		sale_status = Sale.ACTIVE;
	}
	
	public void setSale(Sale sale) {
		
	}
	
	public boolean isItemChange() {
		return false;
	}

}
