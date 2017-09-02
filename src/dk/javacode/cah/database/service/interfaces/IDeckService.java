package dk.javacode.cah.database.service.interfaces;

import java.util.List;

import dk.javacode.cah.model.Deck;
import dk.javacode.cah.model.DeckLanguage;

public interface IDeckService extends IService {
	
	public abstract void createDeck(Deck newdeck);
	public abstract Deck findById(int id); // Include cards
	
	public abstract List<Deck> findAll();
	public abstract List<DeckLanguage> findLanguages();
	public abstract DeckLanguage findLanguageById(int id);
	public abstract List<Deck> findByLanguage(DeckLanguage language);
	public abstract Deck findByName(DeckLanguage langauge, String name);

}
