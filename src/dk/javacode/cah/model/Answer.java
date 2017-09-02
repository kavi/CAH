package dk.javacode.cah.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dk.javacode.srsm.annotations.Column;
import dk.javacode.srsm.annotations.Table;

@Table(name = "answer")
public class Answer {
	@Column(name = "id", primaryKey = true)
	private Integer id;

	@Column(name = "player_id", columnReference = "id")
	private Player playedBy;

	// @Column(name = "white_card_id", columnReference = "id")
	// private WhiteCard card;

	@Column(name = "question_id", columnReference = "id")
	private ActiveBlackCard playedFor;

	private List<WhiteCard> cards = new ArrayList<WhiteCard>();
	private List<Vote> votes = new ArrayList<Vote>();

	public Answer() {
		super();
	}

	// public PlayedCard(Player playedBy, WhiteCard card, ActiveBlackCard
	// playedFor) {
	// super();
	// this.playedFor = playedFor;
	// this.playedBy = playedBy;
	// this.card = card;
	// }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Player getPlayedBy() {
		return playedBy;
	}

	public void setPlayedBy(Player playedBy) {
		this.playedBy = playedBy;
	}

	public List<WhiteCard> getCards() {
		return cards;
	}

	public void setCards(List<WhiteCard> cards) {
		this.cards = cards;
	}

	public ActiveBlackCard getPlayedFor() {
		return playedFor;
	}

	public void setPlayedFor(ActiveBlackCard playedFor) {
		this.playedFor = playedFor;
	}

	public void addVote(Vote v) {
		votes.add(v);
	}

	public void removeVote(Vote v) {
		votes.remove(v);
	}

	public List<Vote> getVotes() {
		return Collections.unmodifiableList(votes);
	}

	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}

	public int getScore() {
		int score = 0;
		for (Vote v : votes) {
			score += v.getScore();
		}
		return score;
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
		Answer other = (Answer) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Answer [id=" + id + ", playedBy=" + playedBy + ", cards=" + cards + "]";
	}

}
