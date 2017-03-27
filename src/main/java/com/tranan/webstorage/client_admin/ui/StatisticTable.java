package com.tranan.webstorage.client_admin.ui;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.Ruler;
import com.tranan.webstorage.client_admin.sub_ui.NoticePanel;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Item.Type;
import com.tranan.webstorage.shared.Order;
import com.tranan.webstorage.shared.OrderIn;
import com.tranan.webstorage.shared.StatisticData;

public class StatisticTable extends Composite {

	private static StatisticTableUiBinder uiBinder = GWT
			.create(StatisticTableUiBinder.class);

	interface StatisticTableUiBinder extends UiBinder<Widget, StatisticTable> {
	}
	
	@UiField
	ScrollPanel scrollTable;
	@UiField
	HTMLPanel chartTable;
	@UiField
	ListBox yearListBox;
	@UiField
	ListBox monthListBox;
	@UiField
	Label summaryTitle;
	@UiField
	Label incomeTotal;
	@UiField
	Label expensesTotal;
	@UiField
	Label interestTotal;
	
	private String[] cur_orderSum;
	private String[] cur_orderInSum;
	
	private void getStatisticData(final String data_id) {
		NoticePanel.onLoading();
		PrettyGal.dataService.getStatisticData(data_id, new AsyncCallback<StatisticData>() {
			
			@Override
			public void onSuccess(StatisticData result) {
				String statistic_month = data_id.substring(0, 2);
				
				String[] orderSum = new String[32];
				String[] orderInSum = new String[32];
				
				for(int i=0; i<=31; i++) {
					orderSum[i] = "0";
					orderInSum[i] = "0";
				}
				
				if(result != null) {
					for(Order order: result.getListOrder()) {
						String order_month = DateTimeFormat.getFormat( "dd-MM-yyyy" )
								.format( order.getFinish_date() ).split("-")[1];
						String order_date = DateTimeFormat.getFormat( "dd-MM-yyyy" )
								.format( order.getFinish_date() ).split("-")[0];
						
						if(order.getStatus() == Order.FINISH && order_month.equals(statistic_month)) {						
							Long orderValues = 0L;
							for(Item item: order.getOrder_items()) {
								orderValues = orderValues + item.getType().get(0).getQuantity() * item.getPrice();
							}
					
							orderSum[Integer.valueOf(order_date)] = String.valueOf( 
									Long.valueOf(orderSum[Integer.valueOf(order_date)]) + orderValues );
						}
					}
					
					for(OrderIn orderIn: result.getListOrderIn()) {
						String orderIn_month = DateTimeFormat.getFormat( "dd-MM-yyyy" )
								.format( orderIn.getCreate_date() ).split("-")[1];
						String orderIn_date = DateTimeFormat.getFormat( "dd-MM-yyyy" )
								.format( orderIn.getCreate_date() ).split("-")[0];
						
						if(orderIn_month.equals(statistic_month)) {						
							Long orderInValues = 0L;
							for(Item item: orderIn.getOrder_items()) {								
								for(Type t: item.getType())
									orderInValues = orderInValues + (t.getQuantity() * item.getCost());															
							}
					
							orderInSum[Integer.valueOf(orderIn_date)] = String.valueOf( 
									Long.valueOf(orderInSum[Integer.valueOf(orderIn_date)]) + orderInValues );														
						}
					}
				}
				
				drawChartByMonth(orderSum, orderInSum);
				Long income = 0L;
				Long expenses = 0L;
				for(int i=1; i<=31; i++) {
					income = income + Long.valueOf(orderSum[i]);
					expenses = expenses + Long.valueOf(orderInSum[i]);
					incomeTotal.setText("+ " + PrettyGal.integerToPriceString(income));
					expensesTotal.setText("- " + PrettyGal.integerToPriceString(expenses));
					interestTotal.setText(PrettyGal.integerToPriceString(income - expenses));
				}
				NoticePanel.endLoading();
				
				cur_orderSum = orderSum;
				cur_orderInSum = orderInSum;
			}
			
			@Override
			public void onFailure(Throwable caught) {
				NoticePanel.failNotice(caught.getMessage());
			}
		});
	}
	
	private static native void drawChartByMonth(String[] orderSum, String[] orderInSum) /*-{
		$wnd.google.charts.load('current', {packages: ['corechart', 'line']});
		$wnd.google.charts.setOnLoadCallback(drawChart);
		
		function drawChart() {
	      var data = new $wnd.google.visualization.DataTable();
	      data.addColumn('number', 'X');
	      data.addColumn('number', 'Doanh thu bán hàng');
	      data.addColumn('number', 'Chi phí nhập hàng');
	
	      data.addRows([
	        [0, 0, 0],    [1, Number(orderSum[1]), Number(orderInSum[1])],   [2, Number(orderSum[2]), Number(orderInSum[2])],  
	        [3, Number(orderSum[3]), Number(orderInSum[3])],   [4, Number(orderSum[4]), Number(orderInSum[4])],  [5, Number(orderSum[5]), Number(orderInSum[5])],
	        [6, Number(orderSum[6]), Number(orderInSum[6])],   [7, Number(orderSum[7]), Number(orderInSum[7])],  [8, Number(orderSum[8]), Number(orderInSum[8])],  
	        [9, Number(orderSum[9]), Number(orderInSum[9])],  [10, Number(orderSum[10]), Number(orderInSum[10])], [11, Number(orderSum[11]), Number(orderInSum[11])],
	        [12, Number(orderSum[12]), Number(orderInSum[12])], [13, Number(orderSum[13]), Number(orderInSum[13])], [14, Number(orderSum[14]), Number(orderInSum[14])], 
	        [15, Number(orderSum[15]), Number(orderInSum[15])], [16, Number(orderSum[16]), Number(orderInSum[16])], [17, Number(orderSum[17]), Number(orderInSum[17])],
	        [18, Number(orderSum[18]), Number(orderInSum[18])], [19, Number(orderSum[19]), Number(orderInSum[19])], [20, Number(orderSum[20]), Number(orderInSum[20])], 
	        [21, Number(orderSum[21]), Number(orderInSum[21])], [22, Number(orderSum[22]), Number(orderInSum[22])], [23, Number(orderSum[23]), Number(orderInSum[23])],
	        [24, Number(orderSum[24]), Number(orderInSum[24])], [25, Number(orderSum[25]), Number(orderInSum[25])], [26, Number(orderSum[26]), Number(orderInSum[26])], 
	        [27, Number(orderSum[27]), Number(orderInSum[27])], [28, Number(orderSum[28]), Number(orderInSum[28])], [29, Number(orderSum[29]), Number(orderInSum[29])],
	        [30, Number(orderSum[30]), Number(orderInSum[30])], [31, Number(orderSum[31]), Number(orderInSum[31])]
	      ]);
	
	      var options = {
	      	animation: {
	      	  startup: true,	
		      duration: 1000,
		      easing: 'out',
		    },
	        hAxis: {
	          title: 'Ngày',
	          gridlines: {count: 15}
	        },
	        vAxis: {
	          title: 'Giá trị'
	        },
//	        series: {
//	          1: {curveType: 'function'}
//	        }
	      };
	
	      var chart = new $wnd.google.visualization.LineChart($wnd.document.getElementById('chartTable'));
	      chart.draw(data, options);
	    }
	}-*/;

	public StatisticTable() {
		initWidget(uiBinder.createAndBindUi(this));
		
		scrollTable.setHeight(Ruler.ItemTable_H + "px");
		chartTable.getElement().setAttribute("id", "chartTable");
		
		String year = DateTimeFormat.getFormat( "dd-MM-yyyy" )
				.format( new Date(System.currentTimeMillis()) ).split("-")[2];
		String month = DateTimeFormat.getFormat( "dd-MM-yyyy" )
				.format( new Date(System.currentTimeMillis()) ).split("-")[1];
		
		yearListBox.addItem(Integer.valueOf(year) - 2 + "");
		yearListBox.addItem(Integer.valueOf(year) - 1 + "");
		yearListBox.addItem(Integer.valueOf(year) - 0 + "");
		yearListBox.setSelectedIndex(2);
		
		monthListBox.addItem("");
		monthListBox.addItem("1");
		monthListBox.addItem("2");
		monthListBox.addItem("3");
		monthListBox.addItem("4");
		monthListBox.addItem("5");
		monthListBox.addItem("6");
		monthListBox.addItem("7");
		monthListBox.addItem("8");
		monthListBox.addItem("9");
		monthListBox.addItem("10");
		monthListBox.addItem("11");
		monthListBox.addItem("12");
//		monthListBox.addItem("cả năm");
		monthListBox.setSelectedIndex(Integer.valueOf(month));
		
		getStatisticData(month + year);
		summaryTitle.setText("Tháng " + month + " Năm " + year);
		
		monthListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String selected_year = yearListBox.getValue(yearListBox.getSelectedIndex());
				String selected_month = monthListBox.getValue(monthListBox.getSelectedIndex());
				if(selected_month.length() == 1)
					selected_month = "0" + selected_month;
				
				getStatisticData(selected_month + selected_year);
				summaryTitle.setText("Tháng " + selected_month + " Năm " + selected_year);
			}
		});
		
		cur_orderSum = new String[32];
		cur_orderInSum = new String[32];
		
		for(int i=0; i<=31; i++) {
			cur_orderSum[i] = "0";
			cur_orderInSum[i] = "0";
		}
		
		addAttachHandler(new Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				drawChartByMonth(cur_orderSum, cur_orderInSum);
			}
		});
	}

}
