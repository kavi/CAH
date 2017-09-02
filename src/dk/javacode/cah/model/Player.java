package dk.javacode.cah.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dk.javacode.srsm.annotations.Column;
import dk.javacode.srsm.annotations.Table;

@Table(name = "player")
public class Player {

	@Column(name = "id", primaryKey = true)
	private Integer id;

	@Column(name = "user_id", columnReference = "id")
	private User user;

	@Column(name = "game_id")
	private Integer game;

	@Column(name = "points")
	private int points;

	@Column(name = "active")
	private boolean active;

	@Column(name = "action_performed")
	private boolean actionPerformed;

	private List<WhiteCard> cards = new ArrayList<WhiteCard>();

	public Player() {
		super();
	}

	public Player(User user, Integer game) {
		super();
		this.user = user;
		this.game = game;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGame() {
		return game;
	}

	public void setGame(Integer game) {
		this.game = game;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActionPerformed() {
		return actionPerformed;
	}

	public void setActionPerformed(boolean actionPerformed) {
		this.actionPerformed = actionPerformed;
	}

	public void addCard(WhiteCard card) {
		this.cards.add(card);
	}

	public void removeCard(WhiteCard card) {
		this.cards.remove(card);
	}

	public void setCards(List<WhiteCard> cards) {
		this.cards = cards;
	}

	public void addAllCards(Collection<WhiteCard> cards) {
		this.cards.addAll(cards);
	}

	public List<WhiteCard> getCards() {
		return cards;
	}
	
	@Override
	public String toString() {
		return "Player [id=" + id + ", user=" + user + ", points=" + points + ", active=" + active + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((game == null) ? 0 : game.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (game == null) {
			if (other.game != null)
				return false;
		} else if (!game.equals(other.game))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}