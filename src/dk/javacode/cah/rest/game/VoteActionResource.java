package dk.javacode.cah.rest.game;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Put;

import dk.javacode.cah.database.dao.GameDao;
import dk.javacode.cah.database.dao.PlayedCardDao;
import dk.javacode.cah.database.dao.PlayerDao;
import dk.javacode.cah.database.dao.impl.MySqlGameDao;
import dk.javacode.cah.database.dao.impl.MySqlAnswerDao;
import dk.javacode.cah.database.dao.impl.MySqlPlayerDao;
import dk.javacode.cah.database.service.GameActionService;
import dk.javacode.cah.database.service.interfaces.IGameActionService;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.GamePhase;
import dk.javacode.cah.model.Answer;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.model.Vote;
import dk.javacode.cah.rest.BasicAuthResource;
import dk.javacode.proxy.InterceptorProxy;

public class VoteActionResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(VoteActionResource.class);

	private IGameActionService gameActionService;
	private PlayedCardDao playedCardDao;
	private GameDao gameDao;
	private PlayerDao playerDao;

	public VoteActionResource() {
		super();
		playedCardDao = InterceptorProxy.buildProxy(PlayedCardDao.class, new MySqlAnswerDao());
		gameDao = InterceptorProxy.buildProxy(GameDao.class, new MySqlGameDao());
		playerDao = InterceptorProxy.buildProxy(PlayerDao.class, new MySqlPlayerDao());
		gameActionService = InterceptorProxy.buildProxy(IGameActionService.class, new GameActionService());
	}

	@Put("json")
	public Representation vote(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.debug("vote");
		String input = entityText; //this.getRequestEntity().getText();
		String idstr = this.getAttribute("id");

		if (idstr == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. No id provided for put (vote).");
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
			return getStringRepresentation("Bad request. No game found for vote.");
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
		if (game.getPhase() != GamePhase.VOTE) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Game is not in phase 'VOTE'. Current phase: "
					+ game.getPhase());
		}

		Player player = playerDao.find(user, game);

		JSONObject obj = null;
		Vote vote = new Vote();
		int pid;
		int score;
		try {
			obj = new JSONObject(input);
			pid = obj.getInt("playedCardId");
			score = obj.getInt("score");
		} catch (RuntimeException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Unable to parse JSON. " + e.getMessage());
		}
		
		Answer voteFor = playedCardDao.findById(pid);

		if (voteFor == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. VoteFor card with id: " + pid + " not found");
		}
		vote.setPlayedCard(voteFor);
		vote.setScore(score);
		vote.setVotedBy(player);
		
		log.debug("Received vote: " + vote);

		try {
			gameActionService.vote(game, player, vote);
		} catch (RuntimeException e) {
			log.error("Unexpected error", e);
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Unable to process request. " + e.getMessage());
		}

		setStatus(Status.SUCCESS_NO_CONTENT);
		return new StringRepresentation("");
	}
}
