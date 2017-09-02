package dk.javacode.cah.database.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.service.interfaces.AbstractService;
import dk.javacode.cah.database.service.interfaces.IDeckService;
import dk.javacode.cah.model.Deck;
import dk.javacode.cah.model.DeckLanguage;
import dk.javacode.notjsr.NotJSRValidator;
import dk.javacode.notjsr.ValidationError;
import dk.javacode.proxy.SimpleInterceptor;

public class DeckService extends AbstractService implements IDeckService {

//	private static Logger log = Logger.getLogger(DeckService.class);

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void createDeck(Deck newdeck) {
		List<ValidationError<Deck>> validationResult = NotJSRValidator.validate(newdeck);
		if (!validationResult.isEmpty()) {
			throw new DaoException("Deck did not validate." + validationResult.get(0).getMessage());
		}
		deckDao.createDeck(newdeck);
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public Deck findById(int id) {
		return deckDao.findById(id);
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<Deck> findAll() {
		return deckDao.findAll();
	}
	
	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public Deck findByName(DeckLanguage langauge, String name) {
		return deckDao.findByName(langauge, name);
	}
	
	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<Deck> findByLanguage(DeckLanguage language) {
		List<Deck> result = deckDao.findByLanguage(language);
		Collections.sort(result, new Comparator<Deck>() {
			@Override
			public int compare(Deck o1, Deck o2) {
				return o1.getId() - o2.getId();
			}			
		});
		return result;
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<DeckLanguage> findLanguages() {
		return deckLanguageDao.findAll();
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public DeckLanguage findLanguageById(int id) {
		return deckLanguageDao.findById(id);
	}
	
	
}
