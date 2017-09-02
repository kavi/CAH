package dk.javacode.cah.rest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import dk.javacode.cah.database.service.BlackCardService;
import dk.javacode.cah.database.service.DeckService;
import dk.javacode.cah.database.service.WhiteCardService;
import dk.javacode.cah.database.service.interfaces.IBlackCardService;
import dk.javacode.cah.database.service.interfaces.IDeckService;
import dk.javacode.cah.database.service.interfaces.IWhiteCardService;
import dk.javacode.cah.model.BlackCard;
import dk.javacode.cah.model.Deck;
import dk.javacode.cah.model.WhiteCard;
import dk.javacode.proxy.InterceptorProxy;
import dk.javacode.srsm.exceptions.MappingException;

public class DeckSingleResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(DeckSingleResource.class);

	private IDeckService deckService;
	private IWhiteCardService whiteCardService;
	private IBlackCardService blackCardService;

	public DeckSingleResource() {
		super();
		whiteCardService = InterceptorProxy.buildProxy(IWhiteCardService.class, new WhiteCardService());
		blackCardService = InterceptorProxy.buildProxy(IBlackCardService.class, new BlackCardService());
		deckService = InterceptorProxy.buildProxy(IDeckService.class, new DeckService());
	}

	@Get
	public Representation getDeck() throws SQLException, MappingException {
		String deckidstr = getAttribute("id");
		log.info("getDeck(" + deckidstr + ") for user: " + user);
		boolean familyFilter = user.isFamilyFilter() != null ? user.isFamilyFilter() : true;
		if (deckidstr == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. No deckId");
		}

		Integer deckId = null;
		try {
			deckId = Integer.parseInt(deckidstr);
		} catch (NumberFormatException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. Invalid deckId: " + deckId);
		}

		Deck deck = null;
		try {
			deck = deckService.findById(deckId);
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Error. Unable to look up deck with id: " + deckId, e);
		}

		if (deck == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. No deck with id: " + deckId);
		}

		try {
			List<Integer> deckIds = new ArrayList<Integer>();
			deckIds.add(deck.getId());

			List<WhiteCard> whitecards = whiteCardService.findAllAvailable(familyFilter, deckIds);
			List<BlackCard> blackcards = blackCardService.findAllAvailable(familyFilter, deckIds);

			deck.setBlackCards(blackcards);
			deck.setWhiteCards(whitecards);

			setStatus(Status.SUCCESS_OK);
			JSONObject jsonObject = new JSONObject(deck);
			Representation r = new JsonRepresentation(jsonObject);
			return r;
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Unexpected error retrieving deck.", e);
		}
	}

}
