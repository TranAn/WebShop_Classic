package com.tranan.webstorage.client_admin.sub_ui;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.dialog.DatePickerDialog;

public class DateBox extends Composite {

	private static DateBoxUiBinder uiBinder = GWT.create(DateBoxUiBinder.class);

	interface DateBoxUiBinder extends UiBinder<Widget, DateBox> {
	}

	@UiField
	TextBox dateTxb;
	@UiField
	TextBox monthTxb;
	@UiField
	TextBox yearTxb;
	
	private void showDatePickerDialog(TextBox textBox) {
		final int left = textBox.getAbsoluteLeft();
		final int top = textBox.getAbsoluteTop() + 40;
		
		final DatePickerDialog dialog = new DatePickerDialog();
		Timer t = new Timer() {

			@Override
			public void run() {
				dialog.setPopupPosition(left, top);
				dialog.show();
			}};
		t.schedule(100);
	}

	public DateBox() {
		initWidget(uiBinder.createAndBindUi(this));
		
		Date currentDate = new Date(System.currentTimeMillis());
		String year = DateTimeFormat.getFormat( "dd-MM-yyyy" ).format( currentDate ).split("-")[2];
		yearTxb.setText(year);

		dateTxb.getElement().setAttribute("placeholder", "ngày");
		monthTxb.getElement().setAttribute("placeholder", "tháng");
		yearTxb.getElement().setAttribute("placeholder", "năm");

		dateTxb.getElement().setAttribute("maxlength", "2");
		monthTxb.getElement().setAttribute("maxlength", "2");
		yearTxb.getElement().setAttribute("maxlength", "4");

		dateTxb.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				char ch = event.getCharCode();
				if (ch != '0' && ch != '1' && ch != '2' && ch != '3'
						&& ch != '4' && ch != '5' && ch != '6' && ch != '7'
						&& ch != '8' && ch != '9') {
					dateTxb.cancelKey();
				}
			}
		});

		monthTxb.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				char ch = event.getCharCode();
				if (ch != '0' && ch != '1' && ch != '2' && ch != '3'
						&& ch != '4' && ch != '5' && ch != '6' && ch != '7'
						&& ch != '8' && ch != '9') {
					monthTxb.cancelKey();
				}
			}
		});

		yearTxb.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				char ch = event.getCharCode();
				if (ch != '0' && ch != '1' && ch != '2' && ch != '3'
						&& ch != '4' && ch != '5' && ch != '6' && ch != '7'
						&& ch != '8' && ch != '9') {
					yearTxb.cancelKey();
				}
			}
		});

		dateTxb.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				dateTxb.setSelectionRange(0, dateTxb.getText().length());
//				showDatePickerDialog(dateTxb);	
			}
		});

		monthTxb.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				monthTxb.setSelectionRange(0, monthTxb.getText().length());
			}
		});

		yearTxb.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				yearTxb.setSelectionRange(0, yearTxb.getText().length());
			}
		});
		
		dateTxb.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if(dateTxb.getText().length() == 1)
					dateTxb.setText("0" + dateTxb.getText());
				if(Integer.valueOf(dateTxb.getText()) > 31)
					dateTxb.setText("31");
				if(Integer.valueOf(dateTxb.getText()) == 0)
					dateTxb.setText("01");
			}
		});
		
		monthTxb.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if(monthTxb.getText().length() == 1)
					monthTxb.setText("0" + monthTxb.getText());
				if(Integer.valueOf(monthTxb.getText()) > 12)
					monthTxb.setText("12");
				if(Integer.valueOf(monthTxb.getText()) == 0)
					monthTxb.setText("01");
			}
		});
	}

	public Date getDate() {
		try {
			String dateString = dateTxb.getText() + "-" + monthTxb.getText() + "-" + yearTxb.getText();
			DateTimeFormat format = DateTimeFormat.getFormat( "dd-MM-yyyy" );
			Date date = format.parse(dateString);
			
			return date;
		} catch (Exception e) {
			return null;
		}
	}

	public void setDate(Date date) {
		if(date != null) {
			String day = 
			DateTimeFormat.getFormat( "dd-MM-yyyy" ).format( date ).split("-")[0];
		
			String month = 
			DateTimeFormat.getFormat( "dd-MM-yyyy" ).format( date ).split("-")[1];
		
			String year = 
			DateTimeFormat.getFormat( "dd-MM-yyyy" ).format( date ).split("-")[2];
			
			dateTxb.setText(day);
			monthTxb.setText(month);
			yearTxb.setText(year);
		}
	}

}
