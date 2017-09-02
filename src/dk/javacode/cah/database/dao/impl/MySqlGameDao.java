package dk.javacode.cah.database.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.dao.GameDao;
import dk.javacode.cah.database.dao.PlayerDao;
import dk.javacode.cah.database.dao.UserDao;
import dk.javacode.cah.model.ActiveBlackCard;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.model.User;
import dk.javacode.proxy.InterceptorProxy;
import dk.javacode.proxy.SimpleInterceptor;
import dk.javacode.srsm.ResultSetMapper;
import dk.javacode.srsm.SqlSelectColumn;
import dk.javacode.srsm.SqlSelectColumn.SqlOperator;
import dk.javacode.srsm.exceptions.MappingException;
import dk.javacode.srsm.helpers.SqlQueryDsl;
import dk.javacode.srsm.helpers.SqlQueryHelper;

public class MySqlGameDao extends AbstractDao implements GameDao {
	private SqlQueryHelper sqlBuilder = new SqlQueryHelper();
	private ResultSetMapper mapper= new ResultSetMapper();
	
	private PlayerDao playerDao = InterceptorProxy.buildProxy(PlayerDao.class, new MySqlPlayerDao());
	private UserDao userDao = InterceptorProxy.buildProxy(UserDao.class, new MySqlUserDao());

	public MySqlGameDao() {
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<Game> findAll() {
		try {
			return sqlBuilder.getAll(connection, Game.class);
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<Game> findAllOpen() {
		try {
			return sqlBuilder.getAll(connection, Game.class, new SqlSelectColumn("active", true), new SqlSelectColumn(
					"started", false));
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<Game> findAllStarted() {
		try {
			return sqlBuilder.getAll(connection, Game.class, new SqlSelectColumn("active", true), new SqlSelectColumn(
					"started", true));
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<Game> findAllOpen(User user) {
		try {
			return sqlBuilder.getAll(connection, Game.class, new SqlSelectColumn("active", true), new SqlSelectColumn(
					"started", false), new SqlSelectColumn("owner_id", user.getId()));
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<Game> findAllStarted(User user) {
		try {
			PreparedStatement stmt = connection.prepareStatement("select g.id, g.name, g.phase_id, g.active, g.started, g.password, g.hand_size, g.rounds_played, g.number_of_rounds from game g join player p on p.game_id = g.id where p.user_id = ? and g.started = ? and g.active = ?");
			stmt.setInt(1, user.getId());
			stmt.setBoolean(2, true);
			stmt.setBoolean(3, true);
			ResultSet result = stmt.executeQuery();
			return mapper.toPojo(Game.class, result);
//			return sqlBuilder.getAll(connection, Game.class, new SqlSelectColumn("active", true), new SqlSelectColumn(
//					"started", true));
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public Game findByName(String name) {
		try {
			// TODO - make this SQL nicer
			Game game = sqlBuilder.getOne(connection, Game.class, new SqlSelectColumn("name", name));
			game.setOwner(userDao.findById(game.getOwner().getId()));
			List<Player> players = playerDao.findAll(game);
			game.setPlayers(players);
			return game;
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}
	
	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public Game findById(int id) {
		try {
			Game game = SqlQueryDsl.build(Game.class).includeReferences().where("id", SqlOperator.EQUALS, id).selectOne(connection);
			if (game == null) {
				return null;
			}
			
			ActiveBlackCard activeBlackCard = SqlQueryDsl.build(ActiveBlackCard.class).eager()
					.where("game_id", SqlOperator.EQUALS, game.getId())
					.and("active", SqlOperator.EQUALS, true)
					.selectOne(connection);
			game.setActiveBlackCard(activeBlackCard);
			game.setPlayers(SqlQueryDsl.build(Player.class).includeReferences().where("game_id", SqlOperator.EQUALS, game.getId()).selectAll(connection));
			return game;
			
//			 TODO - Remove old implementation once the new one is tested..
//			Game game = sqlBuilder.getOne(connection, Game.class, new SqlSelectColumn("id", id));
//			if (game == null) {
//				return null;
//			}
//			game.setOwner(sqlBuilder.getOne(connection, User.class, new SqlSelectColumn("id", game.getOwner().getId())));
//			
//			ActiveBlackCard activeBlackCard = sqlBuilder.getOne(connection, ActiveBlackCard.class, new SqlSelectColumn("game_id", id), new SqlSelectColumn("active", true));
//			if (activeBlackCard != null) {
//				activeBlackCard.setCard(sqlBuilder.getOne(connection, BlackCard.class, new SqlSelectColumn("id", activeBlackCard.getCard().getId())));
//			}
//			game.setActiveBlackCard(activeBlackCard);
//			
//			List<Player> players = playerDao.findAll(game);
//			game.setPlayers(players);
//			return game;
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void update(Game game) {
		try {
			PreparedStatement stmt = connection
					.prepareStatement("update game set started = ?, active = ?, family_filter = ?, phase_id = ?, hand_size = ?, rounds_played = ?, number_of_rounds = ? where id = ?");
			stmt.setBoolean(1, game.isStarted());
			stmt.setBoolean(2, game.isActive());
			stmt.setBoolean(3, game.isFamilyFilter());
			if (game.getPhase() != null) {
				stmt.setInt(4, game.getPhase().getId());
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			stmt.setInt(5, game.getHandSize());
			stmt.setInt(6, game.getRoundsPlayed());
			stmt.setInt(7, game.getNumberOfRounds());
			stmt.setInt(8, game.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}
}
