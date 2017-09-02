package dk.javacode.cah.database.dao;

import java.util.List;

import dk.javacode.cah.database.connection.IDao;
import dk.javacode.cah.model.BlackCard;


public interface BlackCardDao extends IDao {

	public void hardDeleteCard(int id);
	public void softDeleteCard(int id);	

	public List<BlackCard> findAll(boolean familyFilter, int deckId);
	
	public List<BlackCard> findAllAvailable(boolean familyFilter, int deckId);
	
	public BlackCard findById(int id);
	
	public void createCard(BlackCard card);
	
	public void updateCard(BlackCard card);

}
