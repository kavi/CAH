package dk.javacode.cah.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import dk.javacode.srsm.annotations.Column;
import dk.javacode.srsm.annotations.Table;

@Table(name = "deck")
public class Deck {

	@Column(primaryKey = true, name = "id")
	private Integer id;

	@NotNull
	@Column(name = "language_id", columnReference = "id")
	private DeckLanguage language;

	@NotNull
	@Column(name = "owner_id", columnReference = "id")
	private User owner;

	@Size(min = 3, max = 64)
	@Column(name = "name")
	private String name;

	private List<WhiteCard> whiteCards;
	private List<BlackCard> blackCards;

	public Deck() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public DeckLanguage getLanguage() {
		return language;
	}

	public void setLanguage(DeckLanguage language) {
		this.language = language;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<WhiteCard> getWhiteCards() {
		return whiteCards;
	}

	public void setWhiteCards(List<WhiteCard> whitecards) {
		this.whiteCards = whitecards;
	}

	public List<BlackCard> getBlackCards() {
		return blackCards;
	}

	public void setBlackCards(List<BlackCard> blackcards) {
		this.blackCards = blackcards;
	}

	@Override
	public String toString() {
		return "Deck [id=" + id + ", language=" + language + ", name=" + name + "]";
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
		Deck other = (Deck) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
