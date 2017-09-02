package dk.javacode.cah.database.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.BlackCardDao;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.model.BlackCard;
import dk.javacode.cah.model.User;
import dk.javacode.proxy.SimpleInterceptor;
import dk.javacode.srsm.ResultSetMapper;
import dk.javacode.srsm.SqlSelectColumn;
import dk.javacode.srsm.helpers.SqlInsertHelper;
import dk.javacode.srsm.helpers.SqlQueryHelper;
import dk.javacode.srsm.helpers.SqlQueryHelper.JoinType;
import dk.javacode.srsm.helpers.SqlQueryHelper.SqlSelect;

public class MySqlBlackCardDao extends AbstractDao implements BlackCardDao {
	private SqlQueryHelper sqlBuilder = new SqlQueryHelper();
	private SqlInsertHelper sqlInsert = new SqlInsertHelper();
	private ResultSetMapper mapper = new ResultSetMapper();

	public MySqlBlackCardDao() {
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void hardDeleteCard(int id) {
		try {
			PreparedStatement stmt = connection.prepareStatement("delete from black_card where id = ?");
			stmt.setInt(1, id);
			stmt.executeUpdate();
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void softDeleteCard(int id) {
		try {
			PreparedStatement stmt = connection.prepareStatement("update black_card set disabled = ? where id = ?");
			stmt.setBoolean(1, true);
			stmt.setInt(2, id);
			stmt.executeUpdate();
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<BlackCard> findAll(boolean familyFilter, int deckId) {
		System.out.println("findAll...");
		try {
			if (!familyFilter) {
				return sqlBuilder.getAll(connection, BlackCard.class, new SqlSelectColumn("deck_id", deckId));
			} else {				
				return sqlBuilder.getAll(connection, BlackCard.class, new SqlSelectColumn("safe_for_work", true), new SqlSelectColumn("deck_id", deckId));
			}
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<BlackCard> findAllAvailable(boolean familyFilter, int deckId) {
		try {
			List<BlackCard> all;
			if (!familyFilter) {
				 all = sqlBuilder.getAll(connection, BlackCard.class, new SqlSelectColumn("disabled", false), new SqlSelectColumn("deck_id", deckId));
			} else {
				all = sqlBuilder.getAll(connection, BlackCard.class, new SqlSelectColumn("disabled", false), new SqlSelectColumn("safe_for_work", true), new SqlSelectColumn("deck_id", deckId));
			}
//			System.out.println("All blackCards: " + all);
			return all;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public BlackCard findById(int id) {
		try {
			SqlSelect selectSql = sqlBuilder.buildSelectSql(BlackCard.class);
			selectSql.addJoin(User.class, "id", "changed_by", JoinType.LEFT_JOIN);
			String sql = selectSql.getSelect();
			sql += " WHERE black_card.id = ?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet resultSet = stmt.executeQuery();
			return mapper.getSinglePojo(BlackCard.class, resultSet);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void createCard(BlackCard card) {
		try {
			sqlInsert.insert(connection, card, "black_card");
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void updateCard(BlackCard card) {		
		try {
			PreparedStatement stmt = connection.prepareStatement("update black_card set text = ?, disabled = ?, cards_to_pick = ?, cards_to_draw = ?, safe_for_work = ?, deck_id = ?, changed_by = ? where id = ?");
			stmt.setString(1, card.getText());
			stmt.setBoolean(2, card.isDisabled());
			stmt.setInt(3, card.getCardsToPick());
			stmt.setInt(4, card.getCardsToDraw());
			stmt.setBoolean(5, card.isSafeForWork());
			stmt.setInt(6, card.getDeckId());
			if (card.getChangedBy() != null) {
				stmt.setInt(7, card.getChangedBy().getId());
			} else {
				stmt.setNull(7, Types.INTEGER);
			}
			stmt.setInt(8, card.getId());
			stmt.executeUpdate();
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

}
