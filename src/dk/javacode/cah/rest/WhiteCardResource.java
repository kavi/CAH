package dk.javacode.cah.rest;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

import dk.javacode.cah.database.service.DeckService;
import dk.javacode.cah.database.service.WhiteCardService;
import dk.javacode.cah.database.service.interfaces.IDeckService;
import dk.javacode.cah.database.service.interfaces.IWhiteCardService;
import dk.javacode.cah.model.Deck;
import dk.javacode.cah.model.WhiteCard;
import dk.javacode.proxy.InterceptorProxy;

public class WhiteCardResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(WhiteCardResource.class);

	private IDeckService deckService;
	private IWhiteCardService whiteCardService;

	public WhiteCardResource() {
		super();
		whiteCardService = InterceptorProxy.buildProxy(IWhiteCardService.class, new WhiteCardService());
		deckService = InterceptorProxy.buildProxy(IDeckService.class, new DeckService());
	}

	@Put("json")
	public Representation editCard(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.debug("createCard");
		Integer id = null;
		String text = null;
		Boolean safeForWork = null;
		Boolean disabled = null;
		try {
			JSONObject json = new JSONObject(entityText);
			id = json.getInt("id");
			text = json.getString("newtext");
			safeForWork = json.getBoolean("safeForWork");
			if (json.has("disabled")) {
				disabled = json.getBoolean("disabled");
			}
		} catch (RuntimeException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. Unable to parse JSON");			
		}
		WhiteCard originalcard = whiteCardService.findById(id);
		if (originalcard == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. Card with id: " + id + " not found.");
		}
		
		whiteCardService.editCard(id, user, text, safeForWork, disabled);

		setStatus(Status.SUCCESS_NO_CONTENT);
		return new EmptyRepresentation();
	}

	@Post("json")
	public Representation createCard(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.debug("createCard: " + entityText);

		String deckidstr = getAttribute("deck_id");
		if (deckidstr == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. No deckId");
		}
		Integer deckId = null; 
		try {
			deckId = Integer.parseInt(deckidstr);
		} catch (NumberFormatException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Invalid deckId: " + deckId, new RuntimeException());
		}
		
		Deck deck = deckService.findById(deckId);
		if (deck == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. No deck with id: " + deckId, new RuntimeException());			
		}

		JSONObject json = new JSONObject(entityText);
		boolean safeForWork = json.getBoolean("safeForWork");
		String text = json.getString("text");
		WhiteCard card = new WhiteCard(text);
		card.setDeckId(deck.getId());
		card.setSafeForWork(safeForWork);
		whiteCardService.createCard(user, card);

		setStatus(Status.SUCCESS_CREATED);
		return new StringRepresentation(card.getId() + "");
	}
}
