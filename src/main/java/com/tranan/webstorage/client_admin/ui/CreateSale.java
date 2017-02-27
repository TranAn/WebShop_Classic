package com.tranan.webstorage.client_admin.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.Ruler;
import com.tranan.webstorage.client_admin.dialog.ListItemDialog;
import com.tranan.webstorage.client_admin.dialog.ListItemDialog.ListItemDialog_Listener;
import com.tranan.webstorage.client_admin.place.SalePlace;
import com.tranan.webstorage.shared.Item;
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
	@UiField HTMLPanel itemTable;
	
	private int sale_status;
	
	private List<Item> saleItems = new ArrayList<Item>();
	
	private void addSelectedItemToView(List<Item> selectedItems) {
		for(final Item item: selectedItems) {
			final HTMLPanel panel1 = new HTMLPanel("");
			panel1.setStyleName("CreateSale_s2");
			
			//Image Col
			HTMLPanel panel2 = new HTMLPanel("");
			panel2.setStyleName("CreateSale_s3");
			Image img = new Image();
			img.setUrl(item.getAvatar_url());
			img.setSize("50px", "50px");
			panel2.add(img);
			
			//Name Col
			Label lb1 = new Label();
			lb1.setWidth("70%");
			lb1.setStyleName("CreateSale_s4_left");
			if(item.getType().get(0).getName().equals(Item.DEFAULT_TYPE))
				lb1.setText(item.getName());
			else
				lb1.setText(item.getName()+ " ("+ item.getType().get(0).getName()+ ")");			
			
			//Price Col
			HTMLPanel panel3 = new HTMLPanel("");
			panel3.setStyleName("CreateSale_s4");
			final Label lbx1 = new Label();
			lbx1.setText(PrettyGal.integerToPriceString(item.getPrice()));
			panel3.add(lbx1);
			
			//Sale Col
			HTMLPanel panel4 = new HTMLPanel("");
			panel4.setStyleName("CreateSale_s5");
			final IntegerBox intbx1 = new IntegerBox();
			intbx1.setValue(item.getSale());
			intbx1.getElement().setAttribute("type", "number");
			intbx1.getElement().setAttribute("min", "0");
			Anchor anchor2 = new Anchor();
			anchor2.setStyleName("anchor");
			panel4.add(anchor2);
			panel4.add(intbx1);
			anchor2.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					intbx1.setFocus(true);
				}
			});
			
			final Label lbPriceSale = new Label();
			lbPriceSale.setText(PrettyGal.integerToPriceString(item.getPrice() - (item.getPrice() * item.getSale() / 100)));
			lbPriceSale.setStyleName("CreateSale_s4");
			
			HTMLPanel panel5 = new HTMLPanel("<i class='material-icons'>&#xE872;</i>");
			panel5.setStyleName("CreateSale_s9");
			Anchor deleteItem = new Anchor();
			deleteItem.setStyleName("anchor");
			panel5.add(deleteItem);
			
			intbx1.addKeyPressHandler(new KeyPressHandler()
            {
                @Override
                public void onKeyPress(KeyPressEvent event_)
                {
                    boolean enterPressed = KeyCodes.KEY_ENTER == event_
                            .getNativeEvent().getKeyCode();
                    if (enterPressed)
                    {
                    	intbx1.setFocus(false);
                    }
                }
            });
			
			intbx1.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					item.setSale(intbx1.getValue());
					lbPriceSale.setText(PrettyGal.integerToPriceString
							(item.getPrice() - (item.getPrice() * item.getSale() / 100)));
				}
			});
			
			deleteItem.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					itemTable.remove(panel1);
					saleItems.remove(item);
				}
			});
			
			panel1.add(panel2);
			panel1.add(lb1);
			panel1.add(panel3);
			panel1.add(panel4);
			panel1.add(lbPriceSale);
			panel1.add(panel5);
			itemTable.add(panel1);
		}
	}
	
	private void ClearStatus() {
		activeBox.setValue(false);
		deactiveBox.setValue(false);
		
		activeBoxText.removeStyleName("CreateSale_active");
		deactiveBoxText.removeStyleName("CreateSale_deactive");
	}

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
	
	@UiHandler("addItemButton") 
	void onAddItemButtonClick(ClickEvent e) {
		final ListItemDialog listItemDialog = new ListItemDialog(new ListItemDialog_Listener() {
			
			@Override
			public void onSelectedItem(List<Item> selectedItems) {
				for(Item item: selectedItems) {
					item.getType().get(0).setQuantity(0);
					saleItems.add(item);
				}
				addSelectedItemToView(selectedItems);
			}
		});
		Timer t = new Timer() {

			@Override
			public void run() {
				listItemDialog.center();
			}};
		t.schedule(50);
	}
	
	@UiHandler("activeBoxAnchor")
	void onActiveBoxAnchorClick(ClickEvent e) {
		ClearStatus();
		activeBox.setValue(true);
		activeBoxText.addStyleName("CreateSale_active");
		sale_status = Sale.ACTIVE;
	}
	
	@UiHandler("deactiveBoxAnchor")
	void onDeActiveBoxAnchorClick(ClickEvent e) {
		ClearStatus();
		deactiveBox.setValue(true);
		deactiveBoxText.addStyleName("CreateSale_deactive");
		sale_status = Sale.DEACTIVE;
	}
	
	@UiHandler("exitButton")
	void onExitButtonClick(ClickEvent e) {
		PrettyGal.placeController.goTo(new SalePlace());
	}
	
}
