package com.tranan.webstorage.client_admin.sub_ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Item.Type;
import com.tranan.webstorage.shared.OrderIn;

public class OrderInTable_Row extends Composite {

	private static OrderInTable_RowUiBinder uiBinder = GWT
			.create(OrderInTable_RowUiBinder.class);

	interface OrderInTable_RowUiBinder extends
			UiBinder<Widget, OrderInTable_Row> {
	}
			
	@UiField HTMLPanel itemsCol;	
	@UiField Label orderSum;
	@UiField Label orderCreateDate;
			
	private OrderIn orderIn;

	private void addItemCol(Item item) {
		HTMLPanel panel1 = new HTMLPanel("");
		panel1.setStyleName("orderInItem_row");
		
		HTMLPanel panel2 = new HTMLPanel("");
		panel2.setStyleName("orderInItem_col1");
		Image img = new Image();
		img.setSize("30px", "30px");
		img.setUrl(item.getAvatar_url());
		panel2.add(img);
		
		Label lb1 = new Label(item.getName());
		lb1.setStyleName("orderInItem_col2");
		
		Label lb2 = new Label();
		lb2.setStyleName("orderInItem_col4");
		lb2.setText(PrettyGal.integerToPriceString(item.getCost()));
		
		HTMLPanel itemTypes = new HTMLPanel("");
		itemTypes.setStyleName("orderInItem_col3");
		for(Type t: item.getType()) {
			Label item_type = new Label();
			item_type.setStyleName("orderInItem_col3_s1");
			if(t.getName().equals(Item.DEFAULT_TYPE))
				item_type.setText("SL: " + t.getQuantity());
			else	
				item_type.setText(t.getName()+ ": " + t.getQuantity());
			
			itemTypes.add(item_type);
		}
		
		panel1.add(panel2);
		panel1.add(lb1);
		panel1.add(lb2);
		panel1.add(itemTypes);
		itemsCol.add(panel1);
	}

	public OrderInTable_Row() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setOrder(OrderIn orderIn) {
		this.orderIn = orderIn;
		
		itemsCol.clear();
		Long orderInValues = 0L;
		for(Item item: orderIn.getOrder_items()) {
			addItemCol(item);
			for(Type t: item.getType())
				orderInValues = orderInValues + (t.getQuantity() * item.getCost());
		}
		
		orderSum.setText(PrettyGal.integerToPriceString(orderInValues));
		
		if(orderIn.getCreate_date() != null) {
			String dateString = DateTimeFormat.getFormat("dd / MM / yyyy").format(orderIn.getCreate_date());
			orderCreateDate.setText(dateString);
		}
	}

}
