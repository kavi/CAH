package dk.javacode.cah.database.dao;

import java.util.List;

import dk.javacode.cah.database.connection.IDao;
import dk.javacode.cah.model.Deck;
import dk.javacode.cah.model.DeckLanguage;


public interface DeckDao extends IDao {

	public List<Deck> findAll();
	public Deck findById(int id);
	public void createDeck(Deck deck);
	public List<Deck> findByLanguage(DeckLanguage language);
	public Deck findByName(DeckLanguage langauge, String name);

}
