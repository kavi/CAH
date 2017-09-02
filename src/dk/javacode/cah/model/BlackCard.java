package dk.javacode.cah.model;

import java.util.Date;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import dk.javacode.srsm.annotations.Column;
import dk.javacode.srsm.annotations.Table;

@Table(name = "black_card")
public class BlackCard {
	@Column(name = "id", primaryKey = true)
	private Integer id;

	@Column(name = "text")
	private String text;

	@Column(name = "cards_to_pick")
	@Min(1)
	@Max(4)
	private int cardsToPick;

	@Column(name = "cards_to_draw")
	@Min(0)
	@Max(3)
	private int cardsToDraw;

	@Column(name = "disabled")
	private boolean disabled;

	@Column(name = "safe_for_work")
	private boolean safeForWork;

	@Column(name = "created")
	private Date created;

	@Column(name = "parent_id")
	private Integer parentId;

	@Column(name = "deck_id")
	private int deckId;

	@Column(name = "changed_by", columnReference = "id")
	private User changedBy;

	public BlackCard() {
		super();
	}

	public BlackCard(String text, int cardsToPick, int cardsToDraw) {
		super();
		this.text = text;
		this.cardsToPick = cardsToPick;
		this.cardsToDraw = cardsToDraw;
	}

	public BlackCard(String text, int cardsToPick, int cardsToDraw, boolean safeForWork, int deckId) {
		super();
		this.text = text;
		this.cardsToPick = cardsToPick;
		this.cardsToDraw = cardsToDraw;
		this.safeForWork = safeForWork;
		this.deckId = deckId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public int getCardsToPick() {
		return cardsToPick;
	}

	public void setCardsToPick(int cardsToPick) {
		this.cardsToPick = cardsToPick;
	}

	public int getCardsToDraw() {
		return cardsToDraw;
	}

	public void setCardsToDraw(int cardsToDraw) {
		this.cardsToDraw = cardsToDraw;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isSafeForWork() {
		return safeForWork;
	}

	public void setSafeForWork(boolean safeForWork) {
		this.safeForWork = safeForWork;
	}

	public User getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(User changedBy) {
		this.changedBy = changedBy;
	}

	public int getDeckId() {
		return deckId;
	}

	public void setDeckId(int deckId) {
		this.deckId = deckId;
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
		BlackCard other = (BlackCard) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BlackCard [id=" + id + ", deckId=" + deckId + ", text=" + text + ", cardsToPick=" + cardsToPick + ", cardsToDraw=" + cardsToDraw
				+ ", disabled=" + disabled + ", lastChanged=" + created + ", parentId=" + parentId + "]";
	}

}
