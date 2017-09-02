package dk.javacode.cah.database.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.dao.VoteDao;
import dk.javacode.cah.database.dao.impl.MySqlVoteDao;
import dk.javacode.cah.database.service.interfaces.AbstractService;
import dk.javacode.cah.database.service.interfaces.IGameActionService;
import dk.javacode.cah.model.ActiveBlackCard;
import dk.javacode.cah.model.Answer;
import dk.javacode.cah.model.BlackCard;
import dk.javacode.cah.model.Deck;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.GamePhase;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.model.User;
import dk.javacode.cah.model.Vote;
import dk.javacode.cah.model.WhiteCard;
import dk.javacode.proxy.SimpleInterceptor;
import dk.javacode.srsm.exceptions.MappingException;
import dk.javacode.srsm.helpers.SqlInsertHelper;

public class GameActionService extends AbstractService implements IGameActionService {

	private static Logger log = Logger.getLogger(GameActionService.class);

//	private static final int HAND_SIZE = 7;
	private static final int TOTAL_SCORE = 1;

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void drawWhites(Game game) {
		ActiveBlackCard activeBlackCard = game.getActiveBlackCard();
		int handSizeThisTurn = activeBlackCard.getCard().getCardsToDraw() + game.getHandSize();

		List<Player> players = playerDao.findAllActive(game);
		game.setPlayers(players);

		List<WhiteCard> cardsInDeck = whiteCardDeckDao.findCardsInDeck(game);
		System.out.println(cardsInDeck);
		for (Player p : players) {
			playerDao.loadHand(p);
			while (p.getCards().size() < handSizeThisTurn) {
				if (cardsInDeck.isEmpty()) {
					shuffleWhiteDeck(game);
					cardsInDeck = whiteCardDeckDao.findCardsInDeck(game);
				}
				WhiteCard card = cardsInDeck.remove(0);
				whiteCardDeckDao.moveFromDeckToPlayer(game, p, card);
				p.addCard(card);
			}
			playerDao.setActionPerformed(p, false);
		}

		game.setPhase(GamePhase.PLAY_WHITES);
		gameDao.update(game);
	}

	@SimpleInterceptor(ConnectionInterceptor.class)
	public void shuffleWhiteDeck(Game game) {
		List<WhiteCard> discardPile = whiteCardDeckDao.findCardsInDiscard(game);
		Collections.shuffle(discardPile);
		for (WhiteCard wc : discardPile) {
			whiteCardDeckDao.moveFromDiscardToDeck(game, wc);
		}
	}

	@SimpleInterceptor(ConnectionInterceptor.class)
	public void shuffleBlackDeck(Game game) {
		List<BlackCard> discardPile = blackCardDeckDao.findCardsInDiscard(game);
		Collections.shuffle(discardPile);
		for (BlackCard wc : discardPile) {
			blackCardDeckDao.moveFromDiscardToDeck(game, wc);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void drawBlack(Game game) {
		ActiveBlackCard oldActive = blackCardDeckDao.findActiveBlackCard(game);
		if (oldActive != null) {
			blackCardDeckDao.moveFromActiveToDiscard(game, oldActive);
		}

		List<BlackCard> blackDeck = blackCardDeckDao.findCardsInDeck(game);
		if (blackDeck.isEmpty()) {
			shuffleBlackDeck(game);
		}
		BlackCard card = blackDeck.remove(0);

		ActiveBlackCard active = blackCardDeckDao.moveToActive(game, card);
		game.setActiveBlackCard(active);

		game.setPhase(GamePhase.DRAW_HAND);
		gameDao.update(game);
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public boolean playWhites(Game game, Player player, List<WhiteCard> cards) {
		if (game.getPhase() != GamePhase.PLAY_WHITES) {
			throw new RuntimeException("Unable to play white cards. Not allowed in phase: " + game.getPhase());
		}
		if (player.isActionPerformed()) {
			throw new RuntimeException("Player already played whites");
		}
		ActiveBlackCard activeBlackCard = blackCardDeckDao.findActiveBlackCard(game);
		game.setActiveBlackCard(activeBlackCard);

		if (cards.size() != activeBlackCard.getCard().getCardsToPick()) {
			throw new RuntimeException("Incorrect number of cards (" + cards.size() + "/"
					+ activeBlackCard.getCard().getCardsToPick());
		}

		for (WhiteCard card : cards) {
			if (!player.getCards().contains(card)) {
				throw new RuntimeException("Player does not have card(id): " + card.getId() + " in hand");
			}
		}
		whiteCardDeckDao.playCard(game, player, cards, activeBlackCard);

		playerDao.setActionPerformed(player, true);

		List<Player> players = playerDao.findAllActive(game);
		for (Player p : players) {
			if (!p.isActionPerformed()) {
				return true;
			}
		}

		// Next phase
		for (Player p : players) {
			playerDao.setActionPerformed(p, false);
		}
		game.setPhase(GamePhase.VOTE);
		gameDao.update(game);
		return true;
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void vote(Game game, Player player, Vote vote) {
		ActiveBlackCard activeBlackCard = blackCardDeckDao.findActiveBlackCard(game);
		game.setActiveBlackCard(activeBlackCard);
		


		if (vote.getPlayedCard().getPlayedBy().getId() == vote.getVotedBy().getId()) {
			throw new RuntimeException("Can't vote for your own card");
		}
		if (vote.getScore() != TOTAL_SCORE) {
			throw new RuntimeException("Must place a vote worth " + TOTAL_SCORE);
		}

		voteDao.createVote(vote);
		playerDao.setActionPerformed(player, true);

		// Post processing - move to next phase if all players have performed actions
		List<Player> players = playerDao.findAllActive(game);
		for (Player p : players) {
			if (!p.isActionPerformed()) {
				return;
			}
		}

		for (Player p : players) {
			playerDao.setActionPerformed(p, false);
		}
		game.setPhase(GamePhase.FINISH_ROUND);
		gameDao.update(game);
		return;
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void nextRound(Game game, Player player) {
		if (game.getPhase() != GamePhase.FINISH_ROUND) {
			throw new RuntimeException("Invalid action for round");
		}
		
		player.setActionPerformed(true);
		playerDao.setActionPerformed(player, true);

		List<Player> players = playerDao.findAllActive(game);
		for (Player p : players) {
			if (!p.isActionPerformed()) {
				return;
			}
		}

		// All players finished - start new round
		int roundsPlayed = game.getRoundsPlayed() + 1;
		game.setRoundsPlayed(roundsPlayed);
		if (game.getRoundsPlayed() == game.getNumberOfRounds()) {
			// TODO - end game
			game.setActive(false);
			gameDao.update(game);
			return;
		}
		ActiveBlackCard activeBlackCard = blackCardDeckDao.findActiveBlackCard(game);
		List<Answer> playedCards = playedCardDao.findByActiveBlackCard(activeBlackCard);
		VoteDao votedao = new MySqlVoteDao();
		votedao.setConnection(connection);

		for (Answer pc : playedCards) {
			List<Vote> votes = votedao.findByPlayedCard(pc);
			pc.setVotes(votes);
		}
		List<Answer> winnerCards = activeBlackCard.calculateWinnerAnswers();
		for (Answer pc : winnerCards) {
			Player winner = playerDao.findById(pc.getPlayedBy().getId());
			winner.setPoints(winner.getPoints() + 1);
			playerDao.update(winner);
		}
		

		for (Player p : players) {
			playerDao.setActionPerformed(p, false);
		}
		game.setPhase(GamePhase.DRAW_QUESTION);
		gameDao.update(game);

		this.drawBlack(game);
		this.drawWhites(game);
		return;
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void startGame(Game game) {
		if (game.listActivePlayers().size() < 3) {
			throw new RuntimeException("Game must have at least 3 players to start.");
		}
		List<WhiteCard> deck = whiteCardDeckDao.findCardsInDeck(game);
		if (deck.size() < ((4 + game.getHandSize()) * game.listActivePlayers().size())) {
			throw new RuntimeException("Game does not have enough white cards in deck to play");
		}
		List<BlackCard> bdeck = blackCardDeckDao.findCardsInDeck(game);
		if (bdeck.size() < 2) {
			throw new RuntimeException("Game does not have enough black cards in deck to play");
		}
		game.setStarted(true);
		game.setPhase(GamePhase.DRAW_QUESTION);
		this.updateGame(game);

		this.drawBlack(game);
		this.drawWhites(game);
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void updateGame(Game game) {
		log.debug("Connection:" + connection);
		gameDao.update(game);
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void createGame(Game game, User user, List<Deck> decks) {
		if (user.isFamilyFilter() && !game.isFamilyFilter()) {
			throw new DaoException("Family filter user cannot create game without family filter");
		}
		SqlInsertHelper sqlInsert = new SqlInsertHelper();
		try {
			sqlInsert.insert(connection, game, "game");

			List<WhiteCard> whiteDeck = new ArrayList<WhiteCard>();
			for (Deck d : decks) {
				whiteDeck.addAll(whiteCardDao.findAllAvailable(game.isFamilyFilter(), d.getId()));
			}
			Collections.shuffle(whiteDeck);
			PreparedStatement stmt = connection
					.prepareStatement("insert into white_deck_cards (game_id, white_card_id) values (?, ?)");
			int i = 0;
			for (WhiteCard wc : whiteDeck) {
				i++;
				stmt.setInt(1, game.getId());
				stmt.setInt(2, wc.getId());
				stmt.addBatch();
				if (i >= 1000) {
					i = 0;
					stmt.executeBatch();
				}
			}
			if (i > 0) {
				stmt.executeBatch();
			}

			List<BlackCard> blackDeck = new ArrayList<BlackCard>();
			for (Deck d : decks) {
				blackDeck.addAll(blackCardDao.findAllAvailable(game.isFamilyFilter(), d.getId())); 
			}
			Collections.shuffle(blackDeck);
			stmt = connection.prepareStatement("insert into black_deck_cards (game_id, black_card_id) values (?, ?)");
			i = 0;
			for (BlackCard bc : blackDeck) {
				i++;
				stmt.setInt(1, game.getId());
				stmt.setInt(2, bc.getId());
				stmt.addBatch();
				if (i >= 1000) {
					i = 0;
					stmt.executeBatch();
				}
			}
			if (i > 0) {
				stmt.executeBatch();
			}
			addPlayer(game, user);
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (MappingException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void addPlayer(Game game, User user) {
		List<WhiteCard> deck = whiteCardDeckDao.findCardsInDeck(game);
		if (deck.size() < ((4 + game.getHandSize()) * (1 + game.listActivePlayers().size()) ) ) {
			throw new RuntimeException("Adding this player would make the game unplayable (Not enough cards)");
		}
		playerDao.createPlayer(game, user);
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public Game findGame(int id) {
		return gameDao.findById(id);
	}
}