package dk.javacode.cah.database.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.dao.PlayerDao;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.model.User;
import dk.javacode.cah.model.WhiteCard;
import dk.javacode.proxy.SimpleInterceptor;
import dk.javacode.srsm.ResultSetMapper;
import dk.javacode.srsm.SqlSelectColumn;
import dk.javacode.srsm.exceptions.MappingException;
import dk.javacode.srsm.helpers.SqlInsertHelper;
import dk.javacode.srsm.helpers.SqlQueryHelper;
import dk.javacode.srsm.helpers.SqlQueryHelper.SqlSelect;

public class MySqlPlayerDao extends AbstractDao implements PlayerDao {
	private SqlQueryHelper sqlBuilder = new SqlQueryHelper();
	private SqlInsertHelper sqlInsert = new SqlInsertHelper();
	private ResultSetMapper mapper = new ResultSetMapper();

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void createPlayer(Game game, User user) {
		if (!game.isActive()) {
			throw new DaoException("Cannot add player to inactive game.");
		}
		if (game.isStarted()) {
			throw new DaoException("Cannot add player to started game.");
		}
		if (!game.isFamilyFilter() && user.isFamilyFilter()) {
			throw new DaoException("FamilyFilter user cannot join non-family-filter game");
		}
		

		try {
			Player player = new Player(user, game.getId());
			player.setActive(true);			
			sqlInsert.insert(connection, player, "player");
			game.addPlayers(player);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<Player> findAll(Game game) {
		try {
			SqlSelect selectSql = sqlBuilder.buildSelectSql(Player.class);
			selectSql.addJoin(User.class, "id", "user_id");
			return sqlBuilder.selectAll(connection, selectSql, Player.class,
					new SqlSelectColumn("game_id", game.getId()));
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<Player> findAllActive(Game game) {
		try {
			return sqlBuilder.getAll(connection, Player.class, new SqlSelectColumn("game_id", game.getId()),
					new SqlSelectColumn("active", true));
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public Player find(User user, Game game) {
		try {
			Player player = sqlBuilder.getOne(connection, Player.class, new SqlSelectColumn("game_id", game.getId()),
					new SqlSelectColumn("user_id", user.getId()));
			PreparedStatement stmt = connection
					.prepareStatement("select wc.id,wc.text,wc.disabled,wc.created,wc.parent_id from player_card pc join white_card wc on wc.id = pc.white_card_id where player_id = ?");
			stmt.setInt(1, player.getId());
			ResultSet result = stmt.executeQuery();
			List<WhiteCard> cards = mapper.toPojo(WhiteCard.class, result);
			player.setCards(cards);
			player.setGame(game.getId());
			player.setUser(user);
			return player;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public Player findById(int id) {
		try {
			Player player = sqlBuilder.getOne(connection, Player.class, new SqlSelectColumn("id", id));
			return player;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void loadHand(Player player) {
		try {
			PreparedStatement stmt = connection
					.prepareStatement("select wc.id,wc.text,wc.disabled,wc.created,wc.parent_id from player_card pc join white_card wc on wc.id = pc.white_card_id where player_id = ?");
			stmt.setInt(1, player.getId());
			ResultSet result = stmt.executeQuery();
			List<WhiteCard> cards = mapper.toPojo(WhiteCard.class, result);
			player.setCards(cards);
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (MappingException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void setActionPerformed(Player player, boolean actionPerformed) {
		try {
			player.setActionPerformed(actionPerformed);
			PreparedStatement stmt = connection.prepareStatement("update player set action_performed = ? where id = ?");
			stmt.setBoolean(1, actionPerformed);
			stmt.setInt(2, player.getId());
			stmt.executeUpdate();
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void update(Player player) {
		try {
			PreparedStatement stmt = connection.prepareStatement("update player set points = ?, action_performed = ?, active = ? where id = ?");
			stmt.setInt(1, player.getPoints());
			stmt.setBoolean(2, player.isActionPerformed());
			stmt.setBoolean(3, player.isActive());
			stmt.setInt(4, player.getId());
			stmt.executeUpdate();
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
}
