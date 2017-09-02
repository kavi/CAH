package dk.javacode.cah.database.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.dao.WhiteCardDao;
import dk.javacode.cah.model.User;
import dk.javacode.cah.model.WhiteCard;
import dk.javacode.proxy.SimpleInterceptor;
import dk.javacode.srsm.ResultSetMapper;
import dk.javacode.srsm.SqlSelectColumn;
import dk.javacode.srsm.helpers.SqlInsertHelper;
import dk.javacode.srsm.helpers.SqlQueryHelper;
import dk.javacode.srsm.helpers.SqlQueryHelper.JoinType;
import dk.javacode.srsm.helpers.SqlQueryHelper.SqlSelect;

public class MySqlWhiteCardDao extends AbstractDao implements WhiteCardDao {
	private SqlQueryHelper sqlBuilder = new SqlQueryHelper();
	private SqlInsertHelper sqlInsert = new SqlInsertHelper();
	private ResultSetMapper mapper = new ResultSetMapper();

	public MySqlWhiteCardDao() {
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void hardDeleteCard(int id) {
		try {
			PreparedStatement stmt = connection.prepareStatement("delete from white_card where id = ?");
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
			PreparedStatement stmt = connection.prepareStatement("update white_card set disabled = ? where id = ?");
			stmt.setBoolean(1, true);
			stmt.setInt(2, id);
			stmt.executeUpdate();
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<WhiteCard> findAll(boolean familyFilter) {
		try {
			if (!familyFilter) { // Family filter is OFF - return everything
				return sqlBuilder.getAll(connection, WhiteCard.class);
			} else {
				// Family filter is ON - only return cards where safeForWork = true
				return sqlBuilder.getAll(connection, WhiteCard.class, new SqlSelectColumn("safe_for_work", true));
			}
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<WhiteCard> findAllAvailable(boolean familyFilter, int deckId) {
		try {
			if (!familyFilter) { // Family filter is OFF - return all available
				return sqlBuilder.getAll(connection, WhiteCard.class, new SqlSelectColumn("disabled", false), new SqlSelectColumn("deck_id", deckId));
			} else {
				// Family filter is ON - only return cards where safeForWork = true
				return sqlBuilder.getAll(connection, WhiteCard.class, new SqlSelectColumn("disabled", false), new SqlSelectColumn("safe_for_work", true), new SqlSelectColumn("deck_id", deckId));
			}				
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public WhiteCard findById(int id) {
		try {
			SqlSelect selectSql = sqlBuilder.buildSelectSql(WhiteCard.class);

			selectSql.addJoin(User.class, "id", "changed_by", JoinType.LEFT_JOIN);
			String sql = selectSql.getSelect();
			sql += " WHERE white_card.id = ?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet resultSet = stmt.executeQuery();
			return mapper.getSinglePojo(WhiteCard.class, resultSet);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void createCard(WhiteCard card) {
		try {
			sqlInsert.insert(connection, card, "white_card");
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void updateCard(WhiteCard card) {
		try {
			PreparedStatement stmt = connection.prepareStatement("update white_card set text = ?, disabled = ?, safe_for_work = ?, deck_id = ?, changed_by = ? where id = ?");
			stmt.setString(1, card.getText());
			stmt.setBoolean(2, card.isDisabled());
			stmt.setBoolean(3, card.isSafeForWork());
			stmt.setInt(4, card.getDeckId());
			if (card.getChangedBy() != null) {
				stmt.setInt(5, card.getChangedBy().getId());
			} else {
				stmt.setNull(5, Types.INTEGER);
			}
			stmt.setInt(6, card.getId());
			stmt.executeUpdate();
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

}
