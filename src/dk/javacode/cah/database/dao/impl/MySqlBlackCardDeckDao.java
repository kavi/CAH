package dk.javacode.cah.database.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.BlackCardDeckDao;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.model.ActiveBlackCard;
import dk.javacode.cah.model.Answer;
import dk.javacode.cah.model.BlackCard;
import dk.javacode.cah.model.Game;
import dk.javacode.proxy.SimpleInterceptor;
import dk.javacode.srsm.ResultSetMapper;
import dk.javacode.srsm.SqlSelectColumn;
import dk.javacode.srsm.exceptions.MappingException;
import dk.javacode.srsm.helpers.SqlInsertHelper;
import dk.javacode.srsm.helpers.SqlQueryHelper;

public class MySqlBlackCardDeckDao extends AbstractDao implements BlackCardDeckDao {
	private ResultSetMapper mapper = new ResultSetMapper();
	private SqlQueryHelper builder = new SqlQueryHelper();
	private SqlInsertHelper sqlInserter = new SqlInsertHelper();

	public MySqlBlackCardDeckDao() {
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<BlackCard> findCardsInDeck(Game g) {
		try {
			PreparedStatement stmt = connection
					.prepareStatement("select bc.id, bc.text, bc.cards_to_draw, bc.cards_to_pick from black_deck_cards bd join black_card bc on bc.id = bd.black_card_id where game_id = ? order by bd.id");
			stmt.setInt(1, g.getId());
			ResultSet resultSet = stmt.executeQuery();
			return mapper.toPojo(BlackCard.class, resultSet);
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (MappingException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public ActiveBlackCard findActiveBlackCard(Game g) {
		try {
			ActiveBlackCard result = builder.getOne(connection, ActiveBlackCard.class, new SqlSelectColumn("game_id", g.getId()), new SqlSelectColumn("active", true));
			if (result == null) {
				return null;
			}
			BlackCard card = builder.getOne(connection, BlackCard.class, new SqlSelectColumn("id", result.getCard().getId()));
			result.setCard(card);
			return result;
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (MappingException e) {
			throw new DaoException(e);
		}
	}
	
	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<Answer> findPlayedCards(ActiveBlackCard card) {
		try {
			return builder.getAll(connection, Answer.class, new SqlSelectColumn("question_id", card.getId()));
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (MappingException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public List<BlackCard> findCardsInDiscard(Game g) {
		try {
			PreparedStatement stmt = connection
					.prepareStatement("select bc.id, bc.text from black_discard_cards bd join black_card bc on bc.id = bd.black_card_id where game_id = ?");
			stmt.setInt(1, g.getId());
			ResultSet resultSet = stmt.executeQuery();
			return mapper.toPojo(BlackCard.class, resultSet);
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (MappingException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public ActiveBlackCard moveToActive(Game game, BlackCard card) {
		try {
			PreparedStatement stmt = connection
					.prepareStatement("delete from black_deck_cards where black_card_id = ? and game_id = ?");
			stmt.setInt(1, card.getId());
			stmt.setInt(2, game.getId());
			stmt.executeUpdate();
			
			ActiveBlackCard activeBlackCard = new ActiveBlackCard();
			activeBlackCard.setActive(true);
			activeBlackCard.setCard(card);
			activeBlackCard.setGame(game);
			game.setActiveBlackCard(activeBlackCard);

			sqlInserter.insert(connection, activeBlackCard, "question");
			return activeBlackCard;
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (MappingException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public void moveFromActiveToDiscard(Game game, ActiveBlackCard card) {
		try {
			PreparedStatement stmt = connection
					.prepareStatement("update question set active = false where id = ?");
			stmt.setInt(1, card.getId());
			int updates = stmt.executeUpdate();
		
			if (updates != 1) {
				throw new DaoException("Exactly one active black card must be updated. Was: " + updates);
			}

			stmt = connection
					.prepareStatement("insert into black_discard_cards (game_id, black_card_id) values (?, ?)");
			stmt.setInt(1, game.getId());
			stmt.setInt(2, card.getCard().getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}
	
	@Override
	public void moveFromDiscardToDeck(Game game, BlackCard card) {
		try {
			PreparedStatement stmt = connection
					.prepareStatement("delete from black_discard_cards where game_id = ? and black_card_id = ?");
			stmt.setInt(1, game.getId());
			stmt.setInt(2, card.getId());
			int updates = stmt.executeUpdate();

			if (updates != 1) {
				throw new DaoException("Must delete exactly 1 card from black_discard_cards. Aborting. game: " + game
						+ ", card: " + card);
			}

			stmt = connection.prepareStatement("insert into black_deck_cards (game_id, black_card_id) values (?, ?)");
			stmt.setInt(1, game.getId());
			stmt.setInt(2, card.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}
}
