package dk.javacode.cah.rest;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;

import dk.javacode.cah.database.service.BlackCardService;
import dk.javacode.cah.database.service.interfaces.IBlackCardService;
import dk.javacode.cah.model.BlackCard;
import dk.javacode.cah.model.dto.CardDetailDTO;
import dk.javacode.proxy.InterceptorProxy;

public class BlackCardSingleResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(BlackCardSingleResource.class);
	
	private IBlackCardService blackCardService;
	
	public BlackCardSingleResource() {
		super();
		blackCardService = InterceptorProxy.buildProxy(IBlackCardService.class, new BlackCardService());
	}
		
	@Delete("json")
	public Representation deleteCard(Representation r) throws IOException, JSONException, SQLException {
		log.debug("deleteCard");

		String idstr = this.getAttribute("id");
		
		if (idstr == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. No id provided for delete.");			
		}
		
		int id = Integer.parseInt(idstr);
		
		BlackCard card = blackCardService.findById(id);
		if (card == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. Card with id: " + id + " not found.");
		}
		blackCardService.softDeleteCard(id);
		setStatus(Status.SUCCESS_OK);
		return new StringRepresentation(card.getId() + "");
	}
	
	@Get("json")
	public Representation getCard(Representation r) throws IOException, JSONException, SQLException {
		log.debug("getSingleCard");
		
		String idstr = this.getAttribute("id");
		
		if (idstr == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. No id provided for get single card.");			
		}
		
		int id = Integer.parseInt(idstr);
		
		CardDetailDTO<BlackCard> card = null;
		try {
			card = blackCardService.findWithDetailsById(id);
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Unexpected error retrieving singe black card.", e);
		}
		if (card == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Card with id: " + id + " not found.");
		}
		JSONObject out = new JSONObject(card.getCard());
		JSONArray rv = new JSONArray();
		for (BlackCard e : card.getRevisions()) {
			JSONObject o = new JSONObject(e);
			o.put("createdtstamp", e.getCreated().getTime());
			rv.put(o);
		}
		out.put("revisions", rv);	
		setStatus(Status.SUCCESS_OK);
		return new JsonRepresentation(out);
	}
}
