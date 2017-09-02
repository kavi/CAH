package dk.javacode.cah.rest.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import dk.javacode.cah.database.dao.GameDao;
import dk.javacode.cah.database.dao.PlayedCardDao;
import dk.javacode.cah.database.dao.PlayerDao;
import dk.javacode.cah.database.dao.VoteDao;
import dk.javacode.cah.database.dao.impl.MySqlAnswerDao;
import dk.javacode.cah.database.dao.impl.MySqlGameDao;
import dk.javacode.cah.database.dao.impl.MySqlPlayerDao;
import dk.javacode.cah.database.dao.impl.MySqlVoteDao;
import dk.javacode.cah.model.ActiveBlackCard;
import dk.javacode.cah.model.Answer;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.GamePhase;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.model.Vote;
import dk.javacode.cah.rest.BasicAuthResource;
import dk.javacode.cah.util.JsonHelper;
import dk.javacode.proxy.InterceptorProxy;

public class GameSingleResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(GameSingleResource.class);

	private GameDao gameDao;
	private PlayerDao playerDao;
	private VoteDao voteDao;
	private PlayedCardDao answerDao;

	public GameSingleResource() {
		super();
		gameDao = InterceptorProxy.buildProxy(GameDao.class, new MySqlGameDao());
		playerDao = InterceptorProxy.buildProxy(PlayerDao.class, new MySqlPlayerDao());
		answerDao = InterceptorProxy.buildProxy(PlayedCardDao.class, new MySqlAnswerDao());
		voteDao = InterceptorProxy.buildProxy(VoteDao.class, new MySqlVoteDao());
	}

	@Get("json")
	public Representation getGame(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.trace("getGame");
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
			return getStringRepresentation("Bad request. No game with id: " + id);
		}
		boolean isUserInGame = false;
		for (Player p : game.listActivePlayers()) {
			if (p.getUser().getId() == user.getId()) {
				Player player = playerDao.find(user, game);
				game.setCurrentPlayer(player);
				isUserInGame = true;
			}
		}
		if (!isUserInGame) {
			log.info("Unauthorized game access");
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return getStringRepresentation("Bad request. Game id: " + id + " not available for logged in user");
		}

		JSONObject jsonObject = new JSONObject(game);
		if (game.isStarted() && game.isActive()) {
			ActiveBlackCard activeBlackCard = game.getActiveBlackCard();
			if (activeBlackCard == null) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return getStringRepresentation("Bad request. No active black card for active game.");
			}

			if (game.getPhase() == GamePhase.VOTE) {
				List<Answer> answers = answerDao.findByActiveBlackCard(activeBlackCard);
				activeBlackCard.setAnswers(answers);
			}
			if (game.getPhase() == GamePhase.FINISH_ROUND) {
				List<Answer> playedCards = answerDao.findByActiveBlackCard(activeBlackCard);
				activeBlackCard.setAnswers(playedCards);
				for (Answer pc : activeBlackCard.getAnswers()) {
					voteDao.findByPlayedCard(pc);
				}
			}
			JSONObject activeBlackCardJson = new JSONObject();
			activeBlackCardJson.put("active", activeBlackCard.isActive());
			activeBlackCardJson.put("card", new JSONObject(activeBlackCard.getCard()));
			activeBlackCardJson.put("gameId", activeBlackCard.getGame().getId());
			activeBlackCardJson.put("id", activeBlackCard.getId());

			JSONArray playedCards = playedCardsToJson(activeBlackCard.getAnswers(), game.getCurrentPlayer());
			activeBlackCardJson.put("playedCards", playedCards);
			jsonObject.remove("activeBlackCard");
			jsonObject.put("activeBlackCard", activeBlackCardJson);
		}
		return new JsonRepresentation(jsonObject);
	}

	private JSONArray playedCardsToJson(List<Answer> list, Player player) throws JSONException {
		JSONArray out = new JSONArray();
		int i = 0;
		for (Answer pc : list) {
			JSONObject o = new JSONObject();
			o.put("id", pc.getId());
			o.put("score", pc.getScore());
			// o.put("playedById", pc.getPlayedBy().getId()); // TODO - only
			// reveal if this card is played by this player or someone else
			o.put("playedByUser", pc.getPlayedBy().getId() == player.getId());
			// WhiteCard card = whiteCardDao.findById(pc.getCard().getId());
			// pc.setCard(card);
			o.put("cards", JsonHelper.toArray(pc.getCards()));
			o.put("votes", votesToJson(pc.getVotes()));

			out.put(i, o);
			i++;
		}
		return out;
	}

	private JSONArray votesToJson(List<Vote> votes) throws JSONException {
		JSONArray out = new JSONArray();
		int i = 0;
		for (Vote v : votes) {
			JSONObject o = new JSONObject();
			o.put("id", v.getId());
			o.put("score", v.getScore());
			o.put("votedBy", v.getVotedBy().getId());
			out.put(i, o);
			i++;
		}
		return out;
	}
}
