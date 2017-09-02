package dk.javacode.cah.database.dao.impl;

import java.sql.SQLException;
import java.util.List;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.dao.PlayedCardDao;
import dk.javacode.cah.model.ActiveBlackCard;
import dk.javacode.cah.model.Answer;
import dk.javacode.cah.model.WhiteCard;
import dk.javacode.proxy.SimpleInterceptor;
import dk.javacode.srsm.SqlExecuter;
import dk.javacode.srsm.SqlParam;
import dk.javacode.srsm.SqlSelectColumn;
import dk.javacode.srsm.exceptions.MappingException;
import dk.javacode.srsm.helpers.SqlQueryHelper;

public class MySqlAnswerDao extends AbstractDao implements PlayedCardDao {
	private SqlQueryHelper sqlBuilder = new SqlQueryHelper();
	private SqlExecuter<WhiteCard> whiteCardExecuter = new SqlExecuter<WhiteCard>(WhiteCard.class);

	public MySqlAnswerDao() {
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<Answer> findByActiveBlackCard(ActiveBlackCard activeBlackCard) {
		try {
			List<Answer> answers = sqlBuilder.selectAll(connection, Answer.class, new SqlSelectColumn("question_id", activeBlackCard.getId()));
			for (Answer a : answers) {
				String select = sqlBuilder.buildSelectSql(WhiteCard.class).getSelect();
				select += " join answer_cards as ac on ac.white_card_id = white_card.id where ac.answer_id = :answerId order by ac.position";
				List<WhiteCard> cards = whiteCardExecuter.executeQuery(connection, select, new SqlParam("answerId", a.getId()));
				a.setCards(cards);
			}
			activeBlackCard.setAnswers(answers);
			return answers;
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public Answer findById(int id) {
		try {
			return sqlBuilder.getOne(connection, Answer.class, new SqlSelectColumn("id", id));
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

}
