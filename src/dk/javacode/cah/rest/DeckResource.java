package dk.javacode.cah.rest;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import dk.javacode.cah.database.service.DeckService;
import dk.javacode.cah.database.service.interfaces.IDeckService;
import dk.javacode.cah.model.Deck;
import dk.javacode.cah.model.DeckLanguage;
import dk.javacode.cah.util.JsonHelper;
import dk.javacode.proxy.InterceptorProxy;

public class DeckResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(DeckResource.class);

	private IDeckService deckService;

	public DeckResource() {
		super();
		deckService = InterceptorProxy.buildProxy(IDeckService.class, new DeckService());
	}

	@Post("json")
	public Representation createDeck() {
		log.info("createDeck");
		try {
			// input = this.getRequest().getEntityAsText();
			log.info(entityText);
		} catch (Exception e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Unable to get input", e);
		}

		String langidstr = getAttribute("language_id");
		if (langidstr == null) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Bad routing. No language id");
		}
		int languageId;
		try {
			languageId = Integer.parseInt(langidstr);
		} catch (NumberFormatException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request", e);
		}

		DeckLanguage language = deckService.findLanguageById(languageId);
		if (language == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Language not found: " + languageId);
		}

		String name;
		try {
			JSONObject json = new JSONObject(entityText);
			name = json.getString("name");
		} catch (Exception e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. Unparsable json.");
		}
		Deck existing = deckService.findByName(language, name);
		if (existing != null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Duplicate name.");			
		}
		Deck deck = new Deck();
		deck.setName(name);
		deck.setLanguage(language);
		deck.setOwner(user);
		try {
			deckService.createDeck(deck);
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Database error: " + e.getMessage(), e);
		}

		setStatus(Status.SUCCESS_CREATED);
		log.info("[" + user + "] created deck: " + deck);
		JSONObject jsonout = new JSONObject(deck);
		return new JsonRepresentation(jsonout);
	}

	@Get
	public Representation getDecks() {
		log.trace("getDecks");
		String langidstr = getAttribute("language_id");
		if (langidstr == null) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Bad routing. No language id");
		}
		int languageId;
		try {
			languageId = Integer.parseInt(langidstr);
		} catch (NumberFormatException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request", e);
		}

		try {
			DeckLanguage language = deckService.findLanguageById(languageId);
			List<Deck> decks = deckService.findByLanguage(language);

			setStatus(Status.SUCCESS_OK);
			JSONArray jsonArray = JsonHelper.toArray(decks);
			Representation r = new JsonRepresentation(jsonArray);
			return r;
		} catch (RuntimeException e) {
			log.error("Unexpected error", e);
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation("Unexpected error");
		}
	}

}
