package dk.javacode.cah.database.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.service.interfaces.AbstractService;
import dk.javacode.cah.database.service.interfaces.IWhiteCardService;
import dk.javacode.cah.model.User;
import dk.javacode.cah.model.WhiteCard;
import dk.javacode.cah.model.dto.CardDetailDTO;
import dk.javacode.proxy.SimpleInterceptor;

public class WhiteCardService extends AbstractService implements IWhiteCardService {

	private static Logger log = Logger.getLogger(WhiteCardService.class);

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void editCard(int id, User user, String text, Boolean safeForWork, Boolean disabled) {
		WhiteCard originalcard = whiteCardDao.findById(id);
		if (originalcard == null) {
			throw new DaoException("Card with id: " + id + " not found");
		}
		if (safeForWork == null) {
			safeForWork = originalcard.isSafeForWork();
		}
		if (disabled == null) {
			disabled = originalcard.isDisabled();
		}
		text = text.trim();
		WhiteCard newcard = new WhiteCard(text, safeForWork, originalcard.getDeckId());
		
		if (originalcard.getText().equals(text)) {
			log.debug("Text has not changed. Card updated - no new revision");
			originalcard.setDisabled(disabled);
			originalcard.setSafeForWork(safeForWork);
			whiteCardDao.updateCard(originalcard);
			return;
		}

		newcard.setChangedBy(user);
		newcard.setParentId(originalcard.getId());
		whiteCardDao.createCard(newcard);
		
		whiteCardDao.softDeleteCard(originalcard.getId());
		
		log.debug("Making new card instead of updating old card. Old card has been disabled. (New revision)");
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void createCard(User user, WhiteCard card) {
		String text = card.getText().trim();
		card.setChangedBy(user);
		card.setText(text);
		whiteCardDao.createCard(card);
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void deleteCard(int id) {
		whiteCardDao.softDeleteCard(id);
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public WhiteCard findById(int id) {
		return whiteCardDao.findById(id);
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public CardDetailDTO<WhiteCard> findWithDetailsById(int id) {
		WhiteCard card = whiteCardDao.findById(id);
		if (card == null) {
			return null;
		}
		CardDetailDTO<WhiteCard> cardDTO = new CardDetailDTO<WhiteCard>(card);
		WhiteCard current = card;
		List<WhiteCard> revisions = new ArrayList<WhiteCard>();
		revisions.add(card);
		while (current.getParentId() != null) {
			WhiteCard parent = whiteCardDao.findById(current.getParentId());
			revisions.add(parent);
			current = parent;
		}
		cardDTO.setRevisions(revisions);
		return cardDTO;
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public List<WhiteCard> findAllAvailable(boolean familyFilter, List<Integer> deckIds) {
		List<WhiteCard> res =  new ArrayList<WhiteCard>();
		for (int deckId : deckIds) {
			res.addAll(whiteCardDao.findAllAvailable(familyFilter, deckId));
		}
		return res;
	}
	
	
}
