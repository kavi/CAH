package dk.javacode.cah.model;

import java.util.Date;

import dk.javacode.srsm.annotations.Column;
import dk.javacode.srsm.annotations.Table;

@Table(name = "white_card")
public class WhiteCard {
	@Column(name = "id", primaryKey = true)
	private Integer id;

	@Column(name = "text")
	private String text;

	@Column(name = "disabled")
	private boolean disabled;

	@Column(name = "parent_id")
	private Integer parentId;

	@Column(name = "created")
	private Date created;

	@Column(name = "safe_for_work")
	private boolean safeForWork;

	@Column(name = "deck_id")
	private int deckId;

	@Column(name = "changed_by", columnReference = "id")
	private User changedBy;

	public WhiteCard() {
		super();
	}

	public WhiteCard(String text) {
		super();
		this.text = text;
	}

	public WhiteCard(String text, boolean safeForWork, int deckId) {
		super();
		this.text = text;
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date lastChanged) {
		this.created = lastChanged;
	}

	public int getDeckId() {
		return deckId;
	}

	public void setDeckId(int deckId) {
		this.deckId = deckId;
	}

	public User getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(User changedBy) {
		this.changedBy = changedBy;
	}

	@Override
	public String toString() {
		return "WhiteCard [id=" + id + ", text=" + text + ", disabled=" + disabled + ", safeForWork=" + safeForWork + ", parentId=" + parentId
				+ ", created=" + created + "]";
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
		WhiteCard other = (WhiteCard) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
