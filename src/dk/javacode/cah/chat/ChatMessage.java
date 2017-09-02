package dk.javacode.cah.chat;

import java.io.Serializable;
import java.util.Date;

import dk.javacode.cah.model.User;

public class ChatMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private User sender;
	private String text;
	private Date timestamp;

	public ChatMessage() {
	}

	public ChatMessage(User sender, String text) {
		super();
		this.sender = sender;
		this.text = text;
		this.timestamp = new Date();
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public long getTime() {
		return timestamp.getTime();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
		ChatMessage other = (ChatMessage) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ChatMessage [id=" + id + ", sender=" + sender + ", text=" + text + ", timestamp=" + timestamp + "]";
	}
}
