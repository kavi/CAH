package dk.javacode.cah.rest;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

import dk.javacode.cah.database.service.BlackCardService;
import dk.javacode.cah.database.service.interfaces.IBlackCardService;
import dk.javacode.cah.model.BlackCard;
import dk.javacode.proxy.InterceptorProxy;

public class BlackCardResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(BlackCardResource.class);

	private IBlackCardService blackCardService;

	public BlackCardResource() {
		super();
		blackCardService = InterceptorProxy.buildProxy(IBlackCardService.class, new BlackCardService());
	}

	@Put("json")
	public Representation editCard(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.debug("editCard");
		int id;
		String text;
		int cardsToDraw;
		int cardsToPick;
		boolean safeForWork;

		try {
			JSONObject json = new JSONObject(entityText);

			id = json.getInt("id");
			text = json.getString("newtext");
			cardsToDraw = json.getInt("cardsToDraw");
			cardsToPick = json.getInt("cardsToPick");
			safeForWork = json.getBoolean("safeForWork");

			log.debug("Set values on black card. text=" + text + ", cardsToDraw=" + cardsToDraw + ", cardsToPick=" + cardsToPick + " on id: " + id);
		} catch (Exception e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			log.debug("json: " + entityText);
			return getStringRepresentation("Bad request. Unparseble json.");
		}

		BlackCard originalcard = null;
		try {
			originalcard = blackCardService.findById(id);
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Error looking up card with id: " + id, e);
		}
		if (originalcard == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. Card with id: " + id + " not found.");
		}

		try {
			blackCardService.editCard(id, user, text, cardsToPick, cardsToDraw, safeForWork);
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation(e.getMessage(), e);
		}

		setStatus(Status.SUCCESS_OK);
		return new StringRepresentation(originalcard.getId() + "");

	}

	@Post("json")
	public Representation createCard(JsonRepresentation r) throws IOException, JSONException, SQLException {
		String input = entityText; // this.getRequestEntity().getText();
		log.debug("createCard: " + input);

		String deckIdStr = getAttribute("deck_id");
		if (deckIdStr == null) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Bad routing. No deckIdStr in URL.");
		}
		Integer deckId = null;
		try {
			deckId = Integer.parseInt(deckIdStr);
		} catch (RuntimeException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. No deckId must be a number (" + deckIdStr + ")");
		}

		String text;
		int cardsToDraw;
		int cardsToPick;
		boolean safeForWork;
		try {
			JSONObject json = new JSONObject(input);
			text = json.getString("text");
			cardsToDraw = json.getInt("cardsToDraw");
			cardsToPick = json.getInt("cardsToPick");
			safeForWork = json.getBoolean("safeForWork");
		} catch (Exception e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. Unparsable json.");
		}
		BlackCard card = new BlackCard();
		card.setDeckId(deckId);
		card.setText(text);
		card.setCardsToDraw(cardsToDraw);
		card.setCardsToPick(cardsToPick);
		card.setSafeForWork(safeForWork);
		try {
			blackCardService.createCard(user, card);
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation(e.getMessage(), e);

		}

		setStatus(Status.SUCCESS_CREATED);
		return new StringRepresentation(card.getId() + "");
	}
}
