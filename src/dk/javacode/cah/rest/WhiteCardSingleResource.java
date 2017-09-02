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

import dk.javacode.cah.database.service.WhiteCardService;
import dk.javacode.cah.database.service.interfaces.IWhiteCardService;
import dk.javacode.cah.model.WhiteCard;
import dk.javacode.cah.model.dto.CardDetailDTO;
import dk.javacode.proxy.InterceptorProxy;

public class WhiteCardSingleResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(WhiteCardSingleResource.class);

	private IWhiteCardService whiteCardService;

	public WhiteCardSingleResource() {
		super();
		whiteCardService = InterceptorProxy.buildProxy(IWhiteCardService.class, new WhiteCardService());
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

		WhiteCard card = whiteCardService.findById(id);
		if (card == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. Card with id: " + id + " not found.");
		}

		try {
			whiteCardService.deleteCard(id);
			setStatus(Status.SUCCESS_OK);
			JSONObject out = new JSONObject();
			out.put("id", id);
			return new JsonRepresentation(out);
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);			
			return getStringRepresentation("Unexpected error", e);
		}
	}

	@Get("json")
	public Representation getCard(Representation r) throws IOException, JSONException, SQLException {
		log.debug("getSingleCard");

		String idstr = this.getAttribute("id");

		if (idstr == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. No id provided for get single card.");
		}

		int id;
		try {
			id = Integer.parseInt(idstr);
		} catch (RuntimeException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Invalid id provided for get single card.", e);
		}

		try {
			CardDetailDTO<WhiteCard> card = whiteCardService.findWithDetailsById(id);
			if (card == null) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return getStringRepresentation("Bad request. Card with id: " + id + " not found.");
			}
			JSONObject out = new JSONObject(card.getCard());
			JSONArray rv = new JSONArray();
			for (WhiteCard e : card.getRevisions()) {
				JSONObject o = new JSONObject(e);
				o.put("createdtstamp", e.getCreated().getTime());
				rv.put(o);
			}
			out.put("revisions", rv);
			setStatus(Status.SUCCESS_OK);
			return new JsonRepresentation(out);
		} catch (RuntimeException e) {
			log.warn("Unexpected error", e);
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Bad request. Card with id: " + id + " not found.");
		}
	}
}
