package com.tranan.webstorage.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.googlecode.objectify.Key;
import com.tranan.webstorage.shared.Item;
import com.tranan.webstorage.shared.Photo;

@SuppressWarnings("serial")
public class UploadService extends HttpServlet implements Servlet {

	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	private ImagesService imagesService = ImagesServiceFactory
			.getImagesService();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		List<BlobKey> blobKeys = blobs.get("file");
		
		String isAvatar = String.valueOf(req.getParameter("isAvatar").replaceAll(",", ""));
		if(isAvatar == null)
			isAvatar = "F";

		if (blobKeys != null) {
			for (BlobKey key : blobKeys) {
				// get file name on blob info
				BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(key);
				long size = blobInfo.getSize();
				if (size > 0) {
					String encodedFilename = URLEncoder.encode(
							blobInfo.getFilename(), "utf-8");
					encodedFilename.replaceAll("\\+", "%20");

					// set photo upload info
					Photo photo = new Photo();
					if (req.getParameter("itemId") != null)
						photo.setItemId(Long.valueOf(req.getParameter("itemId")
								.replaceAll(",", "")));

					photo.setBlobkey(key.getKeyString());

					String serveUrl = imagesService.getServingUrl(
							ServingUrlOptions.Builder.withBlobKey(key))
							.replace("0.0.0.0", "127.0.0.1");
					photo.setServeUrl(serveUrl);

					Key<Photo> keyPhoto = ofy().save().entity(photo).now();
					Photo rtn = ofy().load().key(keyPhoto).now();

					// save id Photo on Item
					Long itemId = Long.valueOf(req.getParameter("itemId")
							.replaceAll(",", ""));
					Item item = ofy().load().type(Item.class).id(itemId).now();
					if (item != null) {
						if(isAvatar.equals("T") )
							item.getPhoto_ids().add(0, rtn.getId());
						else
							item.getPhoto_ids().add(rtn.getId());
						ofy().save().entity(item);
					}
				} else {
					blobstoreService.delete(key);
				}
			}
		}
	}
}
