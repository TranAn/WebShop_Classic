package com.tranan.webstorage.client.sub_ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Pager extends Composite {

	private static PagerUiBinder uiBinder = GWT.create(PagerUiBinder.class);

	interface PagerUiBinder extends UiBinder<Widget, Pager> {
	}

	@UiField
	HTMLPanel pageLeft;
	@UiField
	HTMLPanel pageRight;
	@UiField
	Label pagerLabel;

	private int current_page = 1;
	private int total;
	private int max_page;
	private PagerListener litener;
	private int page_size;

	public interface PagerListener {
		void pageIndex(int index);
	}

	public Pager() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setPage(int total, int page_size, PagerListener litener) {
		this.litener = litener;
		current_page = 1;
		this.page_size = page_size;
		
		pageLeft.addStyleName("Pager_s2_deactived");
		pageRight.removeStyleName("Pager_s2_deactived");
		
		this.total = total;
		max_page = total / page_size;
		if ((total % page_size) != 0)
			max_page = max_page + 1;

		int lastItem = page_size * current_page;
		int firstItem = lastItem - page_size + 1;
		if(total == 0)
			firstItem = 0;
		if (lastItem >= total) {
			lastItem = total;
			pageRight.addStyleName("Pager_s2_deactived");
		}

		pagerLabel.setText(firstItem + " - " + lastItem + " / " + total);
	}
	
	public void updatePage(int total, int page_size) {
		this.total = total;
		max_page = total / page_size;
		if ((total % page_size) != 0)
			max_page = max_page + 1;

		int lastItem = page_size * current_page;
		int firstItem = lastItem - page_size + 1;
		if(total == 0)
			firstItem = 0;
		if (lastItem >= total) {
			lastItem = total;
			pageRight.addStyleName("Pager_s2_deactived");
		}

		pagerLabel.setText(firstItem + " - " + lastItem + " / " + total);
		
		litener.pageIndex(firstItem - 1);
	}

	@UiHandler("pagerPreviousButton")
	void onPreviousButtonClick(ClickEvent e) {
		if (current_page > 1) {
			current_page--;
			
			if(current_page == 1)
				pageLeft.addStyleName("Pager_s2_deactived");
			pageRight.removeStyleName("Pager_s2_deactived");

			int lastItem = page_size * current_page;
			int firstItem = lastItem - page_size + 1;
			if (lastItem > total) {
				lastItem = total;
			}

			pagerLabel.setText(firstItem + " - " + lastItem + " / " + total);

			litener.pageIndex(firstItem - 1);
		}
	}

	@UiHandler("pagerNextButton")
	void onNextButtonClick(ClickEvent e) {
		if (current_page < max_page) {
			current_page++;
			
			if(current_page == max_page)
				pageRight.addStyleName("Pager_s2_deactived");
			pageLeft.removeStyleName("Pager_s2_deactived");

			int lastItem = page_size * current_page;
			int firstItem = lastItem - page_size + 1;
			if (lastItem > total) {
				lastItem = total;
			}

			pagerLabel.setText(firstItem + " - " + lastItem + " / " + total);

			litener.pageIndex(firstItem - 1);
		}
	}
}
