package dk.javacode.cah.database.dao;

import java.util.List;

import dk.javacode.cah.database.connection.IDao;
import dk.javacode.cah.model.WhiteCard;

public interface WhiteCardDao extends IDao {

	public void hardDeleteCard(int id);
	public void softDeleteCard(int id);	
	
	public List<WhiteCard> findAll(boolean familyFilter);
	public List<WhiteCard> findAllAvailable(boolean familyFilter, int deckId);

	
	public WhiteCard findById(int id);
	
	public void createCard(WhiteCard card);
	
	public void updateCard(WhiteCard card);

}
