package dk.javacode.cah.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dk.javacode.srsm.annotations.Column;
import dk.javacode.srsm.annotations.Table;

@Table(name = "question")
public class ActiveBlackCard {
	@Column(name = "id", primaryKey = true)
	private Integer id;

	@Column(name = "active")
	private boolean active;

	@Column(name = "black_card_id", columnReference = "id")
	private BlackCard card;

	@Column(name = "game_id", columnReference = "id")
	private Game game;

	private List<Answer> answers = new ArrayList<Answer>();

	public ActiveBlackCard() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public BlackCard getCard() {
		return card;
	}

	public void setCard(BlackCard card) {
		this.card = card;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setAnswers(List<Answer> playedCards) {
		this.answers = playedCards;
	}

	public void addAnswer(Answer ans) {
		answers.add(ans);
	}

	public void removeAnswer(Answer ans) {
		answers.remove(ans);
	}

	public List<Answer> getAnswers() {
		return Collections.unmodifiableList(answers);
	}

	public List<Answer> calculateWinnerAnswers() {
		int highestScore = 0;
		List<Answer> highest = new ArrayList<Answer>();
		for (Answer c : answers) {
			int score = c.getScore();
			if (score == highestScore) {
				highest.add(c);
			} else if (score > highestScore) {
				highestScore = score;
				highest.clear();
				highest.add(c);
			}
		}
		return highest;
	}

	@Override
	public String toString() {
		return "Question [id=" + id + ", active=" + active + ", card=" + card + "]";
	}
}
