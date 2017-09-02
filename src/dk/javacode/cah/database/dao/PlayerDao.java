package dk.javacode.cah.database.dao;

import java.util.List;

import dk.javacode.cah.database.connection.IDao;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.model.User;


public interface PlayerDao extends IDao {

	public void createPlayer(Game game, User user);
	
	public void loadHand(Player p);
	public Player find(User user, Game game);
	public Player findById(int id);
	public List<Player> findAll(Game game);
	
	public List<Player> findAllActive(Game game);
	
	public void update(Player player);
	
	public void setActionPerformed(Player player, boolean actionPerformed); // TODO - change to generic update method ?

}
