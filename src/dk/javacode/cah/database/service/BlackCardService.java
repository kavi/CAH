package dk.javacode.cah.database.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.service.interfaces.AbstractService;
import dk.javacode.cah.database.service.interfaces.IBlackCardService;
import dk.javacode.cah.model.BlackCard;
import dk.javacode.cah.model.User;
import dk.javacode.cah.model.dto.CardDetailDTO;
import dk.javacode.notjsr.NotJSRValidator;
import dk.javacode.notjsr.ValidationError;
import dk.javacode.proxy.SimpleInterceptor;

public class BlackCardService extends AbstractService implements IBlackCardService {

	private static Logger log = Logger.getLogger(BlackCardService.class);

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void editCard(int id, User user, String text, int cardsToPick, int cardsToDraw, Boolean safeForWork) {
		BlackCard originalcard = blackCardDao.findById(id);
		if (originalcard == null) {
			throw new DaoException("Card not found");
		}
		if (safeForWork == null) {
			safeForWork = originalcard.isSafeForWork();
		}
		text = text.trim();
		BlackCard newcard = new BlackCard(text, cardsToPick, cardsToDraw, safeForWork, originalcard.getDeckId());
		
		List<ValidationError<BlackCard>> validate = NotJSRValidator.validate(newcard);
		if (!validate.isEmpty()) {
			String msgs = "";
			for (ValidationError<BlackCard> e : validate) {
				msgs += e.getMessage() + "\n";
			}
			log.warn("Validation error: " + msgs);
			throw new DaoException("New card details invalid. Cannot update. " + msgs); 
		}

		if (originalcard.getText().equals(text) && originalcard.getCardsToDraw() == cardsToDraw && originalcard.getCardsToPick() == cardsToPick) {
			log.debug("Text and cards to pick/draw has not changed. Card updated - no new revision");
			originalcard.setSafeForWork(safeForWork);
			blackCardDao.updateCard(originalcard);
			return;
		}

		
		newcard.setChangedBy(user);
		newcard.setParentId(originalcard.getId());
		blackCardDao.createCard(newcard);
		
		originalcard.setDisabled(true);
		blackCardDao.updateCard(originalcard);
		
		log.debug("Making new card instead of updating old card. Old card has been disabled. (New revision)");
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void createCard(User user, BlackCard card) {
		List<ValidationError<BlackCard>> validate = NotJSRValidator.validate(card);
		if (!validate.isEmpty()) {
			throw new DaoException("Invalid card. Cannot create.");
		}
		String text = card.getText().trim();
		card.setChangedBy(user);
		card.setText(text);
		blackCardDao.createCard(card);
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void softDeleteCard(int id) {
		blackCardDao.softDeleteCard(id);
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public BlackCard findById(int id) {
		return blackCardDao.findById(id);	
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public CardDetailDTO<BlackCard> findWithDetailsById(int id) {
		BlackCard card = blackCardDao.findById(id);
		if (card == null) {
			return null;
		}
		BlackCard current = card;		
		List<BlackCard> revisions = new ArrayList<BlackCard>();
		revisions.add(card);
		while (current.getParentId() != null) {
			BlackCard parent = blackCardDao.findById(current.getParentId());
			revisions.add(parent);
			current = parent;
		}
		CardDetailDTO<BlackCard> cardDTO = new CardDetailDTO<BlackCard>(card);
		cardDTO.setRevisions(revisions);
		return cardDTO;
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<BlackCard> findAllAvailable(boolean familyFilter, List<Integer> deckIds) {
		List<BlackCard> blackCards = new ArrayList<BlackCard>();
		for (int deckId : deckIds) {
			blackCards.addAll(blackCardDao.findAllAvailable(familyFilter, deckId));
		}
		return blackCards;
	}
	
}
