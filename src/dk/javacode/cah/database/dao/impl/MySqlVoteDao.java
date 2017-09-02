package dk.javacode.cah.database.dao.impl;

import java.sql.SQLException;
import java.util.List;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.dao.VoteDao;
import dk.javacode.cah.model.Answer;
import dk.javacode.cah.model.Vote;
import dk.javacode.proxy.SimpleInterceptor;
import dk.javacode.srsm.SqlSelectColumn;
import dk.javacode.srsm.exceptions.MappingException;
import dk.javacode.srsm.helpers.SqlInsertHelper;
import dk.javacode.srsm.helpers.SqlQueryHelper;

public class MySqlVoteDao extends AbstractDao implements VoteDao {
	private SqlQueryHelper sqlBuilder = new SqlQueryHelper();
	private SqlInsertHelper sqlInsert = new SqlInsertHelper();

	public MySqlVoteDao() {
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<Vote> findByPlayedCard(Answer playedCard) {
		try {
			List<Vote> votes = sqlBuilder.getAll(connection, Vote.class, new SqlSelectColumn(
					"played_card_id", playedCard.getId()));
			playedCard.setVotes(votes);
			return votes;
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public Vote findById(int id) {
		try {
			return sqlBuilder.getOne(connection, Vote.class, new SqlSelectColumn("id", id));
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void createVote(Vote vote) {
		try {
			sqlInsert.insert(connection, vote, "vote");
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
}
