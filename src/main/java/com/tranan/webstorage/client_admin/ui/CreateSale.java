package com.tranan.webstorage.client_admin.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.Ruler;
import com.tranan.webstorage.client_admin.dialog.ListItemDialog;
import com.tranan.webstorage.client_admin.dialog.ListItemDialog.ListItemDialog_Listener;
import com.tranan.webstorage.client_admin.place.SalePlace;
import com.tranan.webstorage.client_admin.sub_ui.DateBox;
import com.tranan.webstorage.client_admin.sub_ui.NoticePanel;
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
	@UiField TextArea descriptionTxb;
	@UiField TextBox nameSale;
	
	CreateSale thiz = this;
	
	private int sale_status;
	
	private Sale sale;
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
			lb1.setText(item.getName());		
			
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
			
			HTMLPanel panelPriceSale = new HTMLPanel("");
			panelPriceSale.setStyleName("CreateSale_s4");
			final LongBox lbPriceSale = new LongBox();
			PrettyGal.setPriceLongBox(lbPriceSale);
			lbPriceSale.setText(PrettyGal.integerToPriceString(item.getPrice() - (item.getPrice() * item.getSale() / 100)));
			Anchor btnPriceSale = new Anchor();
			btnPriceSale.setStyleName("anchor");
			panelPriceSale.add(btnPriceSale);
			panelPriceSale.add(lbPriceSale);
			btnPriceSale.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					lbPriceSale.setFocus(true);
				}
			});
			
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
					Long sale_price = item.getPrice() - (item.getPrice() * item.getSale() / 100);
					item.setSale_price(sale_price);
					lbPriceSale.setText(PrettyGal.integerToPriceString(sale_price));
				}
			});
			
			lbPriceSale.addKeyPressHandler(new KeyPressHandler()
            {
                @Override
                public void onKeyPress(KeyPressEvent event_)
                {
                    boolean enterPressed = KeyCodes.KEY_ENTER == event_
                            .getNativeEvent().getKeyCode();
                    if (enterPressed)
                    {
                    	lbPriceSale.setFocus(false);
                    }
                }
            });
			
			lbPriceSale.addBlurHandler(new BlurHandler() {
				
				@Override
				public void onBlur(BlurEvent event) {
					Long priceSale = Long.valueOf(lbPriceSale.getText().replaceAll("[.]", ""));
					int sale = 100 - (int)((priceSale * 100.0f) / item.getPrice());
					
					intbx1.setValue(sale);
					item.setSale(sale);
					item.setSale_price(priceSale);
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
			panel1.add(panelPriceSale);
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
	
	private void replaceCkEditor() {
		Timer t = new Timer() {
			@Override
			public void run() {
				replaceCkEditor("descriptionTxb", thiz);
					
			}
		};
		t.schedule(100);
	}
	
	private void setDataEditor() {
		if(sale != null)
			setDataCustomEditor("descriptionTxb", sale.getDescription());
	}

	public CreateSale() {
		initWidget(uiBinder.createAndBindUi(this));
		
		scroll.setHeight(Ruler.ItemTable_H + "px");
		
		descriptionTxb.getElement().setAttribute("id", "descriptionTxb");
		
		activeBox.setValue(true);
		activeBoxText.addStyleName("CreateSale_active");
		sale_status = Sale.ACTIVE;
		
		replaceCkEditor();
	}
	
	public void setSale(Sale sale) {
		this.sale = sale;
		
		nameSale.setText(sale.getName());
		saleFrom.setDate(sale.getFrom());
		saleTo.setDate(sale.getTo());
		
		for(Item i: sale.getSale_items()) {
			saleItems.add(new Item(i));
		}
		addSelectedItemToView(saleItems);
		
		setDataCustomEditor("descriptionTxb", sale.getDescription());
		
		if(sale.getStatus() == Sale.ACTIVE) {
			ClearStatus();
			activeBox.setValue(true);
			activeBoxText.addStyleName("CreateSale_active");
			sale_status = Sale.ACTIVE;
		}
		else {
			ClearStatus();
			deactiveBox.setValue(true);
			deactiveBoxText.addStyleName("CreateSale_deactive");
			sale_status = Sale.DEACTIVE;
		}
	}
	
	public boolean isItemChange() {
		return false;
	}
	
	public static native void replaceCkEditor(String editorId, CreateSale thiz) /*-{
	 	var noteId = editorId;
	  	var editor = $wnd.CKEDITOR.replace( noteId, {
	  		height: '150px',
	  		contentsCss : '',
	  		autoGrow_minHeight: 150,
	//  		autoGrow_maxHeight: 450,
	  		toolbarStartupExpanded : false,
	  		extraPlugins: 'autogrow',
	  		removeButtons: 'Image,Source',
	  		data: 'abc,'
	  	});
	  	
	  	editor.on("instanceReady",function() {
			thiz.@com.tranan.webstorage.client_admin.ui.CreateSale::setDataEditor()();
		});
	  	
	  	editor.on('focus', function(){	 
	//    	$wnd.document.getElementById(editor.id+'_top').style.display = "block";
	    });
	   
	    editor.on('blur', function(){	       
	//    	$wnd.document.getElementById(editor.id+'_top').style.display = "none";
	    });
	}-*/;
	
	public static native String getDataCustomEditor(String editorId) /*-{
		var eid = editorId;
		var editor = $wnd.document.getElementById("cke_"+ eid);
		if(editor != null) {
			var data = $wnd.CKEDITOR.instances[eid].getData();
			return data;
		}
		else
			return "";
	}-*/;

	public static native void setDataCustomEditor(String editorId, String data) /*-{
		var eid = editorId;
		var d = data;
		var editor = $wnd.document.getElementById("cke_"+ eid);
		if(editor != null) {
			$wnd.CKEDITOR.instances[eid].setData(d);
		}
	}-*/;
	
	@UiHandler("addItemButton") 
	void onAddItemButtonClick(ClickEvent e) {
		final ListItemDialog listItemDialog = new ListItemDialog(new ListItemDialog_Listener() {
			
			@Override
			public void onSelectedItem(List<Item> selectedItems) {
				for(Item item: selectedItems) {
					if(!saleItems.contains(item))
						saleItems.add(item);
					else
						selectedItems.remove(item);
				}
				addSelectedItemToView(selectedItems);
			}
		}, false);
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
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent e) {
		final Sale sale = new Sale();
		if(this.sale != null) {
			sale.setId(this.sale.getId());
		}

		sale.setName(nameSale.getText());
		sale.setFrom(saleFrom.getDate());
		sale.setTo(saleTo.getDate());
		sale.setDescription(getDataCustomEditor("descriptionTxb"));
//		sale.setStatus(sale_status);
		sale.setSale_items(saleItems);
		
		NoticePanel.onLoading();
		PrettyGal.dataService.createSale(sale, LoginPage.id_token, new AsyncCallback<Sale>() {
			
			@Override
			public void onSuccess(Sale result) {
				if(sale.getId() == null) {
					NoticePanel.successNotice("Tạo chương trình khuyến mại thành công");
					if(result.getStatus() == Sale.ACTIVE)
						PrettyGal.UIC.getItemTable().ClearListItem();
				}
				else {
					NoticePanel.successNotice("Thay đổi chương trình khuyến mại thành công");
				}
					
				PrettyGal.UIC.getSaleTable().addItem(result);
				PrettyGal.placeController.goTo(new SalePlace());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				NoticePanel.failNotice(caught.getMessage());
			}
		});
	}
	
	@UiHandler("exitButton")
	void onExitButtonClick(ClickEvent e) {
		PrettyGal.placeController.goTo(new SalePlace());
	}
	
}
