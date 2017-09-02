package dk.javacode.cah.database.dao.impl;

import java.sql.SQLException;
import java.util.List;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.dao.DeckLanguageDao;
import dk.javacode.cah.model.DeckLanguage;
import dk.javacode.proxy.SimpleInterceptor;
import dk.javacode.srsm.SqlSelectColumn;
import dk.javacode.srsm.exceptions.MappingException;
import dk.javacode.srsm.helpers.SqlInsertHelper;
import dk.javacode.srsm.helpers.SqlQueryHelper;

public class MySqlDeckLanguageDao extends AbstractDao implements DeckLanguageDao {
	private SqlQueryHelper sqlBuilder = new SqlQueryHelper();
	private SqlInsertHelper sqlInsert = new SqlInsertHelper();

	public MySqlDeckLanguageDao() {
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<DeckLanguage> findAll() {
		try {
			return sqlBuilder.getAll(connection, DeckLanguage.class);
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}
	

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public DeckLanguage findById(int id) {
		try {
			DeckLanguage language = sqlBuilder.getOne(connection, DeckLanguage.class, new SqlSelectColumn("id", id));
			return language;
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void createLanguage(String name) {
		try {
			sqlInsert.insert(connection, new DeckLanguage(name), "language");
		} catch (Exception e) {
			throw new DaoException(e);
		} 
	}

}
