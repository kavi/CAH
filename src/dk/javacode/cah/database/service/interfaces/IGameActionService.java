package dk.javacode.cah.database.service.interfaces;

import java.util.List;

import dk.javacode.cah.model.Deck;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.Player;
import dk.javacode.cah.model.User;
import dk.javacode.cah.model.Vote;
import dk.javacode.cah.model.WhiteCard;

public interface IGameActionService extends IService {
	
	public abstract void startGame(Game game);

	public abstract void drawWhites(Game game);
	public abstract void drawBlack(Game game);
	public boolean playWhites(Game game, Player player, List<WhiteCard> card);

	public abstract void vote(Game game, Player player, Vote vote);

	public abstract void updateGame(Game game);

	public abstract void nextRound(Game game, Player player);
	
	
	// These methods might be moved to a separate service.
	/**
	 * Create a new Game and add the User to the game as a player.

	 * @param game Object containing information about the game.
	 * @param user The owner of the game
	 */
	public void createGame(Game game, User user, List<Deck> decks);
	public Game findGame(int id);
	public void addPlayer(Game game, User user);


}
