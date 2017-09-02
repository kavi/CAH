package dk.javacode.cah.rest.game;

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
import dk.javacode.cah.database.dao.impl.MySqlGameDao;
import dk.javacode.cah.database.dao.impl.MySqlAnswerDao;
import dk.javacode.cah.database.dao.impl.MySqlPlayerDao;
import dk.javacode.cah.database.dao.impl.MySqlVoteDao;
import dk.javacode.cah.model.ActiveBlackCard;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.GamePhase;
import dk.javacode.cah.model.Answer;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.model.Vote;
import dk.javacode.cah.rest.BasicAuthResource;
import dk.javacode.cah.util.JsonHelper;
import dk.javacode.proxy.InterceptorProxy;

public class ActiveCardGameResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(ActiveCardGameResource.class);

	private GameDao gameDao;
	private VoteDao voteDao;
	private PlayedCardDao playedCardDao;
	private PlayerDao playerDao;

	public ActiveCardGameResource() {
		super();
		gameDao = InterceptorProxy.buildProxy(GameDao.class, new MySqlGameDao());
		playedCardDao = InterceptorProxy.buildProxy(PlayedCardDao.class, new MySqlAnswerDao());
		voteDao = InterceptorProxy.buildProxy(VoteDao.class, new MySqlVoteDao());
		playerDao = InterceptorProxy.buildProxy(PlayerDao.class, new MySqlPlayerDao());
	}

	@Get("json")
	public Representation getActiveCard(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.debug("getActiveCard");
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
			return getStringRepresentation("Bad request. No game found for getActiveCard.");
		}
		if (!game.isStarted()) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Game not started.");
		}
		if (!game.isActive()) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Game is not active.");
		}
		ActiveBlackCard activeBlackCard = game.getActiveBlackCard();
		if (activeBlackCard == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. No active black card.");
		}

		if (game.getPhase() == GamePhase.VOTE) {
			List<Answer> playedCards = playedCardDao.findByActiveBlackCard(activeBlackCard);
			activeBlackCard.setAnswers(playedCards);
		}
		if (game.getPhase() == GamePhase.FINISH_ROUND) {
			List<Answer> playedCards = playedCardDao.findByActiveBlackCard(activeBlackCard);
			activeBlackCard.setAnswers(playedCards);
			for (Answer pc : activeBlackCard.getAnswers()) {
				voteDao.findByPlayedCard(pc);
			}

		}
		
		Player player = playerDao.find(user, game);

		JSONObject out = new JSONObject();
		out.put("active", activeBlackCard.isActive());
		out.put("card", new JSONObject(activeBlackCard.getCard()));
		out.put("gameId", activeBlackCard.getGame().getId());
		out.put("id", activeBlackCard.getId());
		
		JSONArray playedCards = playedCardsToJson(activeBlackCard.getAnswers(), player);
		out.put("playedCards", playedCards);
//		return new JsonRepresentation(new JSONObject(activeBlackCard));
		return new JsonRepresentation(out);
	}

	private JSONArray playedCardsToJson(List<Answer> list, Player player) throws JSONException {
		JSONArray out = new JSONArray();
		int i = 0;
		for (Answer pc : list) {
			JSONObject o = new JSONObject();
			o.put("id", pc.getId());
			o.put("score", pc.getScore());
			//o.put("playedById", pc.getPlayedBy().getId()); // TODO - only reveal if this card is played by this player or someone else
			o.put("playedByUser", pc.getPlayedBy().getId() == player.getId());
//			WhiteCard card = whiteCardDao.findById(pc.getCard().getId());
//			pc.setCard(card);
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
		return out;	}
}
