package dk.javacode.cah.rest.game;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import dk.javacode.cah.database.dao.GameDao;
import dk.javacode.cah.database.dao.PlayerDao;
import dk.javacode.cah.database.dao.impl.MySqlGameDao;
import dk.javacode.cah.database.dao.impl.MySqlPlayerDao;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.model.User;
import dk.javacode.cah.rest.BasicAuthResource;
import dk.javacode.cah.util.JsonHelper;
import dk.javacode.proxy.InterceptorProxy;

public class GameScoreboardResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(GameScoreboardResource.class);

	private GameDao gameDao;
	private PlayerDao playerDao;

	public GameScoreboardResource() {
		super();
		gameDao = InterceptorProxy.buildProxy(GameDao.class, new MySqlGameDao());
		playerDao = InterceptorProxy.buildProxy(PlayerDao.class, new MySqlPlayerDao());
	}

	@Get("json")
	public Representation getScoreBoard(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.debug("getPlayerDetails");
		String id = this.getAttribute("id");

		if (id == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. No id provided for getScoreboard.");
		}
		
		Game game = gameDao.findById(Integer.parseInt(id));
		if (game == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Game with id='" + id + "' not found.");			
		}
		
		List<Player> players = playerDao.findAllActive(game);
		for (Player p : players) {
			User pu = userDao.findById(p.getUser().getId());
			p.setUser(pu);
		}
		
		return new JsonRepresentation(JsonHelper.toArray(players));
	}
}
