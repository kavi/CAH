package dk.javacode.cah.database.service.interfaces;

import java.util.List;

import dk.javacode.cah.model.BlackCard;
import dk.javacode.cah.model.User;
import dk.javacode.cah.model.dto.CardDetailDTO;

public interface IBlackCardService extends IService {
	
	public abstract void editCard(int id, User user, String text, int cardsToPick, int cardsToDraw, Boolean safeForWork);
	public abstract void createCard(User user, BlackCard newcard);
	public abstract void softDeleteCard(int id);
	public abstract BlackCard findById(int id);
	public abstract CardDetailDTO<BlackCard> findWithDetailsById(int id);
	public abstract List<BlackCard> findAllAvailable(boolean familyFilter, List<Integer> deckIds);

}
