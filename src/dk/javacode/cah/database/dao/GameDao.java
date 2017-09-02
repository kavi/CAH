package dk.javacode.cah.database.dao;

import java.util.List;

import dk.javacode.cah.database.connection.IDao;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.User;

public interface GameDao extends IDao {


	public List<Game> findAll();
	
	public List<Game> findAllOpen();
	public List<Game> findAllStarted();

	public List<Game> findAllOpen(User user);
	public List<Game> findAllStarted(User user);
	
	public Game findByName(String name);
	public Game findById(int id);
		
	public void update(Game game);
}
