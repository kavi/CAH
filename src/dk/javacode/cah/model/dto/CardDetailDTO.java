package dk.javacode.cah.model.dto;

import java.util.List;

public class CardDetailDTO<E> {

	private E card;
	private List<E> revisions;

	public CardDetailDTO() {
	}

	public CardDetailDTO(E card) {
		super();
		this.card = card;
	}

	public E getCard() {
		return card;
	}

	public void setCard(E card) {
		this.card = card;
	}

	public List<E> getRevisions() {
		return revisions;
	}

	public void setRevisions(List<E> revisions) {
		this.revisions = revisions;
	}

	@Override
	public String toString() {
		return "CardDetailDTO [card=" + card + ", revisions=" + revisions + "]";
	}

}
