package dk.javacode.cah.database.dao;

import java.util.List;

import dk.javacode.cah.database.connection.IDao;
import dk.javacode.cah.model.ActiveBlackCard;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.model.WhiteCard;


public interface WhiteCardDeckDao extends IDao {

	public List<WhiteCard> findCardsInDeck(Game g);
	public List<WhiteCard> findCardsInDiscard(Game g);
	
	public void moveFromDeckToPlayer(Game game, Player player, WhiteCard card);
	public void moveFromPlayerToDiscard(Game game, Player player, WhiteCard card);
	public void moveFromDiscardToDeck(Game game, WhiteCard card);

	public void playCard(Game game, Player player, List<WhiteCard> cards, ActiveBlackCard activeBlackCard);
}
