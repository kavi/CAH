package dk.javacode.cah.database.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.dao.DeckDao;
import dk.javacode.cah.model.Deck;
import dk.javacode.cah.model.DeckLanguage;
import dk.javacode.cah.model.User;
import dk.javacode.srsm.ResultSetMapper;
import dk.javacode.srsm.SqlSelectColumn;
import dk.javacode.srsm.exceptions.MappingException;
import dk.javacode.srsm.helpers.SqlInsertHelper;
import dk.javacode.srsm.helpers.SqlQueryHelper;
import dk.javacode.srsm.helpers.SqlQueryHelper.SqlSelect;

public class MySqlDeckDao extends AbstractDao implements DeckDao {
	private SqlQueryHelper sqlBuilder = new SqlQueryHelper();
	private SqlInsertHelper sqlInsert = new SqlInsertHelper();
	private ResultSetMapper mapper = new ResultSetMapper();

	public MySqlDeckDao() {
	}

	@Override
	public List<Deck> findAll() {
		try {
			return sqlBuilder.getAll(connection, Deck.class);
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public Deck findByName(DeckLanguage language, String name) {
		try {
			SqlSelectColumn nameconstraint = new SqlSelectColumn("name",	name);
			SqlSelectColumn languageconstraint = new SqlSelectColumn("language_id", language.getId());
			Deck deck = sqlBuilder.getOne(connection, Deck.class, languageconstraint, nameconstraint);
			if (deck != null) {
				User owner = sqlBuilder.getOne(connection, User.class, new SqlSelectColumn("id", deck.getOwner().getId()));
				deck.setOwner(owner);
				deck.setLanguage(language);
			}
			return deck;
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}

	}

	@Override
	public List<Deck> findByLanguage(DeckLanguage language) {
		try {
			SqlSelect selectSql = sqlBuilder.buildSelectSql(Deck.class);
			selectSql.addJoin(User.class, "id", "owner_id");
			selectSql.addJoin(DeckLanguage.class, "id", "language_id");
			String sql = selectSql.getSelect();
			sql += " WHERE language_id = ?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, language.getId());
			ResultSet resultSet = stmt.executeQuery();
			return mapper.toPojo(Deck.class, resultSet);
			// return sqlBuilder.getAll(connection, Deck.class, new
			// SqlSelectColumn("language_id", language.getId()));
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public Deck findById(int id) {
		try {
			Deck deck = sqlBuilder.getOne(connection, Deck.class, new SqlSelectColumn("id", id));
			DeckLanguage language = sqlBuilder.getOne(connection, DeckLanguage.class, new SqlSelectColumn("id", deck.getLanguage().getId()));
			if (deck != null) {
				deck.setLanguage(language);
				User owner = sqlBuilder.getOne(connection, User.class, new SqlSelectColumn("id", deck.getOwner().getId()));
				deck.setOwner(owner);
			}
			return deck;
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public void createDeck(Deck deck) {
		try {
			sqlInsert.insert(connection, deck, "deck");
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

}
