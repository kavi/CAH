package dk.javacode.cah.database.service.interfaces;

import java.sql.Connection;

import dk.javacode.cah.database.dao.BlackCardDao;
import dk.javacode.cah.database.dao.BlackCardDeckDao;
import dk.javacode.cah.database.dao.DeckDao;
import dk.javacode.cah.database.dao.DeckLanguageDao;
import dk.javacode.cah.database.dao.GameDao;
import dk.javacode.cah.database.dao.PlayedCardDao;
import dk.javacode.cah.database.dao.PlayerDao;
import dk.javacode.cah.database.dao.VoteDao;
import dk.javacode.cah.database.dao.WhiteCardDao;
import dk.javacode.cah.database.dao.WhiteCardDeckDao;
import dk.javacode.cah.database.dao.impl.MySqlAnswerDao;
import dk.javacode.cah.database.dao.impl.MySqlBlackCardDao;
import dk.javacode.cah.database.dao.impl.MySqlBlackCardDeckDao;
import dk.javacode.cah.database.dao.impl.MySqlDeckDao;
import dk.javacode.cah.database.dao.impl.MySqlDeckLanguageDao;
import dk.javacode.cah.database.dao.impl.MySqlGameDao;
import dk.javacode.cah.database.dao.impl.MySqlPlayerDao;
import dk.javacode.cah.database.dao.impl.MySqlVoteDao;
import dk.javacode.cah.database.dao.impl.MySqlWhiteCardDao;
import dk.javacode.cah.database.dao.impl.MySqlWhiteCardDeckDao;

public abstract class AbstractService implements IService {
	
	protected Connection connection;
	
	protected WhiteCardDeckDao whiteCardDeckDao;
	protected BlackCardDeckDao blackCardDeckDao;
	protected DeckDao deckDao;
	protected DeckLanguageDao deckLanguageDao;
	
	protected WhiteCardDao whiteCardDao;
	protected BlackCardDao blackCardDao;
	
	protected PlayedCardDao playedCardDao;
	protected PlayerDao playerDao;
	
	protected GameDao gameDao;
	protected VoteDao voteDao;

	public AbstractService() {
		super();
		whiteCardDeckDao = new MySqlWhiteCardDeckDao();
		blackCardDeckDao = new MySqlBlackCardDeckDao();
		deckDao = new MySqlDeckDao();
		deckLanguageDao = new MySqlDeckLanguageDao();
		
		whiteCardDao = new MySqlWhiteCardDao();
		blackCardDao = new MySqlBlackCardDao();
		
		playedCardDao = new MySqlAnswerDao();
		playerDao = new MySqlPlayerDao();
		
		gameDao = new MySqlGameDao();
		voteDao = new MySqlVoteDao();
	}

	@Override
	public Connection getConnection() {
		return this.connection;		
	}

	@Override
	public void setConnection(Connection c) {
//		log.debug("Setting connection...");
		this.connection = c;
		voteDao.setConnection(connection);
		gameDao.setConnection(connection);
		playedCardDao.setConnection(connection);
		playerDao.setConnection(connection);
		whiteCardDeckDao.setConnection(connection);
		whiteCardDao.setConnection(connection);
		blackCardDeckDao.setConnection(connection);
		blackCardDao.setConnection(connection);
		deckDao.setConnection(connection);
		deckLanguageDao.setConnection(connection);
	}
	

}
