package dk.javacode.cah.rest.game;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

import dk.javacode.cah.database.dao.GameDao;
import dk.javacode.cah.database.dao.PlayerDao;
import dk.javacode.cah.database.dao.impl.MySqlGameDao;
import dk.javacode.cah.database.dao.impl.MySqlPlayerDao;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.rest.BasicAuthResource;
import dk.javacode.proxy.InterceptorProxy;

public class JoinGameActionResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(JoinGameActionResource.class);
	
	private GameDao gameDao;
	private PlayerDao playerDao;

	public JoinGameActionResource() {
		super();
		gameDao = InterceptorProxy.buildProxy(GameDao.class, new MySqlGameDao());
		playerDao = InterceptorProxy.buildProxy(PlayerDao.class, new MySqlPlayerDao());
	}		
	
	
	@Post("json")
	public Representation joinGame(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.debug("joinGame for user: " + user);
		String idstr = this.getAttribute("id");
		
		if (idstr == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. No name provided for get.");			
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
		
		// FIXME - add check for game password
		
		List<Player> players = playerDao.findAll(game);
		
		for (Player p : players) {
			if (p.getUser().equals(user)) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return getStringRepresentation("Bad request. User already in this game.");				
			}
		}
		
		try {
			playerDao.createPlayer(game, user);
		} catch (Exception e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Unable to join game: " + e.getMessage());
		}
		
		setStatus(Status.SUCCESS_NO_CONTENT);
		return new EmptyRepresentation();
	}
}
