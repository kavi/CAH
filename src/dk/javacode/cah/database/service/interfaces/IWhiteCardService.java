package dk.javacode.cah.database.service.interfaces;

import java.util.List;

import dk.javacode.cah.model.User;
import dk.javacode.cah.model.WhiteCard;
import dk.javacode.cah.model.dto.CardDetailDTO;

public interface IWhiteCardService extends IService {
	
	public abstract void editCard(int id, User changedBy, String text, Boolean safeForWork, Boolean disabled);
	public abstract void createCard(User createdBy, WhiteCard newcard);
	public abstract void deleteCard(int id);
	public abstract WhiteCard findById(int id);
	public abstract CardDetailDTO<WhiteCard> findWithDetailsById(int id);
	//public abstract List<WhiteCard> findAllAvailable(boolean familyFilter, int[] deckIds);
	public abstract List<WhiteCard> findAllAvailable(boolean familyFilter, List<Integer> deckIds);
}
