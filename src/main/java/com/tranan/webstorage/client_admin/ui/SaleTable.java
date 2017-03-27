package com.tranan.webstorage.client_admin.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.Ruler;
import com.tranan.webstorage.client_admin.place.CreateSalePlace;
import com.tranan.webstorage.client_admin.sub_ui.NoticePanel;
import com.tranan.webstorage.client_admin.sub_ui.SaleTable_Row;
import com.tranan.webstorage.client_admin.sub_ui.SaleTable_Row.SaleTableRowLitener;
import com.tranan.webstorage.shared.Sale;

public class SaleTable extends Composite {

	private static SaleTableUiBinder uiBinder = GWT
			.create(SaleTableUiBinder.class);

	interface SaleTableUiBinder extends UiBinder<Widget, SaleTable> {
	}
	
	@UiField
	ScrollPanel scrollTable;
	@UiField
	HTMLPanel saleTable;
	
	private List<Sale> list_sales = null;
	
	private void getSales() {
		if(list_sales == null) {
			NoticePanel.onLoading();
			PrettyGal.dataService.getSales(new AsyncCallback<List<Sale>>() {
				
				@Override
				public void onSuccess(List<Sale> result) {
					if(result == null)
						list_sales = new ArrayList<Sale>();
					else {
						list_sales = result;
						setTableView(result);
					}
					
					NoticePanel.endLoading();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					NoticePanel.failNotice(caught.getMessage());
				}
			});
		}
	}
	
	private void setTableView(List<Sale> sales) {
		saleTable.clear();
		
		for(Sale sale: sales) {
			if(sale.getStatus() == Sale.ACTIVE) {
				sales.remove(sale);
				sales.add(0, sale);
			}
		}
		
		for(Sale sale: sales) {
			final SaleTable_Row row = new SaleTable_Row(new SaleTableRowLitener() {
				
				@Override
				public void onDeleteSale(Sale sale) {
					list_sales.remove(sale);
					setTableView(list_sales);
				}
			});
			
			row.setSale(sale);
			saleTable.add(row);
		}
	}

	public SaleTable() {
		initWidget(uiBinder.createAndBindUi(this));
		
		scrollTable.setHeight(Ruler.ItemTable_H + "px");

		getSales();
		
		addAttachHandler(new Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if(list_sales != null)
					setTableView(list_sales);
			}
		});
	}
	
	public void addItem(Sale sale) {
		if(list_sales.contains(sale)) {
			int index = list_sales.indexOf(sale);
			list_sales.remove(index);
			list_sales.add(index, sale);
		}
		else
			list_sales.add(0, sale);
		
		setTableView(list_sales);
	}
	
	@UiHandler("addSaleButton")
	void onAddSaleButtonClick(ClickEvent e) {
		PrettyGal.placeController.goTo(new CreateSalePlace("", null));
	}

}
