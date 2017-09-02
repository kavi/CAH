package dk.javacode.cah.model;

import dk.javacode.srsm.annotations.Column;
import dk.javacode.srsm.annotations.Table;

@Table(name = "vote")
public class Vote {
	@Column(name = "id", primaryKey = true)
	private Integer id;

	@Column(name = "score")
	private int score;

	@Column(name = "player_id", columnReference = "id")
	private Player votedBy;
	
	@Column(name = "played_card_id", columnReference = "id")
	private Answer playedCard;

	public Vote() {
		super();
	}

	public Vote(int score, Player votedBy) {
		super();
		this.score = score;
		this.votedBy = votedBy;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Player getVotedBy() {
		return votedBy;
	}

	public Answer getPlayedCard() {
		return playedCard;
	}

	public void setPlayedCard(Answer voteFor) {
		this.playedCard = voteFor;
	}

	public void setVotedBy(Player votedBy) {
		this.votedBy = votedBy;
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
		Vote other = (Vote) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Vote [id=" + id + ", score=" + score + ", votedBy=" + votedBy + ", playedCard=" + playedCard + "]";
	}
}
