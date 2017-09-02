package dk.javacode.cah.database.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.dao.WhiteCardDeckDao;
import dk.javacode.cah.model.ActiveBlackCard;
import dk.javacode.cah.model.Answer;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.model.WhiteCard;
import dk.javacode.proxy.SimpleInterceptor;
import dk.javacode.srsm.ResultSetMapper;
import dk.javacode.srsm.exceptions.MappingException;

public class MySqlWhiteCardDeckDao extends AbstractDao implements WhiteCardDeckDao {
	private ResultSetMapper mapper = new ResultSetMapper();

	public MySqlWhiteCardDeckDao() {
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<WhiteCard> findCardsInDeck(Game g) {
		try {
			PreparedStatement stmt = connection
					.prepareStatement("select wc.id, wc.text from white_deck_cards wd join white_card wc on wc.id = wd.white_card_id where game_id = ? order by wd.id");
			stmt.setInt(1, g.getId());
			ResultSet resultSet = stmt.executeQuery();
			return mapper.toPojo(WhiteCard.class, resultSet);
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (MappingException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<WhiteCard> findCardsInDiscard(Game g) {
		try {
			PreparedStatement stmt = connection
					.prepareStatement("select wc.id, wc.text from white_discard_cards wd join white_card wc on wc.id = wd.white_card_id where game_id = ?");
			stmt.setInt(1, g.getId());
			ResultSet resultSet = stmt.executeQuery();
			return mapper.toPojo(WhiteCard.class, resultSet);
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (MappingException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void moveFromDeckToPlayer(Game game, Player player, WhiteCard card) {
		try {
			PreparedStatement stmt = connection
					.prepareStatement("delete from white_deck_cards where white_card_id = ? and game_id = ?");
			stmt.setInt(1, card.getId());
			stmt.setInt(2, game.getId());
			int updates = stmt.executeUpdate();

			if (updates != 1) {
				throw new DaoException("Must delete exactly 1 card from white_deck_cards. Aborting. game: " + game
						+ ", card: " + card);
			}

			stmt = connection.prepareStatement("insert into player_card (player_id, white_card_id) values (?, ?)");
			stmt.setInt(1, player.getId());
			stmt.setInt(2, card.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public void moveFromDiscardToDeck(Game game, WhiteCard card) {
		try {
			PreparedStatement stmt = connection.prepareStatement("delete from white_discard_cards where game_id = ? and white_card_id = ?");
			stmt.setInt(1, game.getId());
			stmt.setInt(2, card.getId());
			int updates = stmt.executeUpdate();

			if (updates != 1) {
				throw new DaoException("Must delete exactly 1 card from white_discard_cards. Aborting. game: " + game + ", card: " + card);
			}

			stmt = connection.prepareStatement("insert into white_deck_cards (game_id, white_card_id) values (?, ?)");
			stmt.setInt(1, game.getId());
			stmt.setInt(2, card.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	// TODO - move to service!
	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void playCard(Game game, Player player, List<WhiteCard> cards, ActiveBlackCard activeBlackCard) {
		if (cards.size() < 1) {
			throw new DaoException("Must play at least 1 card");
		}
		Answer answer = new Answer();
		answer.setCards(cards);
		answer.setPlayedBy(player);

		answer.setPlayedFor(activeBlackCard);

		activeBlackCard.addAnswer(answer);

		try {
			// Create the Answer
			String sql = "insert into answer (question_id, player_id) values (?, ?)";
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			stmt.setInt(1, answer.getPlayedFor().getId());
			stmt.setInt(2, answer.getPlayedBy().getId());
			stmt.execute();
			ResultSet keys = stmt.getGeneratedKeys();
			keys.next();
			int id = keys.getInt(1);
			answer.setId(id);			
			
			// Play every card in list
			int position = 0;
			for (WhiteCard card : answer.getCards()) {
				position++;
				String deleteSql = "delete from player_card where white_card_id = ? and player_id = ?";
				stmt = connection.prepareStatement(deleteSql);
				stmt.setInt(1, card.getId());
				stmt.setInt(2, player.getId());
				int updates = stmt.executeUpdate();

				if (updates != 1) {
					throw new DaoException("Must delete exactly 1 card from player_card. Aborting. player: " + player + ", card: " + cards);
				}
				String insertSql = "insert into answer_cards (white_card_id, answer_id, position) values (?, ?, ?)";
				stmt = connection.prepareStatement(insertSql);
				stmt.setInt(1, card.getId());
				stmt.setInt(2, answer.getId());
				stmt.setInt(3, position);
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void moveFromPlayerToDiscard(Game game, Player player, WhiteCard card) {
		try {
			PreparedStatement stmt = connection.prepareStatement("delete from player_card where white_card_id = ? and player_id = ?");
			stmt.setInt(1, card.getId());
			stmt.setInt(2, player.getId());
			int updates = stmt.executeUpdate();

			if (updates != 1) {
				throw new DaoException("Must delete exactly 1 card from player_card. Aborting. player: " + player + ", card: " + card);
			}

			stmt = connection.prepareStatement("insert into white_discard_cards (white_card_id, game_id) values (?, ?)");
			stmt.setInt(1, card.getId());
			stmt.setInt(2, game.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

}
