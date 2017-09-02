package dk.javacode.cah.rest.game;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
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
import dk.javacode.cah.rest.BasicAuthResource;
import dk.javacode.proxy.InterceptorProxy;

public class PlayerGameResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(PlayerGameResource.class);

	private GameDao gameDao;
	private PlayerDao playerDao;

	public PlayerGameResource() {
		super();
		gameDao = InterceptorProxy.buildProxy(GameDao.class, new MySqlGameDao());
		playerDao = InterceptorProxy.buildProxy(PlayerDao.class, new MySqlPlayerDao());
	}

	@Get("json")
	public Representation getGameDetails(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.trace("getPlayerDetails");
		String id = this.getAttribute("id");

		if (id == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. No id provided for get.");
		}
		
		Game game = gameDao.findById(Integer.parseInt(id));
		if (game == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Game with id='" + id + "' not found.");			
		}
		Player player = playerDao.find(user, game);
		
		JSONObject out = new JSONObject();
		out.put("player", new JSONObject(player));
//		out.put("game", new JSONObject(game));
	    return new JsonRepresentation(out);
	}
}
