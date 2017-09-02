package dk.javacode.cah.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import dk.javacode.srsm.annotations.Column;
import dk.javacode.srsm.annotations.Table;

@Table(name = "game")
public class Game {

	@Column(name = "id", primaryKey = true)
	private Integer id;

	@Column(name = "name")
	@NotNull
	@Pattern(regexp = "[\\w|\\s|\\d|\\?|\\!|\\+|\\&|/|'|,|\\.]+")
	@Size(min = 3, max = 60)
	private String name;

	@Column(name = "password")
	private String password;

	@Column(name = "owner_id", columnReference = "id")
	private User owner;

	@Column(name = "started")
	private boolean started;

	@Column(name = "active")
	private boolean active;

	@Column(name = "phase_id")
	private Integer phaseId;

	@Column(name = "family_filter")
	private boolean familyFilter;
	
	@Column(name = "hand_size")
	private int handSize = 10;
	
	@Column(name = "rounds_played")
	private int roundsPlayed = 0;
	
	@Column(name = "number_of_rounds")
	private int numberOfRounds = 30;

	// TODO: This is a DTO-property!
	private Player currentPlayer = null;

	@Column(name = "game_id", collection = true, collectionType = Player.class)
	private List<Player> players = new ArrayList<Player>();
	
	// Reverse column (activeBlackCard has a foreign_key to game_id) 
	private ActiveBlackCard activeBlackCard; 

	private GamePhase phase;

	public Game() {
		super();
	}

	public Game(String name) {
		super();
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public GamePhase getPhase() {
		return phase;
	}

	/**
	 * This will also set the phaseId to the id of the phase. If phase is null
	 * phaseId will be set to null.
	 * 
	 * @param phase
	 */
	public void setPhase(GamePhase phase) {
		this.phase = phase;
		if (phase != null) {
			this.phaseId = phase.getId();
		} else {
			phaseId = null;
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public ActiveBlackCard getActiveBlackCard() {
		return activeBlackCard;
	}

	public void setActiveBlackCard(ActiveBlackCard activeBlackCard) {
		this.activeBlackCard = activeBlackCard;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isFamilyFilter() {
		return familyFilter;
	}

	public void setFamilyFilter(boolean familyFilter) {
		this.familyFilter = familyFilter;
	}

	public Integer getPhaseId() {
		return phaseId;
	}

	/**
	 * Setting the phaseId will also set the GamePhase. If phaseId is null,
	 * phase will be set to null.
	 * 
	 * @param phaseId
	 */
	public void setPhaseId(Integer phaseId) {
		this.phaseId = phaseId;
		if (phaseId == null) {
			this.phase = null;
		} else {
			this.phase = GamePhase.get(phaseId);
		}
	}

	public void addPlayers(Player p) {
		players.add(p);
	}

	public void addAllPlayers(Collection<Player> p) {
		players.addAll(p);
	}

	public void removePlayer(Player p) {
		players.remove(p);
	}

	public void removeAllPlayers() {
		players.clear();
	}

	public List<Player> getPlayers() {
		return Collections.unmodifiableList(players);
	}

	public List<Player> listActivePlayers() {
		List<Player> res = new ArrayList<Player>();
		for (Player p : players) {
			if (p.isActive()) {
				res.add(p);
			}
		}
		return Collections.unmodifiableList(res);
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	
	
	public int getHandSize() {
		return handSize;
	}

	public void setHandSize(int handSize) {
		this.handSize = handSize;
	}

	public int getNumberOfRounds() {
		return numberOfRounds;
	}

	public void setNumberOfRounds(int numberOfRounds) {
		this.numberOfRounds = numberOfRounds;
	}

	public int getRoundsPlayed() {
		return roundsPlayed;
	}

	public void setRoundsPlayed(int roundsPlayed) {
		this.roundsPlayed = roundsPlayed;
	}

	@Override
	public String toString() {
		return "Game [id=" + id + ", name=" + name + ", password=" + password + ", owner=" + owner + ", started=" + started + ", familyFilter="
				+ familyFilter + ", active=" + active + ", phaseId=" + phaseId + ", players=" + players + ", activeBlackCard=" + activeBlackCard
				+ ", phase=" + phase + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Game other = (Game) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
