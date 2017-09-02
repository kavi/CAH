package dk.javacode.cah.rest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class StaticFolderResource extends ServerResource {
	
	public static String ROOT = "resources/web";
	
	
	private static Logger log = Logger.getLogger(StaticFolderResource.class);
	
	
	@Get
	public Representation represent() {
		String fileattribute = getAttribute("file");
		try {
			fileattribute = URLDecoder.decode(fileattribute, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			log.error("Unable to decode fileattribute", e);
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation("Internal Server Error");
		}
		String filename = ROOT + "/" + fileattribute;

		File file = new File(filename);
		
		if (!file.exists()) {			
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return new StringRepresentation("404 - " + file + " not found.");
		}
		if (file.isDirectory()) {
			File index = getIndexFile(file);
			if (index == null) {
				return getDirectoryListing(file);
			} else {
				filename = index.getAbsolutePath();
				Reference originalReference = getOriginalRef();
				originalReference.addSegment("index.html");
				getResponse().redirectPermanent(originalReference);
				return null;
			}
		}
		String extension = FilenameUtils.getExtension(filename);
		
		MediaType mt = MediaType.APPLICATION_ALL;
		if ("html".equalsIgnoreCase(extension) || "htm".equalsIgnoreCase(extension)) {
			mt = MediaType.TEXT_HTML;
		} else if ("js".equalsIgnoreCase(extension)) {
			mt = MediaType.TEXT_JAVASCRIPT;
		} else if ("css".equalsIgnoreCase(extension)) {
			mt = MediaType.TEXT_CSS;
		} else if ("png".equalsIgnoreCase(extension)) {
			mt = MediaType.IMAGE_PNG;
		} else if ("jpg".equalsIgnoreCase(extension) || "jpeg".equalsIgnoreCase(extension)) {
			mt = MediaType.IMAGE_JPEG;
		} else if ("pdf".equalsIgnoreCase(extension)) {
			mt = MediaType.APPLICATION_PDF;
		} 
		FileRepresentation rep = new FileRepresentation(filename, mt);
		return rep;
	}

	private File getIndexFile(File file) {
		File index = null;		
		for (File f : file.listFiles()) {
			if (f.isFile() && f.getName().equals("index.html") && f.canRead()) {
				index = f.getAbsoluteFile();
			}
		}
		return index;
	}

	private Representation getDirectoryListing(File file) {
		JSONObject out = new JSONObject();
		JSONArray arr = new JSONArray();
		for (File f : file.listFiles()) {
			arr.put(f.getName());
		}
		try {
			out.put("files", arr);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return new JsonRepresentation(out);
	}
}
