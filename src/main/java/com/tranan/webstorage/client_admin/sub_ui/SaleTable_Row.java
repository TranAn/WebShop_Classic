package com.tranan.webstorage.client_admin.sub_ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.place.CreateSalePlace;
import com.tranan.webstorage.client_admin.ui.ItemTable;
import com.tranan.webstorage.client_admin.ui.LoginPage;
import com.tranan.webstorage.shared.Sale;

public class SaleTable_Row extends Composite {

	private static SaleTable_RowUiBinder uiBinder = GWT
			.create(SaleTable_RowUiBinder.class);

	interface SaleTable_RowUiBinder extends UiBinder<Widget, SaleTable_Row> {
	}
	
	@UiField Label saleNameCol;
	@UiField Label saleFromCol;
	@UiField Label saleToCol;
	@UiField ListBox saleStatusCol;
	
	public interface SaleTableRowLitener {
		void onDeleteSale(Sale sale);
	}
	
	private Sale sale;
	private SaleTableRowLitener listener;

	public SaleTable_Row(SaleTableRowLitener listener) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.listener = listener;
		
		saleStatusCol.addItem("Đang chạy");
		saleStatusCol.addItem("Dừng");
		
		saleNameCol.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				PrettyGal.placeController.goTo(new CreateSalePlace(String.valueOf(sale.getId()), sale));
			}
		});
		
		saleStatusCol.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				if(saleStatusCol.getSelectedIndex() == 0)
					sale.setStatus(Sale.ACTIVE);
				else
					sale.setStatus(Sale.DEACTIVE);
				
				NoticePanel.onLoading();
				PrettyGal.dataService.createSale(sale, LoginPage.id_token, new AsyncCallback<Sale>() {
					
					@Override
					public void onSuccess(Sale result) {
						NoticePanel.endLoading();
						PrettyGal.UIC.getItemTable().ClearListItem();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						NoticePanel.failNotice(caught.getMessage());
					}
				});
			}
		});
	}
	
	public void setSale(Sale sale) {
		this.sale = sale;
		
		saleNameCol.setText(sale.getName());
		if(sale.getFrom() != null)
			saleFromCol.setText(DateTimeFormat.getFormat( "dd / MM / yyyy" ).format( sale.getFrom() ));
		if(sale.getTo() != null)
			saleToCol.setText(DateTimeFormat.getFormat( "dd / MM / yyyy" ).format( sale.getTo() ));
		if(sale.getStatus() == Sale.ACTIVE)
			saleStatusCol.setSelectedIndex(0);
		else
			saleStatusCol.setSelectedIndex(1);
	}
	
	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent e) {
		if(Window.confirm("Bạn muốn hủy chương trình khuyến mại này?")) {
			NoticePanel.onLoading();
			PrettyGal.dataService.deleteSale(sale.getId(), LoginPage.id_token, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					PrettyGal.UIC.getItemTable().ClearListItem();
					listener.onDeleteSale(sale);	
					NoticePanel.successNotice("Chương trình khuyến mại đã bị hủy");					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					NoticePanel.failNotice(caught.getMessage());
				}
			});
		}
	}

}
