package dk.javacode.cah.database.dao;

import java.util.List;

import dk.javacode.cah.database.connection.IDao;
import dk.javacode.cah.model.DeckLanguage;


public interface DeckLanguageDao extends IDao {

	public List<DeckLanguage> findAll();
	public DeckLanguage findById(int id);
	public void createLanguage(String language);

}
