package dk.javacode.cah.rest.game;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Put;

import dk.javacode.cah.database.dao.GameDao;
import dk.javacode.cah.database.dao.PlayerDao;
import dk.javacode.cah.database.dao.impl.MySqlGameDao;
import dk.javacode.cah.database.dao.impl.MySqlPlayerDao;
import dk.javacode.cah.database.service.GameActionService;
import dk.javacode.cah.database.service.interfaces.IGameActionService;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.rest.BasicAuthResource;
import dk.javacode.proxy.InterceptorProxy;

public class StartGameActionResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(StartGameActionResource.class);
	
	private IGameActionService gameActionService;
	private GameDao gameDao;
	private PlayerDao playerDao;

	public StartGameActionResource() {
		super();
		gameDao = InterceptorProxy.buildProxy(GameDao.class, new MySqlGameDao());
		playerDao = InterceptorProxy.buildProxy(PlayerDao.class, new MySqlPlayerDao());
		gameActionService = InterceptorProxy.buildProxy(IGameActionService.class, new GameActionService());
	}		
	
	
	@Put("json")
	public Representation startGame(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.debug("startGame");
		String idstr = this.getAttribute("id");
		
		if (idstr == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. No id provided for get.");			
		}
		int id;
		try {
			id = Integer.parseInt(idstr);
		} catch (RuntimeException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Invalid id: " + idstr);						
		}
		Game game = gameDao.findById(id);
		if (game == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. No game found for addPlayer.");
		}
		if (game.isStarted()) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Game already started.");			
		}
		if (!game.isActive()) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Game is not active.");			
		}
		
		List<Player> players = playerDao.findAllActive(game);
		
		if (players.size() < 3) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Must have at least 3 active players to start the game.");			
		}
		try {			
			gameActionService.startGame(game);
			setStatus(Status.SUCCESS_NO_CONTENT);
			return new StringRepresentation("");
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Internal Server error.", e);						
		}
	}
}
