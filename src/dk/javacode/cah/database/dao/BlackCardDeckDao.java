package dk.javacode.cah.database.dao;

import java.util.List;

import dk.javacode.cah.database.connection.IDao;
import dk.javacode.cah.model.ActiveBlackCard;
import dk.javacode.cah.model.BlackCard;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.Answer;


public interface BlackCardDeckDao extends IDao {

	public List<BlackCard> findCardsInDeck(Game g);
	public ActiveBlackCard findActiveBlackCard(Game g);
	public List<Answer> findPlayedCards(ActiveBlackCard card);
	public List<BlackCard> findCardsInDiscard(Game g);
	
	public ActiveBlackCard moveToActive(Game game, BlackCard card);	
	public void moveFromActiveToDiscard(Game game, ActiveBlackCard card);
	public void moveFromDiscardToDeck(Game game, BlackCard card);
}
