package com.tranan.webstorage.client_admin;

import com.google.gwt.user.client.Window;

public class Ruler {

	public static int RightPage_W = Window.getClientWidth() - 250;

	public static int ItemTable_H = Window.getClientHeight() - 64;

	public static int ItemTable_Categories_W = (Window.getClientWidth() - 250 - 40)
			- (Window.getClientWidth() - 250 - 40) * 86 / 100 - 20;

	public static int ItemTableRow_itemname_W = Window.getClientWidth() - 250 - 40  - 690;
	
	public static int ItemImportTable_item_W = (RightPage_W - 40) / 2 - 4;
}
