package dk.javacode.cah.rest.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import dk.javacode.cah.database.dao.GameDao;
import dk.javacode.cah.database.dao.impl.MySqlGameDao;
import dk.javacode.cah.database.service.DeckService;
import dk.javacode.cah.database.service.GameActionService;
import dk.javacode.cah.database.service.interfaces.IDeckService;
import dk.javacode.cah.database.service.interfaces.IGameActionService;
import dk.javacode.cah.model.Deck;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.rest.BasicAuthResource;
import dk.javacode.cah.util.JsonHelper;
import dk.javacode.notjsr.NotJSRValidator;
import dk.javacode.notjsr.ValidationError;
import dk.javacode.proxy.InterceptorProxy;

public class GameResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(GameResource.class);

	private GameDao gameDao;

	private IGameActionService gameActionService;
	private IDeckService deckService;

	public GameResource() {
		super();
		gameActionService = InterceptorProxy.buildProxy(IGameActionService.class, new GameActionService());
		deckService = InterceptorProxy.buildProxy(IDeckService.class, new DeckService());
		gameDao = InterceptorProxy.buildProxy(GameDao.class, new MySqlGameDao());
	}

	@Get("json")
	public Representation listGames(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.trace("listGames");
		try {
			setStatus(Status.SUCCESS_OK);
			List<Game> games = gameDao.findAll();
			List<Game> fullgames = new ArrayList<Game>(games.size());
			for (Game g : games) {
				Game fg = gameDao.findByName(g.getName());
				for (Player p : fg.getPlayers()) {
					if (p.getUser().equals(user)) {
						fg.setCurrentPlayer(p);
					}
				}
				fullgames.add(fg);
			}
			JSONArray out = JsonHelper.toArray(fullgames);
			return new JsonRepresentation(out);
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Unexpected error getting Games", e);
		}
	}

	@Post("json")
	public Representation createGame(JsonRepresentation r) throws IOException, JSONException, SQLException {
		String input = entityText; // this.getRequestEntity().getText();
		log.debug("createGame: " + input);

		Game game = new Game();
		List<Deck> decks = new ArrayList<Deck>();
		try {
			JSONObject json = new JSONObject(input);
			String name = json.getString("name");
			String password = "";
			if (json.has("password")) {
				password = json.getString("password");
			}
			game.setName(name);
			game.setPassword(password);
			game.setOwner(user);
			game.setActive(true);
			game.setStarted(false);
			game.setFamilyFilter(user.isFamilyFilter());

			JSONArray decksjson = json.getJSONArray("decks");
			List<Integer> deckIds = new ArrayList<Integer>();
			for (int i = 0; i < decksjson.length(); i++) {
				JSONObject jsondeck = decksjson.getJSONObject(i);
				deckIds.add(jsondeck.getInt("id"));
			}
			for (int id : deckIds) {
				Deck deck = deckService.findById(id);
				if (deck == null) {
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return getStringRepresentation("No deck with id: " + id);
				}
				decks.add(deck);
			}
			if (decks.isEmpty()) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return getStringRepresentation("A game must contain at least 1 deck");
			}

		} catch (RuntimeException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Unable to parse JSON", e);
		}

		log.debug("Validating..");
		List<ValidationError<Game>> validationResult = NotJSRValidator.validate(game);
		log.debug("...validation result: " + validationResult);
		if (!validationResult.isEmpty()) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			JSONObject out = new JSONObject();
			out.put("validationError", true);
			JSONArray errors = new JSONArray();
			for (ValidationError<Game> err : validationResult) {
				JSONObject obj = new JSONObject();
				obj.put("message", err.getMessage());
				obj.put("propertyPath", err.getPropertyPath().pathAsString());
				obj.put("invalidValue", err.getInvalidValue());
				errors.put(obj);
			}
			out.put("errors", errors);
			log.info("Returning: " + out);
			return new JsonRepresentation(out);
		}
		log.debug("Validation OK");
		try {
			gameActionService.createGame(game, user, decks);
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Internal server error", e);
		}

		setStatus(Status.SUCCESS_CREATED);
		return new StringRepresentation(game.getId() + "");
	}
}
