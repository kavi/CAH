package dk.javacode.cah.rest.game;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Put;

import dk.javacode.cah.database.dao.GameDao;
import dk.javacode.cah.database.dao.PlayerDao;
import dk.javacode.cah.database.dao.WhiteCardDao;
import dk.javacode.cah.database.dao.impl.MySqlGameDao;
import dk.javacode.cah.database.dao.impl.MySqlPlayerDao;
import dk.javacode.cah.database.dao.impl.MySqlWhiteCardDao;
import dk.javacode.cah.database.service.GameActionService;
import dk.javacode.cah.database.service.interfaces.IGameActionService;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.GamePhase;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.model.WhiteCard;
import dk.javacode.cah.rest.BasicAuthResource;
import dk.javacode.proxy.InterceptorProxy;

public class PlayWhiteActionResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(PlayWhiteActionResource.class);
	
	private IGameActionService gameActionService;
	private WhiteCardDao whiteCardDao;
	private GameDao gameDao;
	private PlayerDao playerDao;

	public PlayWhiteActionResource() {
		super();
		whiteCardDao = InterceptorProxy.buildProxy(WhiteCardDao.class, new MySqlWhiteCardDao());
		gameDao = InterceptorProxy.buildProxy(GameDao.class, new MySqlGameDao());
		playerDao = InterceptorProxy.buildProxy(PlayerDao.class, new MySqlPlayerDao());
		gameActionService = InterceptorProxy.buildProxy(IGameActionService.class, new GameActionService());
	}		
	
	
	@Put("json")
	public Representation playWhite(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.debug("playWhite");
		String input = entityText; //this.getRequestEntity().getText();
		String idstr = this.getAttribute("id");
		
		if (idstr == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. No id provided for put (playWhite).");			
		}
		int id = -1; 
		try {
			id = Integer.parseInt(idstr);
		} catch (RuntimeException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Invalid id: " + idstr);						
		}
		
		Game game = gameDao.findById(id);
		if (game == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. No game found for playWhite.");
		}
		if (!game.isStarted()) {
			log.info("Game already started");
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. Game not started.");			
		}
		if (!game.isActive()) {
			log.info("Game not active");
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. Game is not active.");			
		}
		if (game.getPhase() != GamePhase.PLAY_WHITES) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Game is not in phase 'PLAY_WHITES'. Current phase: " + game.getPhase());			
		}
		
		JSONArray json = null;
		List<WhiteCard> cards = null;
		try {
			json = new JSONArray(input);
			cards = new ArrayList<WhiteCard>(json.length());
			for (int i = 0;i < json.length();i++) {
					Integer cardid = (Integer) json.get(i);
					cards.add(whiteCardDao.findById(cardid));
			}
		} catch (RuntimeException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Unable to parse JSON");
		}
		
		Player player = playerDao.find(user, game);
		System.out.println(cards);
		try {
			gameActionService.playWhites(game, player, cards);
		} catch (RuntimeException e) {
			log.error("Unexpected error", e);
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Unable to process request. " + e.getMessage());
		}

		setStatus(Status.SUCCESS_NO_CONTENT);
		return new StringRepresentation("");
	}
}
