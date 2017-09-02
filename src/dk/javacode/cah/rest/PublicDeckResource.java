package dk.javacode.cah.rest;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import dk.javacode.cah.database.service.DeckService;
import dk.javacode.cah.database.service.interfaces.IDeckService;
import dk.javacode.cah.model.Deck;
import dk.javacode.cah.model.DeckLanguage;
import dk.javacode.cah.util.JsonHelper;
import dk.javacode.proxy.InterceptorProxy;

public class PublicDeckResource extends ServerResource {
	private static final Logger log = Logger.getLogger(PublicDeckResource.class);

	private IDeckService deckService;

	public PublicDeckResource() {
		super();
		deckService = InterceptorProxy.buildProxy(IDeckService.class, new DeckService());
	}

	@Get
	public Representation getDecks() {
		log.trace("getDecks");
		String langidstr = getAttribute("language_id");
		if (langidstr == null) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation("Bad routing. No language id");
		}
		int languageId;
		try {
			languageId = Integer.parseInt(langidstr);
		} catch (NumberFormatException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			log.warn("Bad request in getDecks", e);
			return new StringRepresentation("Bad request");
		}

		try {
			DeckLanguage language = deckService.findLanguageById(languageId);
			List<Deck> decks = deckService.findByLanguage(language);

			setStatus(Status.SUCCESS_OK);
			JSONArray jsonArray = JsonHelper.toArray(decks);
			Representation r = new JsonRepresentation(jsonArray);
			return r;
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			log.error("Unexpected error", e);
			return new StringRepresentation("Unexpected error");
		}
	}

}
