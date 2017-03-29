package com.tranan.webstorage.client_admin.dialog;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.shared.Catalog;

public class ListCatalogDialog extends DialogBox {

	private static ListCatalogDialogUiBinder uiBinder = GWT
			.create(ListCatalogDialogUiBinder.class);

	interface ListCatalogDialogUiBinder extends
			UiBinder<Widget, ListCatalogDialog> {
	}

	@UiField
	HTMLPanel dialog;
	@UiField
	ScrollPanel scroll;
	@UiField
	HTMLPanel catalogTable;
	
	float opacity;
	Timer t;
	
	private List<Catalog> selectedCatalogs = new ArrayList<Catalog>();
	
	private ListCatalogDialog_Listener listener;
	
	public interface ListCatalogDialog_Listener {
		void onSelectedCatalog(List<Catalog> selectedCatalogs);
	}
	
	public void setCatalogs(List<Catalog> store_catalogs, List<Catalog> selected_catalogs) {
		catalogTable.clear();
		selectedCatalogs.clear();
		selectedCatalogs.addAll(selected_catalogs);
		
		for(final Catalog catalog: store_catalogs) {
			final HTMLPanel catalogTableRow = new HTMLPanel("");
			if(selected_catalogs.contains(catalog))
				catalogTableRow.setStyleName("ListCatalogDialog_table_chose");
			else
				catalogTableRow.setStyleName("ListCatalogDialog_table");
			
			HTMLPanel checkBoxCol = new HTMLPanel("");
			checkBoxCol.setStyleName("ListCatalogDialog_table_col1");
			final CheckBox cb = new CheckBox();
			if(selected_catalogs.contains(catalog))
				cb.setValue(true);
			else
				cb.setValue(false);
			checkBoxCol.add(cb);
			
			Label catalogNameCol = new Label(catalog.getName());
			catalogNameCol.setStyleName("ListCatalogDialog_table_col2");
			
			catalogTableRow.add(checkBoxCol);
			catalogTableRow.add(catalogNameCol);
			catalogTable.add(catalogTableRow);
			
			cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if(cb.getValue()) {
						catalogTableRow.setStyleName("ListCatalogDialog_table_chose");
						selectedCatalogs.add(catalog);
					}
					else {
						catalogTableRow.setStyleName("ListCatalogDialog_table");
						selectedCatalogs.remove(catalog);
					}
				}
			});
		}
	}

	public ListCatalogDialog(ListCatalogDialog_Listener listener) {
		setWidget(uiBinder.createAndBindUi(this));
		
		this.listener = listener;

		setGlassEnabled(true);
		setAutoHideEnabled(true);
		setAnimationEnabled(false);
		setStyleName("ConfirmDialog_s1");
		
		this.addAttachHandler(new Handler() {

			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached())
					faddingShowDialog();
			}
		});
		
		String height = Window.getClientHeight() / 2 + "px";
		scroll.getElement().setAttribute("style", "overflow-y:scroll; height:" + height);
	}
	
	void faddingShowDialog() {
		opacity = 0f;
		final String width = Window.getClientWidth() / 1.8 + "px";
		dialog.getElement().setAttribute("style", "opacity: 0; width: " + width);
		t = new Timer() {

			@Override
			public void run() {
				opacity = opacity + 0.25f;
				dialog.getElement().setAttribute("style",
						"opacity:" + String.valueOf(opacity) + "; width: " + width);

				if (opacity == 1)
					t.cancel();
			}
		};
		t.scheduleRepeating(50);
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent e) {
		listener.onSelectedCatalog(selectedCatalogs);
		hide();
	}
	
	@UiHandler("exitButton")
	void onExitButtonClick(ClickEvent e) {
		hide();
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		switch (event.getTypeInt()) {
		case Event.ONKEYDOWN:
			if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
				hide();
			}
			break;
		}
	}

}
