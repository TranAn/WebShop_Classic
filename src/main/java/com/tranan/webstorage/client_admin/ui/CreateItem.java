package com.tranan.webstorage.client_admin.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tranan.webstorage.client_admin.PrettyGal;
import com.tranan.webstorage.client_admin.Ruler;
import com.tranan.webstorage.client_admin.dialog.ListCatalogDialog;
import com.tranan.webstorage.client_admin.dialog.ListCatalogDialog.ListCatalogDialog_Listener;
import com.tranan.webstorage.client_admin.place.ItemPlace;
import com.tranan.webstorage.client_admin.sub_ui.NoticePanel;
import com.tranan.webstorage.shared.Catalog;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Item.Type;
import com.tranan.webstorage.shared.Photo;

public class CreateItem extends Composite {

	private static CreateItemUiBinder uiBinder = GWT
			.create(CreateItemUiBinder.class);

	interface CreateItemUiBinder extends UiBinder<Widget, CreateItem> {
	}
	
	@UiField ScrollPanel scroll;
	@UiField TextArea descriptionTxb;
	@UiField HTMLPanel container;
	@UiField HTMLPanel imgPanel;
	@UiField HTMLPanel samplePhoto;
	@UiField Anchor saveButton;
	@UiField TextBox nameText;
	@UiField LongBox costText;
	@UiField LongBox priceText;
	@UiField IntegerBox saleText;
	@UiField IntegerBox quantityText;
	@UiField HTMLPanel quantityPanel;
	@UiField HTMLPanel itemTypeTable;
	@UiField HTMLPanel addItemTab;
	@UiField HTMLPanel editCatalogTab;
	@UiField HTMLPanel itemTabHeader;
	@UiField HTMLPanel catalogTabHeader;
	@UiField TextBox nameCatalog;
	@UiField HTMLPanel catalogTable;
	@UiField HTMLPanel catalogTableTitle;
	@UiField HTMLPanel catalogRow;
	@UiField HTMLPanel itemCatalogsTable;
	@UiField HTMLPanel avatarTitle1;
	@UiField HTMLPanel avatarTitle2;
	@UiField HTMLPanel avatarTitle3;
	@UiField HTMLPanel avatarTitle4;
	@UiField HTMLPanel avatarTitle5;
	@UiField Anchor addCatalogButton;
	@UiField HTMLPanel avatarTitlePanel;
	
	CreateItem thiz = this;

	private static Item item;
	
	private boolean skipCheckItemChange = false;
	private boolean isUpdate = false;
	/*static Long itemId;*/
	
	private List<TypeBox> listTypeBox = new ArrayList<TypeBox>();
	private String avatarIndex;
	private List<Catalog> itemCatalog = new ArrayList<Catalog>();
	
	class TypeBox {
		public TextBox type;
		public IntegerBox quantity;
		
		public TypeBox(TextBox type, IntegerBox quantity) {
			super();
			this.type = type;
			this.quantity = quantity;
		}	
	}
	
	void getListCatalog() {
		if(ItemTable.listCatalog == null) {
			NoticePanel.onLoading();
			
			ItemTable.listCatalog = new ArrayList<Catalog>();
			PrettyGal.dataService.getCatalogs(new AsyncCallback<List<Catalog>>() {
				
				@Override
				public void onSuccess(List<Catalog> result) {
					ItemTable.listCatalog.addAll(result);
					NoticePanel.endLoading();
					
					itemCatalog.clear();
					for(Catalog catalog: ItemTable.listCatalog) {					
						if(item != null && item.getCatalog_ids().contains(catalog.getId())) {
							itemCatalog.add(catalog);
						}
					}
					addCatalogListBox(itemCatalog);
					addCatalogButton.setVisible(true);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					NoticePanel.endLoading();
				}
			});
		}
		else {
			itemCatalog.clear();
			for(Catalog catalog: ItemTable.listCatalog) {
				if(item != null && item.getCatalog_ids().contains(catalog.getId())) {
					itemCatalog.add(catalog);
				}
			}
			addCatalogListBox(itemCatalog);
			addCatalogButton.setVisible(true);
		}
	}
	
	void addCatalogListBox(List<Catalog> catalogs) {
		if(!catalogs.isEmpty()) {
			itemCatalogsTable.clear();
			itemCatalogsTable.setVisible(true);
			
			for(final Catalog catalog: catalogs) {
				final HTMLPanel panel1 = new HTMLPanel("");
				panel1.setStyleName("CreateItem_s16");
				Label lb1 = new Label(catalog.getName());
				lb1.setStyleName("CreateItem_s17");
				HTMLPanel panel2 = new HTMLPanel("");
				panel2.setStyleName("CreateItem_s18");
				final CheckBox cb = new CheckBox();
				Anchor a = new Anchor();
				a.setStyleName("CreateItem_s11");
				
				panel2.add(cb);
				panel1.add(lb1);
//				panel1.add(panel2);
				panel1.add(a);
				itemCatalogsTable.add(panel1);
				
//				if(item != null && item.getCatalog_ids().contains(catalog.getId())) {
//					panel1.addStyleName("CreateItem_s16_active");
//					cb.setValue(true);
//				}
				panel1.addStyleName("CreateItem_s16_active");
				
//				a.addClickHandler(new ClickHandler() {
//					
//					@Override
//					public void onClick(ClickEvent event) {
//						cb.setValue(!cb.getValue());
//						if(cb.getValue()) {	
//							panel1.addStyleName("CreateItem_s16_active");
//							item_catalog_ids.add(catalog.getId());
//						}
//						else {
//							panel1.removeStyleName("CreateItem_s16_active");
//							item_catalog_ids.remove(catalog.getId());
//						}
//					}
//				});
			}
		}
		else {
			itemCatalogsTable.clear();
			itemCatalogsTable.setVisible(false);
		}
	}

	public CreateItem() {
		initWidget(uiBinder.createAndBindUi(this));
		item = null;
		getListCatalog();
		
		descriptionTxb.getElement().setAttribute("id", "descriptionTxb");
		container.getElement().setAttribute("id", "container");
		imgPanel.getElement().setAttribute("id", "imgPanel");
		samplePhoto.getElement().setAttribute("id", "samplePhoto");
		
		nameText.getElement().setAttribute("placeholder", "Tên sản phẩm");
		priceText.getElement().setAttribute("placeholder", "0");
		costText.getElement().setAttribute("placeholder", "0");
		saleText.getElement().setAttribute("placeholder", "0");
		quantityText.getElement().setAttribute("placeholder", "0");
		quantityText.getElement().setAttribute("type", "number");
		priceText.getElement().setAttribute("maxlength", "13");
		costText.getElement().setAttribute("maxlength", "13");
		saleText.getElement().setAttribute("maxlength", "2");
		
		scroll.setHeight(Ruler.ItemTable_H + "px");
		
		replaceCkEditor();
		replacePlupLoad();
		
		priceText.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(!event.isLeftArrow() && !event.isRightArrow() && event.getNativeKeyCode() != 8) {
					Long value = Long.valueOf(priceText.getText().replaceAll("[.]", ""));
					priceText.setText(PrettyGal.integerToPriceString(value));
				}
			}
		});
		
		priceText.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char ch = event.getCharCode();
				if(ch != '0' && ch != '1' && ch != '2' && ch != '3' && ch != '4' && ch != '5'
						&& ch != '6' && ch != '7' && ch != '8' && ch != '9') {
					priceText.cancelKey();
				}
			}
		});
		
		priceText.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				Long value = Long.valueOf(priceText.getText().replaceAll("[.]", ""));
				priceText.setText(PrettyGal.integerToPriceString(value));
			}
		});
		
		costText.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(!event.isLeftArrow() && !event.isRightArrow() && event.getNativeKeyCode() != 8) {
					Long value = Long.valueOf(costText.getText().replaceAll("[.]", ""));
					costText.setText(PrettyGal.integerToPriceString(value));
				}
			}
		});
		
		costText.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char ch = event.getCharCode();
				if(ch != '0' && ch != '1' && ch != '2' && ch != '3' && ch != '4' && ch != '5'
						&& ch != '6' && ch != '7' && ch != '8' && ch != '9') {
					costText.cancelKey();
				}
			}
		});
		
		costText.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				Long value = Long.valueOf(costText.getText().replaceAll("[.]", ""));
				costText.setText(PrettyGal.integerToPriceString(value));
			}
		});
		
		saleText.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char ch = event.getCharCode();
				if(ch != '0' && ch != '1' && ch != '2' && ch != '3' && ch != '4' && ch != '5'
						&& ch != '6' && ch != '7' && ch != '8' && ch != '9') {
					saleText.cancelKey();
				}
			}
		});
	}
	
	public void setItem(final Item i) {
		isUpdate = true;
		CreateItem.item = new Item(i);
		
		for(final Long photo_id: item.getPhoto_ids()) {
			samplePhoto.getElement().setAttribute("style", "display: none");
			
			final HTMLPanel span = new HTMLPanel("");
			span.setStyleName("CreateItem_s3");
			final Image img = new Image();
			img.setSize("120px", "120px");
			
			Anchor removeButton = new Anchor();    
			removeButton.setStyleName("CreateItem_s4");
            removeButton.getElement().setInnerHTML("<i class='material-icons'>&#xE872;</i>");
            removeButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					imgPanel.remove(span);
					item.getPhoto_ids().remove(photo_id);
					setFileQueued(5 - item.getPhoto_ids().size());
					
					if(item.getPhoto_ids().isEmpty()) {
						avatarIndex = null;
						String file_queued = String.valueOf(getFileQueued());
						if(file_queued.equals("0")) {
							setAvatarTitle("-1");
							samplePhoto.getElement().setAttribute("style", "display: ");
						}
						else
							setAvatarTitle("0");
					}
					else {
						int remove_index = item.getPhoto_ids().indexOf(photo_id);
						if(avatarIndex != null) {
							if(remove_index == Integer.valueOf(avatarIndex)) {
								avatarIndex = "0";
								setAvatarTitle(avatarIndex);
							}
							else if(remove_index < Integer.valueOf(avatarIndex)) {
								avatarIndex = String.valueOf(Integer.valueOf(avatarIndex) - 1);
								setAvatarTitle(avatarIndex);
							}
						}
					}
				}
			});
            
            Anchor avatarButton = new Anchor();
            avatarButton.setStyleName("CreateItem_s20");
            avatarButton.getElement().setInnerHTML("Ảnh Đại Diện");
            avatarButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					avatarIndex = String.valueOf(item.getPhoto_ids().indexOf(photo_id));
					setAvatarTitle(avatarIndex);
				}
			});
            
            span.add(img);
            span.add(removeButton);
            span.add(avatarButton);
            imgPanel.add(span);
            
			PrettyGal.dataService.getPhoto(photo_id, new AsyncCallback<Photo>() {
				
				@Override
				public void onSuccess(Photo result) {
					img.setUrl(result.getServeUrl());
					avatarTitle1.getElement().setAttribute("style", "display:");
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
		
		nameText.setText(item.getName());
		costText.setText(PrettyGal.integerToPriceString(item.getCost()));
		priceText.setText(PrettyGal.integerToPriceString(item.getPrice()));
		saleText.setText(String.valueOf(item.getSale()));
		if(item.getType().size() == 1 && item.getType().get(0).getName().equals(Item.DEFAULT_TYPE))
			quantityText.setValue(item.getType().get(0).getQuantity());
		else {
			for(Type type: item.getType())
				addTypeRow(type.getName(), type.getQuantity());
		}
		
		Timer t = new Timer() {
			@Override
			public void run() {
//				setDataCustomEditor("descriptionTxb", item.getDescription());
				setFileQueued(5 - item.getPhoto_ids().size());
			}
		};
		t.schedule(500);
		
		getListCatalog();
	}
	
	public void clean() {
		nameText.setText("");
		priceText.setText("");
		costText.setText("");
		quantityText.setText("");
		setDataCustomEditor("descriptionTxb", "");
		imgPanel.getElement().setInnerHTML
		("<div class='CreateItem_s6' id='samplePhoto'>" +
			"<i class='material-icons CreateItem_s2'>&#xE3B0;</i>" +
		"</div>");
	}
	
	public boolean isItemChange() {
		if(skipCheckItemChange)
			return false;
		
		if(item != null) {
			if(!nameText.getText().equals(item.getName()))
				return true;
			if(!costText.getText().replaceAll("[.]", "").equals(String.valueOf(item.getCost())))
				return true;
			if(!priceText.getText().replaceAll("[.]", "").equals(String.valueOf(item.getPrice())))
				return true;
			if(!saleText.getText().equals(String.valueOf(item.getSale())))
				return true;
			if(item.getType().isEmpty() && !quantityText.getText().isEmpty())
				return true;
			else {
				if(item.getType().size() == 1 && !quantityText.getText().equals(String.valueOf(item.getType().get(0).getQuantity())))
					return true;
				if(item.getType().size() == 1 && !listTypeBox.isEmpty())
					return true;
				if(item.getType().size() > 1) {
					if(item.getType().size() != listTypeBox.size())
						return true;
					else {
						for(int i = 0; i < item.getType().size(); i++) {
							if(!item.getType().get(i).getName().equals(listTypeBox.get(i).type.getText()))
								return true;
							if(item.getType().get(i).getQuantity() != listTypeBox.get(i).quantity.getValue())
								return true;
						}
					}
				}
			}
			if(!item.getDescription().equals(getDataCustomEditor("descriptionTxb")))
				return true;
			
			return false;
		}
		else {
			if(!nameText.getText().isEmpty())
				return true;
			if(!costText.getText().isEmpty())
				return true;
			if(!priceText.getText().isEmpty())
				return true;
			if(!saleText.getText().isEmpty())
				return true;
			if(!quantityText.getText().isEmpty())
				return true;
			if(!getDataCustomEditor("descriptionTxb").isEmpty())
				return true;
			
			return false;
		}
	}

	public void replaceCkEditor() {
		Timer t = new Timer() {
			@Override
			public void run() {
				replaceCkEditor("descriptionTxb", thiz);
			}
		};
		t.schedule(100);
	}
	
	private void setDataEditor() {
		if(item != null)
			setDataCustomEditor("descriptionTxb", item.getDescription());
	}
	
	private void setAvatarTitle(String index) {
		avatarTitle1.setVisible(false);
		avatarTitle2.setVisible(false);
		avatarTitle3.setVisible(false);
		avatarTitle4.setVisible(false);
		avatarTitle5.setVisible(false);
		
		int img_index = Integer.valueOf(index);
		switch (img_index) {
		case 0:
			avatarTitle1.setVisible(true);
			break;
		case 1:
			avatarTitle2.setVisible(true);
			break;
		case 2:
			avatarTitle3.setVisible(true);
			break;
		case 3:
			avatarTitle4.setVisible(true);
			break;
		case 4:
			avatarTitle5.setVisible(true);
			break;
		default:
			break;
		}
	}

	public void replacePlupLoad() {
		Timer t = new Timer() {
			@Override
			public void run() {
				getPlupLoad(thiz);
			}
		};
		t.schedule(100);
	}
	
	public static native void replaceCkEditor(String editorId, CreateItem thiz) /*-{
	 	var noteId = editorId;
	  	var editor = $wnd.CKEDITOR.replace( noteId, {
	  		height: '150px',
	  		contentsCss : '',
	  		autoGrow_minHeight: 150,
//	  		autoGrow_maxHeight: 450,
	  		toolbarStartupExpanded : false,
	  		extraPlugins: 'autogrow',
	  		removeButtons: 'Image,Source',
	  	});
	  	
	  	editor.on("instanceReady",function() {
			thiz.@com.tranan.webstorage.client_admin.ui.CreateItem::setDataEditor()();
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
	
	public void uploadPhoto(final Long itemId) {
		PrettyGal.dataService.getUploadUrl(itemId, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				/*CreateItem.itemId = itemId;*/
				String itemIdstr = (itemId != null ? itemId.toString() : "");
				updatePlupLoadParam(itemIdstr);
				startPlupLoad(result, thiz);
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("!: Failed to get blob service upload url.");
			}
		});
	}
	
	public static void updateUploaderUrl() {
		PrettyGal.dataService.getUploadUrl(item.getId(), new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				updatePlupLoadUrl(result);
			}
	
			@Override
			public void onFailure(Throwable caught) {
//				Window.alert("!: Failed to get blob service upload url.");
			}
		});
	};
	
	public void onUploadComplete(String isUploadPhoto) {
		if(!isUpdate)
			NoticePanel.successNotice("Thêm sản phẩm thành công");
		else
			NoticePanel.successNotice("Thay đổi sản phẩm thành công");
//		clean();
		
		if(isUploadPhoto.equals("T")) {
			PrettyGal.dataService.getItemById(item.getId(), new AsyncCallback<Item>() {
				
				@Override
				public void onSuccess(Item result) {
					item = result;
					PrettyGal.UIC.getItemTable().AddItem(item, isUpdate);
					PrettyGal.placeController.goTo(new ItemPlace());
				}
				
				@Override
				public void onFailure(Throwable caught) {
					PrettyGal.UIC.getItemTable().AddItem(item, isUpdate);
					PrettyGal.placeController.goTo(new ItemPlace());
				}
			});
		} else {
			PrettyGal.UIC.getItemTable().AddItem(item, isUpdate);
			PrettyGal.placeController.goTo(new ItemPlace());
		}
	}
	
	public static native void getPlupLoad(CreateItem thiz) /*-{
		$wnd.max_file = 5;
		var files_remaining;          
		var isUploadPhoto = "F";
		var avatarIndex = -1;
		var uploadIndex = 0;
		var currentIndex = 0;
		
	  	$wnd.uploader = new $wnd.plupload.Uploader({
		    runtimes : 'html5,html4,flash',
		    container: $wnd.document.getElementById('container'),
		    browse_button : 'pick_files',
	    	drop_element: 'container',
		    url : '/',
		    use_query_string: false,
		   	dragdrop: true,
		    multipart : true,
		    multi_selection: true,
		    
		    //Enable resize
		    resize: {
				width: 1024,
				height: 1024
			},
		    
		    //Enable params
		    multipart_params : {
				itemId: '',
				isAvatar: 'F'
			},
		     
		    //Enable filter files
		    filters : {
		        max_file_size : '5mb',
		        mime_types: [
		            {title : "Image files", extensions : "jpg,png"}  
		        ]
		    },
		 
		    // Flash settings
		    flash_swf_url : '../plupload/Moxie.swf',
		    
		    // PreInit events, bound before any internal events
	        preinit : {
	            Init: function(up, info) {
	            },
	 
	            UploadFile: function(up, file) {
	            	if(files_remaining > 1)
			    		@com.tranan.webstorage.client_admin.ui.CreateItem::updateUploaderUrl()();
			    		
			    	if(uploadIndex == avatarIndex)
			    		$wnd.uploader.settings.multipart_params.isAvatar = 'T';
			    	else
			    		$wnd.uploader.settings.multipart_params.isAvatar = 'F';
	            }
	        },
		 
		    init: {
		        PostInit: function() {			 
	//	            $wnd.document.getElementById('imageTable').innerHTML = '';
		        },
		        
		        QueueChanged: function(up) {
	            	files_remaining = $wnd.uploader.files.length;
	//            	$wnd.document.getElementById('lbPhotosCount').innerHTML = files_remaining + " / Photos";
	 				if($wnd.max_file == 5 && $wnd.uploader.files.length == 0) {
	 					var el = $wnd.document.getElementById( 'samplePhoto' );
 						el.style.display = "";
 						
						thiz.@com.tranan.webstorage.client_admin.ui.CreateItem::setAvatarTitle(Ljava/lang/String;)(-1);
	 				}
	 				if($wnd.uploader.files.length != 0 || $wnd.max_file < 5) {
//	 					var e2 = $wnd.document.getElementById( 'avatarTitle' );
// 						e2.style.display = "";
	 				}
	        	},
		 
		        FilesAdded: function(up, files) {		     
//		        	if($wnd.uploader.files.length > 1)
//		        		$wnd.uploader.removeFile($wnd.uploader.files[0].id);
 
 					if($wnd.uploader.files.length > $wnd.max_file) {
 						up.splice($wnd.max_file);
 						files.splice($wnd.max_file);
 					}
 					
 					var el = $wnd.document.getElementById( 'samplePhoto' );
 					el.style.display = "none";
// 					if(el != null)
//						el.parentNode.removeChild( el );
		        			        	
				   	$wnd.plupload.each(files, function(file) {
						var img = new $wnd.o.Image();
					          
	                    img.onload = function() {
	                        // create a thumb placeholder
	                        var span = document.createElement('div');
	                        span.id = this.uid;	
	                        span.setAttribute("class", "CreateItem_s3");                          
//	                        $wnd.document.getElementById('imgPanel').innerHTML = '';                
	                        $wnd.document.getElementById('imgPanel').appendChild(span);
	                        	                     	                  
	                        // embed the actual thumbnail
	                        var widthcrop = 120;
	                        var heightcrop = 120;
	                        this.embed(span.id, {	  
	                            width: widthcrop,
	                            height: heightcrop,
	                            crop: true
	                        });	                                               	                       	                  
	                    };
	                    	                
		                img.onembedded = function() {
		                	var index = $wnd.uploader.files.indexOf(file);
							$wnd.uploader.files.splice(index, 1);
		                	$wnd.uploader.files.splice(currentIndex, 0, file);
		                	
		                	// drop thumbnails at different angles
//                        	$wnd.plupload.each(['', '-ms-', '-webkit-', '-o-', '-moz-'], function(prefix) {
//                          $wnd.document.getElementById(img.uid).style[prefix + 'transform'] = 'rotate('+ (Math.floor(Math.random() * 6) - 3) + 'deg)';
//                        	});

							// add remove image button
	                        var removeButton = document.createElement("a");
	                        var span = 	$wnd.document.getElementById(this.uid);
	                        span.appendChild(removeButton);
	                        removeButton.className = "CreateItem_s4";
	                        removeButton.innerHTML = "<i class='material-icons'>&#xE872;</i>";
	                        removeButton.onclick = function(index) {	                        		                        	
	                        	var img_index = $wnd.uploader.files.indexOf(file);
	                        	if(avatarIndex == img_index) {
	                        		avatarIndex = -1;
	                        		thiz.@com.tranan.webstorage.client_admin.ui.CreateItem::setAvatarTitle(Ljava/lang/String;)(avatarIndex);
	                        	}
	                        	if(avatarIndex > img_index) {
	                        		avatarIndex--;
	                        		thiz.@com.tranan.webstorage.client_admin.ui.CreateItem::setAvatarTitle(Ljava/lang/String;)(avatarIndex + 5 - $wnd.max_file);
	                        	}
	                        	
	                        	span.parentNode.removeChild(span);
	                        	$wnd.uploader.removeFile(file);
	                        	currentIndex--;
	                        };
	                        
	                        // add avatar button
		                    var avatarButton = document.createElement("span");
		                    var span = 	$wnd.document.getElementById(this.uid);
		                    span.appendChild(avatarButton);
		                    avatarButton.className = "CreateItem_s20";
		                    avatarButton.innerHTML = "Ảnh Đại Diện";       
		                    avatarButton.onclick = function(index) {
		                    	var img_index = $wnd.uploader.files.indexOf(file);
		                    	avatarIndex = img_index;
	                        	thiz.@com.tranan.webstorage.client_admin.ui.CreateItem::setAvatarTitle(Ljava/lang/String;)(img_index + 5 - $wnd.max_file);
	                        };    
	                        
	                        currentIndex++;
	                    };
		                              
	                	img.load(file.getSource());
			  		});
		        },		      	
		 
		        UploadProgress: function(up, file) {
	//	        	var total_files =  $wnd.uploader.files.length;
	//	        	var files_uploaded = total_files - files_remaining;
	//	        	var total_percent = ((file.percent/100 * 1/total_files)*100) + ((files_uploaded * 1/total_files)*100);
	//	            $wnd.document.getElementById('lbUploadProgress').innerHTML = '<span>' + Math.ceil(total_percent) + "%</span>";
		        },
		        
		        FileUploaded: function(up, file, info) {
	                files_remaining--;
	                uploadIndex++;
	                isUploadPhoto = "T";
	            },
	            
	            UploadComplete: function(up, files) {      
	         		up.splice(0);
 						files.splice(0);
						
					thiz.@com.tranan.webstorage.client_admin.ui.CreateItem::onUploadComplete(Ljava/lang/String;)(isUploadPhoto);
	            },
		 
		        Error: function(up, err) {
	//	            $wnd.document.getElementById('console').innerHTML += "\nError #" + err.code + ": " + err.message;
		        }
		    }
		});
		 
		$wnd.uploader.init();
	}-*/;
	
	public static native void updatePlupLoadUrl(String uploadUrl) /*-{
		var upload_url = uploadUrl;
		if($wnd.uploader != null)
			$wnd.uploader.settings.url = upload_url;
	}-*/;
	
	public static native void updatePlupLoadParam(String par1) /*-{
		var itemId = par1;
		if($wnd.uploader != null) {
			$wnd.uploader.settings.multipart_params.itemId = itemId;
		}
	}-*/;
	
	public static native void startPlupLoad(String uploadUrl, CreateItem thiz) /*-{
		var upload_url = uploadUrl;
		if($wnd.uploader != null && $wnd.uploader.files.length > 0) {
			$wnd.uploader.settings.url = upload_url;
			$wnd.uploader.start();
		} else {
			var b = "F";
			thiz.@com.tranan.webstorage.client_admin.ui.CreateItem::onUploadComplete(Ljava/lang/String;)(b);
		}
	}-*/;
	
	public static native void setFileQueued(int max_file) /*-{
		$wnd.max_file = max_file;
	}-*/;
	
	public static native String getFileQueued() /*-{
		return $wnd.uploader.files.length;
	}-*/;
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent e) {
		NoticePanel.onLoading();
		
		if(item == null)
			item = new Item();
		
		if(avatarIndex != null) {
			int index = Integer.valueOf(avatarIndex);
			Long photo_id = item.getPhoto_ids().get(index);
			item.getPhoto_ids().remove(index);
			item.getPhoto_ids().add(0, photo_id);
		}
		
		item.setName(nameText.getText());
		if(!costText.getText().isEmpty())
			item.setCost(Long.valueOf(costText.getText().replaceAll("[.]", "")));
		else
			item.setCost(0L);
		if(!priceText.getText().isEmpty())
			item.setPrice(Long.valueOf(priceText.getText().replaceAll("[.]", "")));
		else
			item.setPrice(0L);
		if(!saleText.getText().isEmpty())
			item.setSale(Integer.valueOf(saleText.getText()));
		else
			item.setSale(0);
		item.getType().clear();
		if(listTypeBox.isEmpty()) {
			Type type = new Type();
			type.setName(Item.DEFAULT_TYPE);
			if(!quantityText.getText().isEmpty())
				type.setQuantity(quantityText.getValue());
			else
				type.setQuantity(0);
			item.getType().add(type);
		}
		else {
			for(TypeBox type_box: listTypeBox) {
				Type type = new Type();
				type.setName(type_box.type.getText());
				if(!type_box.quantity.getText().isEmpty())
					type.setQuantity(type_box.quantity.getValue());
				else
					type.setQuantity(0);
				item.getType().add(type);
			}
		}
		item.getCatalog_ids().clear();
		for(Catalog catalog: itemCatalog) {
			item.getCatalog_ids().add(catalog.getId());
		}
		item.setDescription(getDataCustomEditor("descriptionTxb"));
		item.setAvatar_url("");
		
		PrettyGal.dataService.createItem(item, LoginPage.id_token, new AsyncCallback<Item>() {
			
			@Override
			public void onSuccess(Item result) {
				item = result;
				skipCheckItemChange = true;
				
				String file_queued = String.valueOf(getFileQueued());
				if(file_queued.equals("0")) {
					if(!isUpdate)
						NoticePanel.successNotice("Thêm sản phẩm thành công");
					else
						NoticePanel.successNotice("Thay đổi sản phẩm thành công");
					PrettyGal.UIC.getItemTable().AddItem(result, isUpdate);
					PrettyGal.placeController.goTo(new ItemPlace());
				}
				else {
					uploadPhoto(result.getId());
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				NoticePanel.failNotice(caught.getMessage());
			}
		});
	}
	
	void addTypeRow(String name, int quantity) {
		final HTMLPanel panel1 = new HTMLPanel("");
		panel1.setStyleName("CreateItem_s7");
		Label lb1 = new Label("Chủng loại");
		lb1.setStyleName("CreateItem_s8");
		TextBox txb1 = new TextBox();
		txb1.setStyleName("CreateItem_s9");
		txb1.setText(name);
		Label lb2 = new Label("Số lượng");
		lb2.setStyleName("CreateItem_s8");
		IntegerBox txb2 = new IntegerBox();
		txb2.setValue(quantity);
		txb2.setStyleName("CreateItem_s9");
		txb2.getElement().setAttribute("type", "number");
		HTMLPanel panel2 = new HTMLPanel("<i class='material-icons'>&#xE872;</i>");
		panel2.setStyleName("CreateItem_s10");
		Anchor a = new Anchor();
		a.setStyleName("CreateItem_s11");
		
		panel2.add(a);
		panel1.add(lb1);
		panel1.add(txb1);
		panel1.add(lb2);
		panel1.add(txb2);
		panel1.add(panel2);
		
		itemTypeTable.add(panel1);
		quantityPanel.setVisible(false);
		
		final TypeBox tp = new TypeBox(txb1, txb2);
		listTypeBox.add(tp);
		
		a.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				itemTypeTable.remove(panel1);
				listTypeBox.remove(tp);
				if(listTypeBox.isEmpty())
					quantityPanel.setVisible(true);
			}
		});
	}
	
	@UiHandler("addItemTypeButton") 
	void onAddItemTypeButtonClick(ClickEvent e) {
		addTypeRow("", 0);
	}
	
	@UiHandler("addCatalogButton") 
	void onAddCatalogButtonClick(ClickEvent e) {
		final ListCatalogDialog dialog = new ListCatalogDialog(new ListCatalogDialog_Listener() {
			
			@Override
			public void onSelectedCatalog(List<Catalog> selectedCatalogs) {
				itemCatalog = selectedCatalogs;
				addCatalogListBox(itemCatalog);
			}
		});
		
		dialog.setCatalogs(ItemTable.listCatalog, itemCatalog);
		Timer t = new Timer() {

			@Override
			public void run() {
				dialog.center();
				avatarTitlePanel.setVisible(false);
			}};
		t.schedule(50);
		
		dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				avatarTitlePanel.setVisible(true);
			}
		});
	}
	
	@UiHandler("exitButton") 
	void onExitButtonClick(ClickEvent e) {
		skipCheckItemChange = true;
		PrettyGal.placeController.goTo(new ItemPlace());
	}
	
	@UiHandler("exitCatalogButton") 
	void onExitCatalogButtonClick(ClickEvent e) {
		skipCheckItemChange = true;
		PrettyGal.placeController.goTo(new ItemPlace());
	}
	
	@UiHandler("itemTabBtn") 
	void onItemTabClick(ClickEvent e) {
		addItemTab.setVisible(true);
		editCatalogTab.setVisible(false);
		itemTabHeader.setStyleName("CreateItem_s12");
		catalogTabHeader.setStyleName("CreateItem_s12_deactive");
		
		itemCatalog.clear();
		for(Catalog catalog: ItemTable.listCatalog) {
			if(item != null && item.getCatalog_ids().contains(catalog.getId())) {
				itemCatalog.add(catalog);
			}
		}
		addCatalogListBox(itemCatalog);
	}
	
	@UiHandler("catalogTabBtn") 
	void onCatalogTabClick(ClickEvent e) {
		nameCatalog.setText("");
		addItemTab.setVisible(false);
		editCatalogTab.setVisible(true);
		itemTabHeader.setStyleName("CreateItem_s12_deactive");
		catalogTabHeader.setStyleName("CreateItem_s12");
		
		catalogTable.clear();
		if(ItemTable.listCatalog.isEmpty())
			catalogTable.setVisible(false);
		else 
			showCatalogView(ItemTable.listCatalog);
	}
	
	@UiHandler("saveCatalogButton") 
	void onSaveCatalogButtonClick(ClickEvent e) {
		if(!nameCatalog.getText().isEmpty()) {
			NoticePanel.onLoading();
		 
			nameCatalog.removeStyleName("ItemCatalog_validateNameFail");
			Catalog catalog = new Catalog();
		 
			catalog.setName(nameCatalog.getText());
			PrettyGal.dataService.createCatalog(catalog, LoginPage.id_token, new AsyncCallback<Catalog>() {			
				@Override
				public void onSuccess(Catalog result) {
					NoticePanel.successNotice("Thêm catalog thành công");
					ItemTable.listCatalog.add(0, result);
					showCatalogView(ItemTable.listCatalog);
					nameCatalog.setText("");
				}
				
				@Override
				public void onFailure(Throwable caught) {
					NoticePanel.failNotice(caught.getMessage());
				}
			});
		}
		else
			nameCatalog.addStyleName("ItemCatalog_validateNameFail");
	}
	
	private void showCatalogView(List<Catalog> catalogs) {
		catalogTable.clear();
		catalogTable.setVisible(true);
		catalogTable.add(catalogTableTitle);
		
		for(final Catalog catalog: catalogs) {
			HTMLPanel panel1 = new HTMLPanel("");
			panel1.setStyleName("CreateItem_s13");
			final TextBox txb1 = new TextBox();
			txb1.setStyleName("CreateItem_s14");
			txb1.setText(catalog.getName());
			HTMLPanel panel2 = new HTMLPanel("");
			panel2.setStyleName("CreateItem_s15b");
			panel2.getElement().setInnerHTML("<div style='margin-top:2px; margin-left: 15px; margin-right: -10px;'>"
												+ "<i class='material-icons'>&#xE872;</i>"
												+ "</div>");
			Anchor deleteBtn = new Anchor();
			deleteBtn.setStyleName("CreateItem_s11");
			
			final HTMLPanel panel3 = new HTMLPanel("");
			panel3.setStyleName("CreateItem_s15");
			panel3.getElement().setInnerHTML("<div style='margin-top:2px; margin-left: 15px;'>"
												+ "<i class='material-icons'>&#xE254;</i>"
												+ "</div>");
			Anchor editBtn = new Anchor();
			editBtn.setStyleName("CreateItem_s11");
			panel3.add(editBtn);
			
			final HTMLPanel panel4 = new HTMLPanel("");
			panel4.setStyleName("CreateItem_s15");
			panel4.getElement().setInnerHTML("<div style='margin-top:2px; margin-left: 15px;'>"
												+ "<i class='material-icons'>&#xE161;</i>"
												+ "</div>");
			Anchor saveBtn = new Anchor();
			saveBtn.setStyleName("CreateItem_s11");
			panel4.add(saveBtn);
			panel4.setVisible(false);
			
			txb1.addFocusHandler(new FocusHandler() {				
				@Override
				public void onFocus(FocusEvent event) {
					panel3.setVisible(false);
					panel4.setVisible(true);
				}
			});
			txb1.addBlurHandler(new BlurHandler() {		
				@Override
				public void onBlur(BlurEvent event) {
					panel3.setVisible(true);
					panel4.setVisible(false);
					if(!txb1.getText().equals(catalog.getName())) {
						NoticePanel.onLoading();
						catalog.setName(txb1.getText());
						PrettyGal.dataService.createCatalog(catalog, LoginPage.id_token, new AsyncCallback<Catalog>() {
							
							@Override
							public void onSuccess(Catalog result) {
								int index = ItemTable.listCatalog.indexOf(catalog);
								ItemTable.listCatalog.remove(index);
								ItemTable.listCatalog.add(index, catalog);
								showCatalogView(ItemTable.listCatalog);
								NoticePanel.endLoading();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								NoticePanel.failNotice(caught.getMessage());
							}
						});
					}
				}
			});
			txb1.addKeyPressHandler(new KeyPressHandler()
            {
                @Override
                public void onKeyPress(KeyPressEvent event_)
                {
                    boolean enterPressed = KeyCodes.KEY_ENTER == event_
                            .getNativeEvent().getKeyCode();
                    if (enterPressed)
                    {
                        txb1.setFocus(false);
                    }
                }
            });
			
			deleteBtn.addClickHandler(new ClickHandler() {			
				@Override
				public void onClick(ClickEvent event) {
					if(Window.confirm("Bạn muốn xóa catalog này?")) {
						NoticePanel.onLoading();
						PrettyGal.dataService.deleteCatalog(catalog, LoginPage.id_token, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								NoticePanel.successNotice("Xóa catalog thành công");
								ItemTable.listCatalog.remove(catalog);
								showCatalogView(ItemTable.listCatalog);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								NoticePanel.failNotice(caught.getMessage());
							}
						});
					}
				}
			});
			
			editBtn.addClickHandler(new ClickHandler() {			
				@Override
				public void onClick(ClickEvent event) {
					txb1.setFocus(true);
				}
			});
			
			saveBtn.addClickHandler(new ClickHandler() {			
				@Override
				public void onClick(ClickEvent event) {
					txb1.setFocus(false);
				}
			});
			
			panel2.add(deleteBtn);
			panel1.add(txb1);
			panel1.add(panel2);
			panel1.add(panel3);
			panel1.add(panel4);
			catalogTable.add(panel1);
		}
	}

}
