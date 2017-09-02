package dk.javacode.cah.rest.game;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import dk.javacode.cah.database.dao.GameDao;
import dk.javacode.cah.database.dao.impl.MySqlGameDao;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.rest.BasicAuthResource;
import dk.javacode.cah.util.JsonHelper;
import dk.javacode.proxy.InterceptorProxy;

public class UserStartedGamesResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(UserStartedGamesResource.class);
	
	private GameDao gameDao;

	public UserStartedGamesResource() {
		super();
		gameDao = InterceptorProxy.buildProxy(GameDao.class, new MySqlGameDao());
	}		
	
	@Get("json")
	public Representation getStartedGames(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.debug("getStartedGames");
		
		List<Game> allStarted = gameDao.findAllStarted(user);
		List<Game> fullgames = new ArrayList<Game>(allStarted.size());
		for (Game g : allStarted) {
			fullgames.add(gameDao.findByName(g.getName()));
		}
		JSONArray out = JsonHelper.toArray(fullgames);
		return new JsonRepresentation(out);
	}
}
