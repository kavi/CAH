package dk.javacode.cah.database.dao;

import java.util.List;

import dk.javacode.cah.database.connection.IDao;
import dk.javacode.cah.model.BlackCard;
import dk.javacode.cah.model.Game;


public interface BlackCardDiscardDao extends IDao {

	public List<BlackCard> findAll(Game g);
	
	public void remove(Game game, BlackCard card);
	
	public void add(Game game, BlackCard card);
}
